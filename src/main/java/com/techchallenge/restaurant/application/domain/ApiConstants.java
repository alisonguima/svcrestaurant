package com.techchallenge.restaurant.application.domain;

public class ApiConstants {

  private ApiConstants() {}

  public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

  public static final String NAME_REQUIRED = "Name is required";
  public static final String NAME_SIZE = "Name must be between 2 and 100 characters";

  public static final String EMAIL_REQUIRED = "Email is required";
  public static final String EMAIL_INVALID = "Email must be valid";

  public static final String LOGIN_REQUIRED = "Login is required";
  public static final String LOGIN_SIZE = "Login must be between 3 and 50 characters";

  public static final String PASSWORD_REQUIRED = "Password is required";
  public static final String CUR_PASSWORD_REQUIRED = "Current password is required";
  public static final String NEW_PASSWORD_REQUIRED = "New password is required";
  public static final String PASSWORD_INVALID = "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*?&)";
  public static final String INVALID_PASSWORD = "Current password is incorrect";
  public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

  public static final String USER_TYPE_REQUIRED = "User type is required";
  public static final String USER_TYPE_NAME_REQUIRED = "User type name is required";
  public static final String USER_TYPE_NAME_SIZE = "User type name must be between 2 and 100 characters";
  public static final String USER_TYPE_ALLOWED_NAMES = "Dono de Restaurante|Cliente";
  public static final String USER_TYPE_INVALID_NAME = "User type must be 'Dono de Restaurante' or 'Cliente'";
  public static final String USER_TYPE_ALREADY_EXISTS = "User type already exists";
  public static final String USER_TYPE_NOT_FOUND_WITH_ID = "User type not found with id: ";
  public static final String USER_TYPE_IN_USE = "User type is in use";

  public static final String EMAIL_ALREADY_EXISTS = "Email already in use";
  public static final String LOGIN_ALREADY_EXISTS = "Login already in use";

  public static final String RESTAURANT_NAME_REQUIRED = "Restaurant name is required";
  public static final String RESTAURANT_ADDRESS_REQUIRED = "Restaurant address is required";
  public static final String RESTAURANT_CUISINE_REQUIRED = "Restaurant cuisine type is required";
  public static final String RESTAURANT_OPENING_HOURS_REQUIRED = "Restaurant opening hours is required";
  public static final String RESTAURANT_OWNER_REQUIRED = "Restaurant owner is required";
  public static final String RESTAURANT_NOT_FOUND_WITH_ID = "Restaurant not found with id: ";
  public static final String RESTAURANT_ALREADY_EXISTS = "Restaurant already exists";
  public static final String RESTAURANT_OWNER_NOT_FOUND = "Restaurant owner not found";

  public static final String MENU_ITEM_NAME_REQUIRED = "Menu item name is required";
  public static final String MENU_ITEM_DESCRIPTION_REQUIRED = "Menu item description is required";
  public static final String MENU_ITEM_PRICE_REQUIRED = "Menu item price is required";
  public static final String MENU_ITEM_PRICE_INVALID = "Menu item price must be greater than zero";
  public static final String MENU_ITEM_PHOTO_PATH_REQUIRED = "Menu item photo path is required";
  public static final String MENU_ITEM_NOT_FOUND_WITH_ID = "Menu item not found with id: ";
  public static final String MENU_ITEM_ALREADY_EXISTS = "Menu item already exists";
  public static final String MENU_ITEM_RESTAURANT_NOT_FOUND = "Restaurant for menu item not found";

}
