package com.techchallenge.restaurant.application.exception;

public class UserTypeInvalidNameException extends BaseException {
  public UserTypeInvalidNameException() {
    super("USER_TYPE_INVALID_NAME", "User type must be 'Dono' or 'Cliente'");
  }
}
