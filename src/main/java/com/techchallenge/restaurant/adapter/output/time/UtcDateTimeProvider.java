package com.techchallenge.restaurant.adapter.output.time;

import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import static java.time.ZoneOffset.UTC;

@Component
public class UtcDateTimeProvider implements DateTimeProviderPort {

  @Override
  public ZonedDateTime nowUtc() {
    return ZonedDateTime.now(UTC);
  }
}
