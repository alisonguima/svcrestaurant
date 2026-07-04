package com.techchallenge.restaurant.adapter.input.exception;

public final class ErrorResponseConstants {

  private ErrorResponseConstants() {}

  public static final String PROBLEM_DETAIL_TYPE_BASE = "https://api.restaurant.com/errors/";

  public static final String ERROR_TYPE_DUPLICATE_RESOURCE = "duplicate-resource";
  public static final String ERROR_TYPE_RESOURCE_NOT_FOUND = "resource-not-found";
  public static final String ERROR_TYPE_INVALID_REQUEST = "invalid-request";
  public static final String ERROR_TYPE_CONFLICT = "conflict";
  public static final String ERROR_TYPE_FORBIDDEN = "forbidden";
  public static final String ERROR_TYPE_VALIDATION = "validation-error";
  public static final String ERROR_TYPE_INTERNAL_SERVER = "internal-server-error";

  public static final String ERROR_TITLE_DUPLICATE_RESOURCE = "Duplicate Resource";
  public static final String ERROR_TITLE_RESOURCE_NOT_FOUND = "Resource Not Found";
  public static final String ERROR_TITLE_INVALID_REQUEST = "Invalid Request";
  public static final String ERROR_TITLE_UNPROCESSABLE_ENTITY = "Unprocessable Entity";
  public static final String ERROR_TITLE_CONFLICT = "Conflict";
  public static final String ERROR_TITLE_FORBIDDEN = "Forbidden";
  public static final String ERROR_TITLE_VALIDATION = "Validation Error";
  public static final String ERROR_TITLE_INTERNAL_SERVER = "Internal Server Error";

  public static final String ERROR_DETAIL_VALIDATION = "One or more fields have validation errors";
  public static final String ERROR_DETAIL_INTERNAL_SERVER = "An unexpected error occurred. Please try again later.";

  public static final String FIELD_USER_TYPE_ID = "userTypeId";
}
