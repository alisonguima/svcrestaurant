package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private MenuItemService menuItemService;

  @BeforeEach
  void setUp() {
    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");

  private Restaurant createTestRestaurant(Long id) {
    return Restaurant.builder()
        .id(id)
        .name("Casa do Chef")
        .address("Rua A, 123")
        .cuisineType("Brasileira")
        .openingHours("10:00-22:00")
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
        .restaurant(Restaurant.builder().id(restaurantId).build())
        .lastUpdateAt(NOW)
        .build();
  }

  @Test
  void shouldCreateMenuItemSuccessfully() {
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));
    when(menuItemPersistencePort.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MenuItem result = menuItemService.createMenuItem(5L, MenuItem.builder()
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

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.createMenuItem(999L, MenuItem.builder()
            .name("Picanha")
            .description("Picanha na brasa")
            .price(BigDecimal.valueOf(79.9))
            .build()));

    assertEquals(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getCode());
    assertEquals(ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getMessage());
  }

  @Test
  void shouldUpdateMenuItemSuccessfully() {
    Restaurant restaurant = createTestRestaurant(5L);
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

    MenuItem result = menuItemService.updateMenuItem(5L, 1L, updateData);

    assertEquals("Picanha Premium", result.getName());
    assertEquals("Picanha premium na brasa", result.getDescription());
    assertEquals(BigDecimal.valueOf(99.9), result.getPrice());
    assertEquals(false, result.getOnlyAtRestaurant());
    verify(menuItemPersistencePort).findById(1L);
    verify(menuItemPersistencePort).save(any(MenuItem.class));
  }

  @Test
  void shouldUpdateMenuItemWithPartialData() {
    Restaurant restaurant = createTestRestaurant(5L);
    MenuItem existingMenuItem = createTestMenuItem(1L, 5L, "Picanha");
    MenuItem updateData = MenuItem.builder()
        .name("Picanha Atualizada")
        .build();

    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(existingMenuItem));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(menuItemPersistencePort.save(any(MenuItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    MenuItem result = menuItemService.updateMenuItem(5L, 1L, updateData);

    ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
    verify(menuItemPersistencePort).save(captor.capture());
    MenuItem savedMenuItem = captor.getValue();
    assertEquals("Picanha Atualizada", savedMenuItem.getName());
    assertEquals("Descrição do prato", savedMenuItem.getDescription());
    assertEquals(BigDecimal.valueOf(79.9), savedMenuItem.getPrice());
  }

  @Test
  void shouldGetMenuItemSuccessfully() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    MenuItem result = menuItemService.getMenuItem(5L, 1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Picanha", result.getName());
    assertEquals(5L, result.getRestaurant().getId());
    verify(menuItemPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentMenuItem() {
    when(menuItemPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.getMenuItem(5L, 999L));

    assertEquals(ErrorCode.MENU_ITEM_NOT_FOUND, exception.getCode());
    assertTrue(exception.getMessage().contains("999"));
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemWithWrongRestaurant() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.getMenuItem(10L, 1L));

    assertEquals(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getCode());
    assertEquals(ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemWithNullRestaurant() {
    MenuItem menuItem = MenuItem.builder()
        .id(1L)
        .name("Picanha")
        .restaurant(null)
        .build();
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.getMenuItem(5L, 1L));

    assertEquals(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldGetMenuItemsByRestaurantSuccessfully() {
    MenuItem menuItem1 = createTestMenuItem(1L, 5L, "Picanha");
    MenuItem menuItem2 = createTestMenuItem(2L, 5L, "Costela");
    List<MenuItem> menuItems = Arrays.asList(menuItem1, menuItem2);

    when(restaurantPersistencePort.findById(5L)).thenReturn(Optional.of(createTestRestaurant(5L)));
    when(menuItemPersistencePort.findByRestaurantId(5L)).thenReturn(menuItems);

    List<MenuItem> result = menuItemService.getMenuItemsByRestaurant(5L);

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

    List<MenuItem> result = menuItemService.getMenuItemsByRestaurant(5L);

    assertTrue(result.isEmpty());
    verify(restaurantPersistencePort).findById(5L);
  }

  @Test
  void shouldThrowExceptionWhenGettingMenuItemsByNonExistentRestaurant() {
    when(restaurantPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.getMenuItemsByRestaurant(999L));

    assertEquals(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldDeleteMenuItemSuccessfully() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    menuItemService.deleteMenuItem(5L, 1L);

    verify(menuItemPersistencePort).findById(1L);
    verify(menuItemPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentMenuItem() {
    when(menuItemPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.deleteMenuItem(5L, 999L));

    assertEquals(ErrorCode.MENU_ITEM_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenDeletingMenuItemWithWrongRestaurant() {
    MenuItem menuItem = createTestMenuItem(1L, 5L, "Picanha");
    when(menuItemPersistencePort.findById(1L)).thenReturn(Optional.of(menuItem));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> menuItemService.deleteMenuItem(10L, 1L));

    assertEquals(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, exception.getCode());
  }
}
