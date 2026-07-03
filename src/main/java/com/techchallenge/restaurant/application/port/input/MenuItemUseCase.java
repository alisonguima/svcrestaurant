package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

import java.util.List;

public interface MenuItemUseCase {

  MenuItem createMenuItem(Long restaurantId, MenuItem menuItem);
  MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem menuItem);
  MenuItem getMenuItem(Long restaurantId, Long menuItemId);
  List<MenuItem> getMenuItemsByRestaurant(Long restaurantId);
  void deleteMenuItem(Long restaurantId, Long menuItemId);
}
