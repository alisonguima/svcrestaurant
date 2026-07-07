package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.usecase.menuitem.CreateMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.DeleteMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.GetMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.GetMenuItemsByRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.UpdateMenuItemUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
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
class MenuItemServiceTest {

  @Mock
  private MenuItemPersistencePort menuItemPersistencePort;

  @Mock
  private RestaurantPersistencePort restaurantPersistencePort;

  @Mock
  private DateTimeProviderPort dateTimeProviderPort;

  @Mock
  private TransactionPort transactionPort;

  private CreateMenuItemUseCase createMenuItemUseCase;
  private UpdateMenuItemUseCase updateMenuItemUseCase;
  private GetMenuItemUseCase getMenuItemUseCase;
  private GetMenuItemsByRestaurantUseCase getMenuItemsByRestaurantUseCase;
  private DeleteMenuItemUseCase deleteMenuItemUseCase;

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");
  private static final Long OWNER_ID = 1L;
  private static final User OWNER = User.builder().id(OWNER_ID).name("Owner").build();

  @BeforeEach
  void setUp() {
    createMenuItemUseCase = new CreateMenuItemUseCase(menuItemPersistencePort,
        restaurantPersistencePort, dateTimeProviderPort, transactionPort);
    updateMenuItemUseCase = new UpdateMenuItemUseCase(menuItemPersistencePort,
        dateTimeProviderPort, transactionPort);
    getMenuItemUseCase = new GetMenuItemUseCase(menuItemPersistencePort, transactionPort);
    getMenuItemsByRestaurantUseCase = new GetMenuItemsByRestaurantUseCase(menuItemPersistencePort,
        restaurantPersistencePort, transactionPort);
    deleteMenuItemUseCase = new DeleteMenuItemUseCase(menuItemPersistencePort, transactionPort);

    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private Restaurant createTestRestaurant(Long id) {
    return Restaurant.builder()
        .id(id)
        .name("Casa do Chef")
        .address("Rua A, 123")
        .cuisineType("Brasileira")
        .openingHours("10:00-22:00")
        .owner(OWNER)
        .lastUpdateAt(NOW)
        .build();
  }

  private MenuItem createTestMenuItem(Long id, Long restaurantId, String name) {
    return MenuItem.builder()
        .id(id)
        .name(name)
        .description("Descrição do prato")
        .price(BigDecimal.valueOf(79.9))
        .onlyAtRestaurant(true)
        .photoPath("/tmp/prato.jpg")
        .restaurant(createTestRestaurant(restaurantId))
        .lastUpdateAt(NOW)
        .build();
  }

  @Test
  void shouldCreateMenuItemSuccessfully() {
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));
    when(menuItemPersistencePort.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MenuItem result = createMenuItemUseCase.execute(5L, OWNER_ID, MenuItem.builder()
        .name("Picanha")
        .description("Picanha na brasa")
        .price(BigDecimal.valueOf(79.9))
        .onlyAtRestaurant(true)
        .photoPath("/tmp/picanha.jpg")
        .build());

    assertEquals(5L, result.getRestaurant().getId());
    assertEquals(NOW, result.getLastUpdateAt());
    assertEquals("Picanha", result.getName());
    assertEquals(BigDecimal.valueOf(79.9), result.getPrice());
    verify(restaurantPersistencePort).findById(5L);
    verify(menuItemPersistencePort).save(any(MenuItem.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingMenuItemWithNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(MenuItemRestaurantNotFoundException.class,
        () -> createMenuItemUseCase.execute(999L, OWNER_ID, MenuItem.builder()
            .name("Picanha")
            .description("Picanha na brasa")
            .price(BigDecimal.valueOf(79.9))
            .build()));
  }

  @Test
  void shouldThrowExceptionWhenCreatingMenuItemByUnauthorizedOwner() {
    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));

    assertThrows(MenuItemOwnerUnauthorizedException.class,
        () -> createMenuItemUseCase.execute(5L, 999L, MenuItem.builder()
            .name("Picanha")
            .description("Picanha na brasa")
            .price(BigDecimal.valueOf(79.9))
            .build()));
  }

  @Test
  void shouldUpdateMenuItemSuccessfully() {
    MenuItem existingMenuItem = createTestMenuItem(1L, 5L, "Picanha");
    MenuItem updateData = MenuItem.builder()
        .name("Picanha Premium")
        .description("Picanha premium na brasa")
        .price(BigDecimal.valueOf(99.9))
        .onlyAtRestaurant(false)
        .photoPath("/tmp/picanha_premium.jpg")
        .build();

    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(existingMenuItem));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(menuItemPersistencePort.save(any(MenuItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    MenuItem result = updateMenuItemUseCase.execute(5L, 1L, OWNER_ID, updateData);

    assertEquals("Picanha Premium", result.getName());
    assertEquals("Picanha premium na brasa", result.getDescription());
    assertEquals(BigDecimal.valueOf(99.9), result.getPrice());
    assertEquals(false, result.getOnlyAtRestaurant());
    verify(menuItemPersistencePort).findById(1L);
    verify(menuItemPersistencePort).save(any(MenuItem.class));
  }

  @Test
  void shouldUpdateMenuItemWithPartialData() {
    MenuItem existingMenuItem = createTestMenuItem(1L, 5L, "Picanha");
    MenuItem updateData = MenuItem.builder()
        .name("Picanha Atualizada")
        .build();

    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(existingMenuItem));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(menuItemPersistencePort.save(any(MenuItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    updateMenuItemUseCase.execute(5L, 1L, OWNER_ID, updateData);

    ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
    verify(menuItemPersistencePort).save(captor.capture());
    MenuItem savedMenuItem = captor.getValue();
    assertEquals("Picanha Atualizada", savedMenuItem.getName());
    assertEquals("Descrição do prato", savedMenuItem.getDescription());
    assertEquals(BigDecimal.valueOf(79.9), savedMenuItem.getPrice());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingMenuItemByUnauthorizedOwner() {
    MenuItem existingMenuItem = createTestMenuItem(1L, 5L, "Picanha");

    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(existingMenuItem));

    assertThrows(MenuItemOwnerUnauthorizedException.class,
        () -> updateMenuItemUseCase.execute(5L, 1L, 999L, MenuItem.builder().name("x").build()));
  }

  @Test
  void shouldGetMenuItemSuccessfully() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    MenuItem result = getMenuItemUseCase.execute(5L, 1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Picanha", result.getName());
    assertEquals(5L, result.getRestaurant().getId());
    verify(menuItemPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentMenuItem() {
    when(menuItemPersistencePort.findById(999L)).thenReturn(Optional.empty());

    MenuItemNotFoundException ex = assertThrows(MenuItemNotFoundException.class,
        () -> getMenuItemUseCase.execute(5L, 999L));

    assertTrue(ex.getMessage().contains("999"));
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemWithWrongRestaurant() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    assertThrows(MenuItemRestaurantNotFoundException.class,
        () -> getMenuItemUseCase.execute(10L, 1L));
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemWithNullRestaurant() {
    MenuItem menuItem = MenuItem.builder()
        .id(1L)
        .name("Picanha")
        .restaurant(null)
        .build();
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    assertThrows(MenuItemRestaurantNotFoundException.class,
        () -> getMenuItemUseCase.execute(5L, 1L));
  }

  @Test
  void shouldGetMenuItemsByRestaurantSuccessfully() {
    MenuItem menuItem1 = createTestMenuItem(1L, 5L, "Picanha");
    MenuItem menuItem2 = createTestMenuItem(2L, 5L, "Costela");
    List<MenuItem> menuItems = Arrays.asList(menuItem1, menuItem2);

    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));
    when(menuItemPersistencePort.findByRestaurantId(5L)).thenReturn(menuItems);

    List<MenuItem> result = getMenuItemsByRestaurantUseCase.execute(5L);

    assertEquals(2, result.size());
    assertEquals(menuItem1, result.get(0));
    assertEquals(menuItem2, result.get(1));
    verify(restaurantPersistencePort).findById(5L);
    verify(menuItemPersistencePort).findByRestaurantId(5L);
  }

  @Test
  void shouldGetEmptyListWhenNoMenuItemsExist() {
    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));
    when(menuItemPersistencePort.findByRestaurantId(5L)).thenReturn(List.of());

    List<MenuItem> result = getMenuItemsByRestaurantUseCase.execute(5L);

    assertTrue(result.isEmpty());
    verify(restaurantPersistencePort).findById(5L);
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemsByNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(MenuItemRestaurantNotFoundException.class,
        () -> getMenuItemsByRestaurantUseCase.execute(999L));
  }

  @Test
  void shouldDeleteMenuItemSuccessfully() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    deleteMenuItemUseCase.execute(5L, 1L, OWNER_ID);

    verify(menuItemPersistencePort).findById(1L);
    verify(menuItemPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentMenuItem() {
    when(menuItemPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(MenuItemNotFoundException.class,
        () -> deleteMenuItemUseCase.execute(5L, 999L, OWNER_ID));
  }

  @Test
  void shouldThrowExceptionWhenDeletingMenuItemWithWrongRestaurant() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    assertThrows(MenuItemRestaurantNotFoundException.class,
        () -> deleteMenuItemUseCase.execute(10L, 1L, OWNER_ID));
  }

  @Test
  void shouldThrowExceptionWhenDeletingMenuItemByUnauthorizedOwner() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    assertThrows(MenuItemOwnerUnauthorizedException.class,
        () -> deleteMenuItemUseCase.execute(5L, 1L, 999L));
  }
}
