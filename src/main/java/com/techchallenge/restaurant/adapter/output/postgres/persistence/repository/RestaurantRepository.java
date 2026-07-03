package com.techchallenge.restaurant.adapter.output.postgres.persistence.repository;

import com.techchallenge.restaurant.adapter.output.postgres.model.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  boolean existsByNameIgnoreCase(String name);
}
