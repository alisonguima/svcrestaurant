package com.techchallenge.restaurant.application.port.output;

import com.techchallenge.restaurant.application.domain.user.User;

import java.util.Optional;

public interface UserPersistencePort {

  User save(User user);
  Optional<User> findById(Long id);
  boolean existsByLogin(String login);
  boolean existsByEmail(String email);
  long countByUserTypeId(Long userTypeId);
  void deleteById(Long id);

}
