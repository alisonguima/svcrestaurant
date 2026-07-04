package com.techchallenge.restaurant.adapter.input.validation;

import com.techchallenge.restaurant.application.exception.ApiConstants;

public final class InputValidationConstants {

  private InputValidationConstants() {}

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

  public static final String USER_TYPE_NAME_REQUIRED = "User type name is required";
  public static final String USER_TYPE_NAME_SIZE = "User type name must be between 2 and 100 characters";
  public static final String USER_TYPE_ALLOWED_NAMES =
      ApiConstants.USER_TYPE_DONO_RESTAURANTE + "|" + ApiConstants.USER_TYPE_CLIENTE;

  public static final String RESTAURANT_NAME_REQUIRED = "Restaurant name is required";
  public static final String RESTAURANT_ADDRESS_REQUIRED = "Restaurant address is required";
  public static final String RESTAURANT_CUISINE_REQUIRED = "Restaurant cuisine type is required";
  public static final String RESTAURANT_OPENING_HOURS_REQUIRED = "Restaurant opening hours is required";
  public static final String RESTAURANT_OWNER_REQUIRED = "Restaurant owner is required";

  public static final String MENU_ITEM_NAME_REQUIRED = "Menu item name is required";
  public static final String MENU_ITEM_DESCRIPTION_REQUIRED = "Menu item description is required";
  public static final String MENU_ITEM_PRICE_REQUIRED = "Menu item price is required";
  public static final String MENU_ITEM_PRICE_INVALID = "Menu item price must be greater than zero";
  public static final String MENU_ITEM_PHOTO_PATH_REQUIRED = "Menu item photo path is required";
}
