package com.techchallenge.restaurant.application.port.input.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

import java.util.List;

public interface GetRestaurantsPort {
  List<Restaurant> execute();
}
