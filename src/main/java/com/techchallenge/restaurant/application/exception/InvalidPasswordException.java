package com.techchallenge.restaurant.application.exception;

public class InvalidPasswordException extends BaseException {
  public InvalidPasswordException() {
    super("INVALID_PASSWORD", "Current password is incorrect");
  }
}
