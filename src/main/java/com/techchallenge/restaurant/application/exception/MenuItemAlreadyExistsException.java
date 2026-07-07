package com.techchallenge.restaurant.application.exception;

public class MenuItemAlreadyExistsException extends BaseException {
  public MenuItemAlreadyExistsException() {
    super("MENU_ITEM_ALREADY_EXISTS", "Menu item already exists");
  }
}
