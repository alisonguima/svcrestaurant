package com.techchallenge.restaurant.adapter.input.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.techchallenge.restaurant.application.exception.BaseException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.exception.UserTypeInUseException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
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

    log.warn("handleValidation - fields={}", fieldErrors.keySet());

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_VALIDATION));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_VALIDATION);
    problemDetail.setDetail(ErrorResponseConstants.ERROR_DETAIL_VALIDATION);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errors", fieldErrors);
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {

    log.warn("handleNotReadable - message={}", ex.getMessage());

    String detail = resolveNotReadableDetail(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_INVALID_REQUEST));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_INVALID_REQUEST);
    problemDetail.setDetail(detail);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ProblemDetail> handleBusiness(BaseException ex, WebRequest request) {
    log.warn("handleBusiness - code={}, message={}", ex.getCode(), ex.getMessage());

    HttpStatus status = resolveStatus(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(status);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ex.getCode().toLowerCase().replace('_', '-')));
    problemDetail.setTitle(ex.getCode());
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity.status(status).body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, WebRequest request) {
    log.error("handleGeneric - message={}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setType(URI.create(ErrorResponseConstants.PROBLEM_DETAIL_TYPE_BASE + ErrorResponseConstants.ERROR_TYPE_INTERNAL_SERVER));
    problemDetail.setTitle(ErrorResponseConstants.ERROR_TITLE_INTERNAL_SERVER);
    problemDetail.setDetail(ErrorResponseConstants.ERROR_DETAIL_INTERNAL_SERVER);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("timestamp", problemDetailTimestamp());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }

  private static HttpStatus resolveStatus(BaseException ex) {
    return switch (ex) {
      case UserNotFoundException e             -> HttpStatus.NOT_FOUND;
      case UserTypeNotFoundException e         -> HttpStatus.NOT_FOUND;
      case RestaurantNotFoundException e       -> HttpStatus.NOT_FOUND;
      case RestaurantOwnerNotFoundException e  -> HttpStatus.NOT_FOUND;
      case MenuItemNotFoundException e         -> HttpStatus.NOT_FOUND;
      case MenuItemRestaurantNotFoundException e -> HttpStatus.NOT_FOUND;
      case RestaurantOwnerUnauthorizedException e -> HttpStatus.FORBIDDEN;
      case MenuItemOwnerUnauthorizedException e   -> HttpStatus.FORBIDDEN;
      case UserTypeInUseException e            -> HttpStatus.CONFLICT;
      default                                  -> HttpStatus.UNPROCESSABLE_ENTITY;
    };
  }

  private String resolveNotReadableDetail(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof InvalidFormatException invalidFormatException) {
      String fieldName = invalidFormatException.getPath().stream()
          .map(JsonMappingException.Reference::getFieldName)
          .filter(Objects::nonNull)
          .findFirst()
          .orElse("unknown");

      if (ErrorResponseConstants.FIELD_USER_TYPE_ID.equals(fieldName)) {
        return "Invalid userTypeId value";
      }
      return "Invalid value for field: " + fieldName;
    }
    return "Invalid request body";
  }

  private String problemDetailTimestamp() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
        .withResolverStyle(ResolverStyle.STRICT);
    return ZonedDateTime.now(UTC).format(formatter);
  }
}
