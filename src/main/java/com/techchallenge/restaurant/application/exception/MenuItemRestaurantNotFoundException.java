package com.techchallenge.restaurant.application.exception;

public class MenuItemRestaurantNotFoundException extends BaseException {
  public MenuItemRestaurantNotFoundException() {
    super("MENU_ITEM_RESTAURANT_NOT_FOUND", "Restaurant for menu item not found");
  }
}
