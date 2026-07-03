package com.techchallenge.restaurant.adapter.input.response.restaurant;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record GetRestaurantResponse(
    String id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    String ownerId,
    String ownerName,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
