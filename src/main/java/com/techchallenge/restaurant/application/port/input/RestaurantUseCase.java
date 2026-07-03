package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

import java.util.List;

public interface RestaurantUseCase {

  Restaurant createRestaurant(Restaurant restaurant);
  Restaurant updateRestaurant(Long restaurantId, Restaurant restaurant);
  Restaurant getRestaurant(Long restaurantId);
  List<Restaurant> getRestaurants();
  void deleteRestaurant(Long restaurantId);
}
