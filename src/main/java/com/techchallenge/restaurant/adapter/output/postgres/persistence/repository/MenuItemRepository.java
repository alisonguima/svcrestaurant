package com.techchallenge.restaurant.adapter.output.postgres.persistence.repository;

import com.techchallenge.restaurant.adapter.output.postgres.model.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

  List<MenuItemEntity> findAllByRestaurantId(Long restaurantId);
  boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);
}
