package com.techchallenge.restaurant.application.port.input.user;

public interface UpdateUserPasswordPort {
  void execute(Long userId, String currentPassword, String newPassword);
}
