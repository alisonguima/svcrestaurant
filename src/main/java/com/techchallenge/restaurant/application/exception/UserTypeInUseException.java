package com.techchallenge.restaurant.application.exception;

public class UserTypeInUseException extends BaseException {
  public UserTypeInUseException() {
    super("USER_TYPE_IN_USE", "User type is in use");
  }
}
