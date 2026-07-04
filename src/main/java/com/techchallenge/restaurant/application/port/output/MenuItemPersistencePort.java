package com.techchallenge.restaurant.application.port.output;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemPersistencePort {

  MenuItem save(MenuItem menuItem);
  Optional<MenuItem> findById(Long id);
  List<MenuItem> findByRestaurantId(Long restaurantId);
  boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);
  void deleteById(Long id);
}
