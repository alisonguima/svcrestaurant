package com.techchallenge.restaurant.application.exception;

public class ApiConstants {

  private ApiConstants() {}

  public static final String INVALID_PASSWORD = "Current password is incorrect";
  public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

  public static final String USER_TYPE_INVALID_NAME = "User type must be 'Dono de Restaurante' or 'Cliente'";
  public static final String USER_TYPE_ALREADY_EXISTS = "User type already exists";
  public static final String USER_TYPE_NOT_FOUND_WITH_ID = "User type not found with id: ";
  public static final String USER_TYPE_IN_USE = "User type is in use";
  public static final String USER_TYPE_DONO_RESTAURANTE = "Dono de Restaurante";
  public static final String USER_TYPE_CLIENTE = "Cliente";

  public static final String EMAIL_ALREADY_EXISTS = "Email already in use";
  public static final String LOGIN_ALREADY_EXISTS = "Login already in use";

  public static final String RESTAURANT_NOT_FOUND_WITH_ID = "Restaurant not found with id: ";
  public static final String RESTAURANT_ALREADY_EXISTS = "Restaurant already exists";
  public static final String RESTAURANT_OWNER_NOT_FOUND = "Restaurant owner not found";
  public static final String RESTAURANT_OWNER_UNAUTHORIZED = "Only users of type 'Dono de Restaurante' can create a restaurant";

  public static final String MENU_ITEM_NOT_FOUND_WITH_ID = "Menu item not found with id: ";
  public static final String MENU_ITEM_ALREADY_EXISTS = "Menu item already exists";
  public static final String MENU_ITEM_RESTAURANT_NOT_FOUND = "Restaurant for menu item not found";
}
