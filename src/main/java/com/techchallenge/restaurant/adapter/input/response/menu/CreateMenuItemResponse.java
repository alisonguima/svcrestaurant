package com.techchallenge.restaurant.adapter.input.response.menu;

public record CreateMenuItemResponse(
    String id,
    String name,
    String description,
    String price,
    boolean onlyAtRestaurant,
    String photoPath,
    String restaurantId,
    String restaurantName
) {}
