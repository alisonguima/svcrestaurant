package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.user.User;

public interface UserUseCase {

  User createUser(User user);
  void updateUser(Long userId, User user);
  void updatePassword(Long userId, String currentPassword, String newPassword);
  void assignUserType(Long userId, Long userTypeId);
  void deleteUser(Long userId);
  User getUser(Long userId);
}
