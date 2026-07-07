package com.techchallenge.restaurant.application.exception;

public class RestaurantOwnerUnauthorizedException extends BaseException {
  public RestaurantOwnerUnauthorizedException() {
    super("RESTAURANT_OWNER_UNAUTHORIZED", "Only users of type 'Dono' can create a restaurant");
  }
}
