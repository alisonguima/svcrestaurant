package com.techchallenge.restaurant.application.exception;

public class MenuItemOwnerUnauthorizedException extends BaseException {
  public MenuItemOwnerUnauthorizedException() {
    super("MENU_ITEM_OWNER_UNAUTHORIZED", "Only the restaurant owner can manage menu items");
  }
}
