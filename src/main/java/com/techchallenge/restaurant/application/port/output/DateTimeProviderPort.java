package com.techchallenge.restaurant.application.port.output;

import java.time.ZonedDateTime;

public interface DateTimeProviderPort {

  ZonedDateTime nowUtc();
}
