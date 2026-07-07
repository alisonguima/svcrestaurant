package com.techchallenge.restaurant.application.port.input.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

public interface GetMenuItemPort {
  MenuItem execute(Long restaurantId, Long menuItemId);
}
