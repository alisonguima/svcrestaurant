package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.mapper.MenuItemDomainMapper;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
public class MenuItemService implements MenuItemUseCase {

  private static final Logger log = LoggerFactory.getLogger(MenuItemService.class);

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final RestaurantPersistencePort restaurantPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  @Override
  public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
    return transactionPort.execute(() -> {
      ConflictValidatorUtils.throwIfExists(
          menuItemPersistencePort.existsByNameIgnoreCaseAndRestaurantId(menuItem.getName(), restaurantId),
          "createMenuItem - Menu item already exists: name={}", menuItem.getName(),
          ErrorCode.MENU_ITEM_ALREADY_EXISTS,
          ApiConstants.MENU_ITEM_ALREADY_EXISTS);

      Restaurant restaurant = getRestaurant(restaurantId);
      return menuItemPersistencePort.save(
          MenuItemDomainMapper.INSTANCE.prepareForCreate(menuItem, restaurant, dateTimeProviderPort.nowUtc()));
    });
  }

  @Override
  public MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem menuItem) {
    return transactionPort.execute(() -> {
      MenuItem existing = getMenuItem(restaurantId, menuItemId);
      return menuItemPersistencePort.save(
          MenuItemDomainMapper.INSTANCE.mergeForUpdate(menuItem, existing, dateTimeProviderPort.nowUtc()));
    });
  }

  @Override
  public MenuItem getMenuItem(Long restaurantId, Long menuItemId) {
    return transactionPort.executeReadOnly(() -> {
      MenuItem item = menuItemPersistencePort.findById(menuItemId)
          .orElseThrow(() -> new DefaultException(ErrorCode.MENU_ITEM_NOT_FOUND, ApiConstants.MENU_ITEM_NOT_FOUND_WITH_ID + menuItemId));
      if (item.getRestaurant() == null || !restaurantId.equals(item.getRestaurant().getId())) {
        throw new DefaultException(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND);
      }
      return item;
    });
  }

  @Override
  public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
    return transactionPort.executeReadOnly(() -> {
      getRestaurant(restaurantId);
      return menuItemPersistencePort.findByRestaurantId(restaurantId);
    });
  }

  @Override
  public void deleteMenuItem(Long restaurantId, Long menuItemId) {
    transactionPort.executeVoid(() -> {
      getMenuItem(restaurantId, menuItemId);
      menuItemPersistencePort.deleteById(menuItemId);
    });
  }

  private Restaurant getRestaurant(Long restaurantId) {
    return restaurantPersistencePort.findById(restaurantId)
        .orElseThrow(() -> new DefaultException(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, ApiConstants.MENU_ITEM_RESTAURANT_NOT_FOUND));
  }
}
