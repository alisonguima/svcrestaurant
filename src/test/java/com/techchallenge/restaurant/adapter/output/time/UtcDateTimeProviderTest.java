package com.techchallenge.restaurant.adapter.output.time;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;

class UtcDateTimeProviderTest {

  private final UtcDateTimeProvider provider = new UtcDateTimeProvider();

  @Test
  void nowUtc_shouldReturnZonedDateTimeWithUtcOffset() {
    ZonedDateTime before = ZonedDateTime.now(UTC);

    ZonedDateTime result = provider.nowUtc();

    ZonedDateTime after = ZonedDateTime.now(UTC);

    assertEquals(UTC, result.getOffset());
    assertFalse(result.isBefore(before), "Result should not be before the test start");
    assertFalse(result.isAfter(after), "Result should not be after the test end");
  }
}
