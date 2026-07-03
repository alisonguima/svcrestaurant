package com.techchallenge.restaurant.adapter.input.request.restaurant;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(
    @NotBlank(message = ApiConstants.RESTAURANT_NAME_REQUIRED)
    @Size(min = 2, max = 120)
    String name,

    @NotBlank(message = ApiConstants.RESTAURANT_ADDRESS_REQUIRED)
    @Size(min = 5, max = 255)
    String address,

    @NotBlank(message = ApiConstants.RESTAURANT_CUISINE_REQUIRED)
    @Size(min = 2, max = 100)
    String cuisineType,

    @NotBlank(message = ApiConstants.RESTAURANT_OPENING_HOURS_REQUIRED)
    @Size(min = 2, max = 100)
    String openingHours,

    @NotNull(message = ApiConstants.RESTAURANT_OWNER_REQUIRED)
    Long ownerUserId) {}
