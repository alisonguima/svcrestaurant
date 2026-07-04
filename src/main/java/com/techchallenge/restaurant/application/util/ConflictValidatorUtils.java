package com.techchallenge.restaurant.application.util;

import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.exception.DefaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class ConflictValidatorUtils {

  private static final Logger log = LoggerFactory.getLogger(ConflictValidatorUtils.class);

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
