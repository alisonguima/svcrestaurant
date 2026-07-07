package com.techchallenge.restaurant.application.exception;

public class LoginAlreadyExistsException extends BaseException {
  public LoginAlreadyExistsException() {
    super("LOGIN_ALREADY_EXISTS", "Login already in use");
  }
}
