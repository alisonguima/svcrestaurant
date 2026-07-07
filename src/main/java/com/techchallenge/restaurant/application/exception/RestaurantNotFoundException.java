package com.techchallenge.restaurant.application.exception;

public class RestaurantNotFoundException extends BaseException {
  public RestaurantNotFoundException(Long id) {
    super("RESTAURANT_NOT_FOUND", "Restaurant not found with id: " + id);
  }
}
