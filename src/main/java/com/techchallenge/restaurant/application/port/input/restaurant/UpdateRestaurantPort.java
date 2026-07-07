package com.techchallenge.restaurant.application.port.input.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

public interface UpdateRestaurantPort {
  Restaurant execute(Long restaurantId, Restaurant patch);
}
