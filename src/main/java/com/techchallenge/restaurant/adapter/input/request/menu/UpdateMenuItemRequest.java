package com.techchallenge.restaurant.adapter.input.request.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(
    @Size(min = 2, max = 120)
    String name,

    @Size(min = 2, max = 500)
    String description,

    @DecimalMin(value = "0.01")
    BigDecimal price,

    Boolean onlyAtRestaurant,

    @Size(max = 255)
    String photoPath) {}
