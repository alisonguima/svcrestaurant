package com.techchallenge.restaurant.application.exception;

public class UserTypeAlreadyExistsException extends BaseException {
  public UserTypeAlreadyExistsException() {
    super("USER_TYPE_ALREADY_EXISTS", "User type already exists");
  }
}
