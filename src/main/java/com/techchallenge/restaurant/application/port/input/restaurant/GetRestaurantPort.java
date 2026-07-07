package com.techchallenge.restaurant.application.port.input.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

public interface GetRestaurantPort {
  Restaurant execute(Long restaurantId);
}
