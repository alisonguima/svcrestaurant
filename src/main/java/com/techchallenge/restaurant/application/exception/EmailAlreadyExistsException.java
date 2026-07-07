package com.techchallenge.restaurant.application.exception;

public class EmailAlreadyExistsException extends BaseException {
  public EmailAlreadyExistsException() {
    super("EMAIL_ALREADY_EXISTS", "Email already in use");
  }
}
