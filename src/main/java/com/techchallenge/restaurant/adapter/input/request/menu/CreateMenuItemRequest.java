package com.techchallenge.restaurant.adapter.input.request.menu;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateMenuItemRequest(
    @NotBlank(message = ApiConstants.MENU_ITEM_NAME_REQUIRED)
    @Size(min = 2, max = 120)
    String name,

    @NotBlank(message = ApiConstants.MENU_ITEM_DESCRIPTION_REQUIRED)
    @Size(min = 2, max = 500)
    String description,

    @NotNull(message = ApiConstants.MENU_ITEM_PRICE_REQUIRED)
    @DecimalMin(value = "0.01", message = ApiConstants.MENU_ITEM_PRICE_INVALID)
    BigDecimal price,

    boolean onlyAtRestaurant,

    @NotBlank(message = ApiConstants.MENU_ITEM_PHOTO_PATH_REQUIRED)
    @Size(max = 255)
    String photoPath) {}
