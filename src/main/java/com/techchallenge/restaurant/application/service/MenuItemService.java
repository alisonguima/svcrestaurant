package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MenuItemService implements MenuItemUseCase {

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final RestaurantPersistencePort restaurantPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;

  @Override
  public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
    menuItem.setRestaurant(getRestaurant(restaurantId));
    menuItem.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    return menuItemPersistencePort.save(menuItem);
  }

  @Override
  public MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem menuItem) {
    MenuItem existing = getMenuItem(restaurantId, menuItemId);
    existing.setName(Optional.ofNullable(menuItem.getName()).orElse(existing.getName()));
    existing.setDescription(Optional.ofNullable(menuItem.getDescription()).orElse(existing.getDescription()));
    existing.setPrice(Optional.ofNullable(menuItem.getPrice()).orElse(existing.getPrice()));
    existing.setOnlyAtRestaurant(Optional.ofNullable(menuItem.getOnlyAtRestaurant()).orElse(existing.getOnlyAtRestaurant()));
    existing.setPhotoPath(Optional.ofNullable(menuItem.getPhotoPath()).orElse(existing.getPhotoPath()));
    existing.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    return menuItemPersistencePort.save(existing);
  }

  @Override
  public MenuItem getMenuItem(Long restaurantId, Long menuItemId) {
    MenuItem item = menuItemPersistencePort.findById(menuItemId)
        .orElseThrow(() -> new DefaultException(ErrorCode.MENU_ITEM_NOT_FOUND, ApiConstants.MENU_ITEM_NOT_FOUND_WITH_ID + menuItemId));
    if (item.getRestaurant() == null || !restaurantId.equals(item.getRestaurant().getId())) {
      throw new DefaultException(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND);
    }
    return item;
  }

  @Override
  public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
    getRestaurant(restaurantId);
    return menuItemPersistencePort.findByRestaurantId(restaurantId);
  }

  @Override
  public void deleteMenuItem(Long restaurantId, Long menuItemId) {
    getMenuItem(restaurantId, menuItemId);
    menuItemPersistencePort.deleteById(menuItemId);
  }

  private Restaurant getRestaurant(Long restaurantId) {
    return restaurantPersistencePort.findById(restaurantId)
        .orElseThrow(() -> new DefaultException(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND));
  }
}
