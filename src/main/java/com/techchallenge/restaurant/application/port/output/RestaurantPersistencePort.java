package com.techchallenge.restaurant.application.port.output;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantPersistencePort {

  Restaurant save(Restaurant restaurant);
  Optional<Restaurant> findById(Long id);
  List<Restaurant> findAll();
  boolean existsByNameIgnoreCase(String name);
  void deleteById(Long id);
}
