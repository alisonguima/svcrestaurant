package com.techchallenge.restaurant.application.util;

import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.exception.DefaultException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class ConflictValidatorUtils {

  private ConflictValidatorUtils() {}

  public static void throwIfExists(boolean exists, String logMessage, Object logArg, ErrorCode code, String errorMessage) {
    Optional.of(exists)
        .filter(Boolean::booleanValue)
        .ifPresent(e -> {
          log.warn(logMessage, logArg);
          throw new DefaultException(code, errorMessage);
        });
  }
}
