package com.techchallenge.restaurant.adapter.output.postgres.persistence.repository;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Long> {

  boolean existsByNameIgnoreCase(String name);
}
