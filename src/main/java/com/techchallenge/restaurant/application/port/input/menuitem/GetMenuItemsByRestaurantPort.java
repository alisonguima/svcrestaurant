package com.techchallenge.restaurant.application.port.input.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

import java.util.List;

public interface GetMenuItemsByRestaurantPort {
  List<MenuItem> execute(Long restaurantId);
}
