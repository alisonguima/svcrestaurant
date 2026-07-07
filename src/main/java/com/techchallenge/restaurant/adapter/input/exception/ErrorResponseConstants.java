package com.techchallenge.restaurant.adapter.input.exception;

public final class ErrorResponseConstants {

  private ErrorResponseConstants() {}

  public static final String PROBLEM_DETAIL_TYPE_BASE = "https://api.restaurant.com/errors/";

  public static final String ERROR_TYPE_INVALID_REQUEST = "invalid-request";
  public static final String ERROR_TYPE_VALIDATION = "validation-error";
  public static final String ERROR_TYPE_INTERNAL_SERVER = "internal-server-error";

  public static final String ERROR_TITLE_INVALID_REQUEST = "Invalid Request";
  public static final String ERROR_TITLE_VALIDATION = "Validation Error";
  public static final String ERROR_TITLE_INTERNAL_SERVER = "Internal Server Error";

  public static final String ERROR_DETAIL_VALIDATION = "One or more fields have validation errors";
  public static final String ERROR_DETAIL_INTERNAL_SERVER = "An unexpected error occurred. Please try again later.";

  public static final String FIELD_USER_TYPE_ID = "userTypeId";
}
