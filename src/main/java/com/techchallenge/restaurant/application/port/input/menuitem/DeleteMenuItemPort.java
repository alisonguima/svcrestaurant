package com.techchallenge.restaurant.application.port.input.menuitem;

public interface DeleteMenuItemPort {
  void execute(Long restaurantId, Long menuItemId, Long ownerId);
}
