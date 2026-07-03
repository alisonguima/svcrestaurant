package com.techchallenge.restaurant.application.port.output;

import com.techchallenge.restaurant.application.domain.enums.UserType;

import java.util.List;
import java.util.Optional;

public interface UserTypePersistencePort {

  UserType save(UserType userType);
  Optional<UserType> findById(Long id);
  List<UserType> findAll();
  boolean existsByNameIgnoreCase(String name);
  void deleteById(Long id);
}
