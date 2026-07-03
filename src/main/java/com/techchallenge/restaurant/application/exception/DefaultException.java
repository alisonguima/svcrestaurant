package com.techchallenge.restaurant.application.exception;

public class DefaultException extends RuntimeException {

  private final ErrorCode code;

  public DefaultException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCode getCode() {
    return code;
  }
}
