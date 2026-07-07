package com.techchallenge.restaurant.application.exception;

public class MenuItemNotFoundException extends BaseException {
  public MenuItemNotFoundException(Long id) {
    super("MENU_ITEM_NOT_FOUND", "Menu item not found with id: " + id);
  }
}
