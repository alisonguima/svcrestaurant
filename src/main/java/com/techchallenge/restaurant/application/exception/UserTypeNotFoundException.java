package com.techchallenge.restaurant.application.exception;

public class UserTypeNotFoundException extends BaseException {
  public UserTypeNotFoundException(Long id) {
    super("USER_TYPE_NOT_FOUND", "User type not found with id: " + id);
  }
}
