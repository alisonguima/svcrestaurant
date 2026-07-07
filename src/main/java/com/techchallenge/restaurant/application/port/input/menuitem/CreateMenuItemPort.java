package com.techchallenge.restaurant.application.port.input.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

public interface CreateMenuItemPort {
  MenuItem execute(Long restaurantId, Long ownerId, MenuItem menuItem);
}
