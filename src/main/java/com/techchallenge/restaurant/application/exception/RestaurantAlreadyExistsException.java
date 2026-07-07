package com.techchallenge.restaurant.application.exception;

public class RestaurantAlreadyExistsException extends BaseException {
  public RestaurantAlreadyExistsException() {
    super("RESTAURANT_ALREADY_EXISTS", "Restaurant already exists");
  }
}
