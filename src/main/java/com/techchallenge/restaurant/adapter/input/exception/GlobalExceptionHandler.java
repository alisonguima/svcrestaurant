package com.techchallenge.restaurant.adapter.input.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value"),
            (existing, duplicate) -> existing));

    log.warn("handleValidation - Validation failed: fields={}", fieldErrors.keySet());

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_VALIDATION));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_VALIDATION);
    problemDetail.setDetail(ErrorResponseConstants.ERROR_DETAIL_VALIDATION);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errors", fieldErrors);
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(problemDetail);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {

    log.warn("handleNotReadable - Invalid request body: message={}", ex.getMessage());

    String detail = resolveNotReadableDetail(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_INVALID_REQUEST));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_INVALID_REQUEST);
    problemDetail.setDetail(detail);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(problemDetail);
  }

  @ExceptionHandler(DefaultException.class)
  public ResponseEntity<ProblemDetail> handleBusiness(
      DefaultException ex, WebRequest request) {

    log.warn("handleBusiness - Business error: code={}, message={}", ex.getCode(), ex.getMessage());

    ErrorSpec errorSpec = resolveErrorSpec(ex.getCode());
    ProblemDetail problemDetail = ProblemDetail.forStatus(errorSpec.status());
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + errorSpec.type()));
    problemDetail.setTitle(errorSpec.title());
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity
        .status(errorSpec.status())
        .body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGeneric(
      Exception ex, WebRequest request) {

    log.error("handleGeneric - Unexpected error: message={}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_INTERNAL_SERVER));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_INTERNAL_SERVER);
    problemDetail.setDetail(ErrorResponseConstants.ERROR_DETAIL_INTERNAL_SERVER);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(problemDetail);
  }

  private ErrorSpec resolveErrorSpec(ErrorCode code) {
    return switch (code) {
      case EMAIL_ALREADY_EXISTS, LOGIN_ALREADY_EXISTS ->
          new ErrorSpec(HttpStatus.UNPROCESSABLE_ENTITY,
              ErrorResponseConstants.ERROR_TYPE_DUPLICATE_RESOURCE,
              ErrorResponseConstants.ERROR_TITLE_DUPLICATE_RESOURCE);
      case USER_NOT_FOUND ->
          new ErrorSpec(HttpStatus.NOT_FOUND,
              ErrorResponseConstants.ERROR_TYPE_RESOURCE_NOT_FOUND,
              ErrorResponseConstants.ERROR_TITLE_RESOURCE_NOT_FOUND);
      case INVALID_PASSWORD ->
          new ErrorSpec(HttpStatus.UNPROCESSABLE_ENTITY,
              ErrorResponseConstants.ERROR_TYPE_CONFLICT,
              ErrorResponseConstants.ERROR_TITLE_UNPROCESSABLE_ENTITY);
      case USER_TYPE_NOT_FOUND ->
          new ErrorSpec(HttpStatus.NOT_FOUND,
              ErrorResponseConstants.ERROR_TYPE_RESOURCE_NOT_FOUND,
              ErrorResponseConstants.ERROR_TITLE_RESOURCE_NOT_FOUND);
      case USER_TYPE_INVALID_NAME ->
          new ErrorSpec(HttpStatus.UNPROCESSABLE_ENTITY,
              ErrorResponseConstants.ERROR_TYPE_INVALID_REQUEST,
              ErrorResponseConstants.ERROR_TITLE_INVALID_REQUEST);
      case USER_TYPE_ALREADY_EXISTS, RESTAURANT_ALREADY_EXISTS, MENU_ITEM_ALREADY_EXISTS ->
          new ErrorSpec(HttpStatus.UNPROCESSABLE_ENTITY,
              ErrorResponseConstants.ERROR_TYPE_DUPLICATE_RESOURCE,
              ErrorResponseConstants.ERROR_TITLE_DUPLICATE_RESOURCE);
      case USER_TYPE_IN_USE, RESTAURANT_NOT_FOUND, RESTAURANT_OWNER_NOT_FOUND, MENU_ITEM_NOT_FOUND, MENU_ITEM_RESTAURANT_NOT_FOUND ->
          new ErrorSpec(HttpStatus.NOT_FOUND,
              ErrorResponseConstants.ERROR_TYPE_RESOURCE_NOT_FOUND,
              ErrorResponseConstants.ERROR_TITLE_RESOURCE_NOT_FOUND);
    };
  }

  private String resolveNotReadableDetail(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();

    if (cause instanceof InvalidFormatException invalidFormatException) {
      String fieldName = resolveFieldName(invalidFormatException);

      if ("userTypeId".equals(fieldName)) {
        return "Invalid userTypeId value";
      }

      return "Invalid value for field: " + fieldName;
    }

    return "Invalid request body";
  }

  private String resolveFieldName(InvalidFormatException exception) {
    return exception.getPath().stream()
        .map(JsonMappingException.Reference::getFieldName)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("unknown");
  }

  private String problemDetailTimestamp() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
        .withResolverStyle(ResolverStyle.STRICT);
    return ZonedDateTime.now(UTC).format(formatter);
  }

  private record ErrorSpec(HttpStatus status, String type, String title) {}
}
