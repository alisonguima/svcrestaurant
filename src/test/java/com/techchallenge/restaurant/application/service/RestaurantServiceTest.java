package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.usecase.restaurant.CreateRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.DeleteRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.GetRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.GetRestaurantsUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.UpdateRestaurantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantServiceTest {

  @Mock
  private RestaurantPersistencePort restaurantPersistencePort;

  @Mock
  private UserPersistencePort userPersistencePort;

  @Mock
  private DateTimeProviderPort dateTimeProviderPort;

  @Mock
  private TransactionPort transactionPort;

  private CreateRestaurantUseCase createRestaurantUseCase;
  private UpdateRestaurantUseCase updateRestaurantUseCase;
  private GetRestaurantUseCase getRestaurantUseCase;
  private GetRestaurantsUseCase getRestaurantsUseCase;
  private DeleteRestaurantUseCase deleteRestaurantUseCase;

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");

  @BeforeEach
  void setUp() {
    createRestaurantUseCase = new CreateRestaurantUseCase(restaurantPersistencePort,
        userPersistencePort, dateTimeProviderPort, transactionPort);
    updateRestaurantUseCase = new UpdateRestaurantUseCase(restaurantPersistencePort,
        userPersistencePort, dateTimeProviderPort, transactionPort);
    getRestaurantUseCase = new GetRestaurantUseCase(restaurantPersistencePort, transactionPort);
    getRestaurantsUseCase = new GetRestaurantsUseCase(restaurantPersistencePort, transactionPort);
    deleteRestaurantUseCase = new DeleteRestaurantUseCase(restaurantPersistencePort, transactionPort);

    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private static final UserType DONO_USER_TYPE =
      UserType.builder().id(1L).name(UserType.DONO).build();

  private User createOwner(Long id) {
    return User.builder().id(id).name("Chef Owner").userType(DONO_USER_TYPE).build();
  }

  private User createOwnerWithType(Long id, UserType userType) {
    return User.builder().id(id).name("Chef Owner").userType(userType).build();
  }

  private Restaurant createTestRestaurant(Long id, String name, User owner) {
    return Restaurant.builder()
        .id(id)
        .name(name)
        .address("Rua A, 123")
        .cuisineType("Brasileira")
        .openingHours("10:00-22:00")
        .owner(owner)
        .lastUpdateAt(NOW)
        .build();
  }

  @Test
  void shouldCreateRestaurantSuccessfully() {
    User owner = createOwner(10L);
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.findById(10L)).thenReturn(Optional.of(owner));
    when(restaurantPersistencePort.save(any(Restaurant.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Restaurant result = createRestaurantUseCase.execute(Restaurant.builder()
        .name("Casa do Chef")
        .address("Rua A, 123")
        .cuisineType("Brasileira")
        .openingHours("10:00-22:00")
        .owner(User.builder().id(10L).build())
        .build());

    assertEquals("Casa do Chef", result.getName());
    assertEquals(NOW, result.getLastUpdateAt());
    assertEquals(10L, result.getOwner().getId());
    verify(userPersistencePort).findById(10L);
    verify(restaurantPersistencePort).save(any(Restaurant.class));
  }

  @Test
  void shouldThrowExceptionWhenOwnerIsClientType() {
    UserType clientType = UserType.builder().id(2L).name(UserType.CLIENTE).build();
    User clientOwner = createOwnerWithType(10L, clientType);
    when(userPersistencePort.findById(10L)).thenReturn(Optional.of(clientOwner));

    assertThrows(RestaurantOwnerUnauthorizedException.class,
        () -> createRestaurantUseCase.execute(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(10L).build())
            .build()));
  }

  @Test
  void shouldThrowExceptionWhenOwnerHasNoUserType() {
    User ownerWithoutType = createOwnerWithType(10L, null);
    when(userPersistencePort.findById(10L)).thenReturn(Optional.of(ownerWithoutType));

    assertThrows(RestaurantOwnerUnauthorizedException.class,
        () -> createRestaurantUseCase.execute(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(10L).build())
            .build()));
  }

  @Test
  void shouldThrowExceptionWhenCreatingRestaurantWithNonExistentOwner() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(RestaurantOwnerNotFoundException.class,
        () -> createRestaurantUseCase.execute(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(999L).build())
            .build()));
  }

  @Test
  void shouldUpdateRestaurantSuccessfully() {
    User owner = createOwner(10L);
    Restaurant existingRestaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    Restaurant updateData = Restaurant.builder()
        .name("Casa do Chef - Novo")
        .address("Rua B, 456")
        .cuisineType("Italiana")
        .openingHours("11:00-23:00")
        .build();

    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(existingRestaurant));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(restaurantPersistencePort.save(any(Restaurant.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Restaurant result = updateRestaurantUseCase.execute(1L, updateData);

    assertEquals("Casa do Chef - Novo", result.getName());
    assertEquals("Rua B, 456", result.getAddress());
    assertEquals("Italiana", result.getCuisineType());
    assertEquals("11:00-23:00", result.getOpeningHours());
    verify(restaurantPersistencePort).findById(1L);
    verify(restaurantPersistencePort).save(any(Restaurant.class));
  }

  @Test
  void shouldUpdateRestaurantWithPartialData() {
    User owner = createOwner(10L);
    Restaurant existingRestaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    Restaurant updateData = Restaurant.builder()
        .name("Casa do Chef - Novo")
        .build();

    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(existingRestaurant));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(restaurantPersistencePort.save(any(Restaurant.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    updateRestaurantUseCase.execute(1L, updateData);

    ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);
    verify(restaurantPersistencePort).save(captor.capture());
    Restaurant savedRestaurant = captor.getValue();
    assertEquals("Casa do Chef - Novo", savedRestaurant.getName());
    assertEquals("Rua A, 123", savedRestaurant.getAddress());
    assertEquals("Brasileira", savedRestaurant.getCuisineType());
  }

  @Test
  void shouldUpdateRestaurantWithNewOwner() {
    User oldOwner = createOwner(10L);
    User newOwner = User.builder().id(20L).name("New Owner").build();
    Restaurant existingRestaurant = createTestRestaurant(1L, "Casa do Chef", oldOwner);
    Restaurant updateData = Restaurant.builder()
        .owner(User.builder().id(20L).build())
        .build();

    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(existingRestaurant));
    when(userPersistencePort.findById(20L)).thenReturn(Optional.of(newOwner));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(restaurantPersistencePort.save(any(Restaurant.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    updateRestaurantUseCase.execute(1L, updateData);

    ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);
    verify(restaurantPersistencePort).save(captor.capture());
    Restaurant savedRestaurant = captor.getValue();
    assertEquals(20L, savedRestaurant.getOwner().getId());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingRestaurantWithNonExistentOwner() {
    User owner = createOwner(10L);
    Restaurant existingRestaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    Restaurant updateData = Restaurant.builder()
        .owner(User.builder().id(999L).build())
        .build();

    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(existingRestaurant));
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(RestaurantOwnerNotFoundException.class,
        () -> updateRestaurantUseCase.execute(1L, updateData));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(RestaurantNotFoundException.class,
        () -> updateRestaurantUseCase.execute(999L, Restaurant.builder()
            .name("Casa do Chef")
            .build()));
  }

  @Test
  void shouldGetRestaurantSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));

    Restaurant result = getRestaurantUseCase.execute(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Casa do Chef", result.getName());
    verify(restaurantPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    RestaurantNotFoundException ex = assertThrows(RestaurantNotFoundException.class,
        () -> getRestaurantUseCase.execute(999L));

    assertTrue(ex.getMessage().contains("999"));
  }

  @Test
  void shouldGetAllRestaurantsSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant1 = createTestRestaurant(1L, "Casa do Chef", owner);
    Restaurant restaurant2 = createTestRestaurant(2L, "Casa da Nonna", owner);
    List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

    when(restaurantPersistencePort.findAll()).thenReturn(restaurants);

    List<Restaurant> result = getRestaurantsUseCase.execute();

    assertEquals(2, result.size());
    assertEquals(restaurant1, result.get(0));
    assertEquals(restaurant2, result.get(1));
    verify(restaurantPersistencePort).findAll();
  }

  @Test
  void shouldGetEmptyListWhenNoRestaurantsExist() {
    when(restaurantPersistencePort.findAll()).thenReturn(List.of());

    List<Restaurant> result = getRestaurantsUseCase.execute();

    assertTrue(result.isEmpty());
    verify(restaurantPersistencePort).findAll();
  }

  @Test
  void shouldDeleteRestaurantSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));

    deleteRestaurantUseCase.execute(1L);

    verify(restaurantPersistencePort).findById(1L);
    verify(restaurantPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(RestaurantNotFoundException.class,
        () -> deleteRestaurantUseCase.execute(999L));
  }
}
