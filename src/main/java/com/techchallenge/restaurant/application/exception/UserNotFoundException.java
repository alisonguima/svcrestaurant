package com.techchallenge.restaurant.application.exception;

public class UserNotFoundException extends BaseException {
  public UserNotFoundException(Long id) {
    super("USER_NOT_FOUND", "User not found with id: " + id);
  }
}
