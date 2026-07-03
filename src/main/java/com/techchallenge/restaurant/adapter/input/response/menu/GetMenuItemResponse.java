package com.techchallenge.restaurant.adapter.input.response.menu;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record GetMenuItemResponse(
    String id,
    String name,
    String description,
    BigDecimal price,
    boolean onlyAtRestaurant,
    String photoPath,
    String restaurantId,
    String restaurantName,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
