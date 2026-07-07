package com.techchallenge.restaurant.application.exception;

public class RestaurantOwnerNotFoundException extends BaseException {
  public RestaurantOwnerNotFoundException() {
    super("RESTAURANT_OWNER_NOT_FOUND", "Restaurant owner not found");
  }
}
