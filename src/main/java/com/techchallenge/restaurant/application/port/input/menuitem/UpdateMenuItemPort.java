package com.techchallenge.restaurant.application.port.input.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

public interface UpdateMenuItemPort {
  MenuItem execute(Long restaurantId, Long menuItemId, Long ownerId, MenuItem patch);
}
