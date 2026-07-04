package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private RestaurantService restaurantService;

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");

  @BeforeEach
  void setUp() {
    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private static final UserType DONO_USER_TYPE =
      UserType.builder().id(1L).name("Dono de Restaurante").build();

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

    Restaurant result = restaurantService.createRestaurant(Restaurant.builder()
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
    UserType clientType = UserType.builder().id(2L).name("Cliente").build();
    User clientOwner = createOwnerWithType(10L, clientType);
    when(userPersistencePort.findById(10L)).thenReturn(Optional.of(clientOwner));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.createRestaurant(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(10L).build())
            .build()));

    assertEquals(ErrorCode.RESTAURANT_OWNER_UNAUTHORIZED, exception.getCode());
    assertEquals(ApiConstants.RESTAURANT_OWNER_UNAUTHORIZED, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenOwnerHasNoUserType() {
    User ownerWithoutType = createOwnerWithType(10L, null);
    when(userPersistencePort.findById(10L)).thenReturn(Optional.of(ownerWithoutType));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.createRestaurant(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(10L).build())
            .build()));

    assertEquals(ErrorCode.RESTAURANT_OWNER_UNAUTHORIZED, exception.getCode());
    assertEquals(ApiConstants.RESTAURANT_OWNER_UNAUTHORIZED, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCreatingRestaurantWithNonExistentOwner() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.createRestaurant(Restaurant.builder()
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(999L).build())
            .build()));

    assertEquals(ErrorCode.RESTAURANT_OWNER_NOT_FOUND, exception.getCode());
    assertEquals(ApiConstants.RESTAURANT_OWNER_NOT_FOUND, exception.getMessage());
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

    Restaurant result = restaurantService.updateRestaurant(1L, updateData);

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

    Restaurant result = restaurantService.updateRestaurant(1L, updateData);

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

    restaurantService.updateRestaurant(1L, updateData);

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

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.updateRestaurant(1L, updateData));

    assertEquals(ErrorCode.RESTAURANT_OWNER_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.updateRestaurant(999L, Restaurant.builder()
            .name("Casa do Chef")
            .build()));

    assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldGetRestaurantSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));

    Restaurant result = restaurantService.getRestaurant(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Casa do Chef", result.getName());
    verify(restaurantPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.getRestaurant(999L));

    assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getCode());
    assertTrue(exception.getMessage().contains("999"));
  }

  @Test
  void shouldGetAllRestaurantsSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant1 = createTestRestaurant(1L, "Casa do Chef", owner);
    Restaurant restaurant2 = createTestRestaurant(2L, "Casa da Nonna", owner);
    List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

    when(restaurantPersistencePort.findAll()).thenReturn(restaurants);

    List<Restaurant> result = restaurantService.getRestaurants();

    assertEquals(2, result.size());
    assertEquals(restaurant1, result.get(0));
    assertEquals(restaurant2, result.get(1));
    verify(restaurantPersistencePort).findAll();
  }

  @Test
  void shouldGetEmptyListWhenNoRestaurantsExist() {
    when(restaurantPersistencePort.findAll()).thenReturn(List.of());

    List<Restaurant> result = restaurantService.getRestaurants();

    assertTrue(result.isEmpty());
    verify(restaurantPersistencePort).findAll();
  }

  @Test
  void shouldDeleteRestaurantSuccessfully() {
    User owner = createOwner(10L);
    Restaurant restaurant = createTestRestaurant(1L, "Casa do Chef", owner);
    when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));

    restaurantService.deleteRestaurant(1L);

    verify(restaurantPersistencePort).findById(1L);
    verify(restaurantPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> restaurantService.deleteRestaurant(999L));

    assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getCode());
  }
}
