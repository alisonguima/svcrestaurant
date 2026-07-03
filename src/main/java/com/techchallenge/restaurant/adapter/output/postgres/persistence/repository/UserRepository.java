package com.techchallenge.restaurant.adapter.output.postgres.persistence.repository;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByLogin(String login);
  boolean existsByEmail(String email);
  long countByUserTypeId(Long userTypeId);

}
