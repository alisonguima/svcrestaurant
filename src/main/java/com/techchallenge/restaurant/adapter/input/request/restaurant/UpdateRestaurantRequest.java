package com.techchallenge.restaurant.adapter.input.request.restaurant;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
    @Size(min = 2, max = 120)
    String name,

    @Size(min = 5, max = 255)
    String address,

    @Size(min = 2, max = 100)
    String cuisineType,

    @Size(min = 2, max = 100)
    String openingHours,

    Long ownerUserId) {}
