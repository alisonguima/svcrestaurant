package com.techchallenge.restaurant.adapter.input.response.restaurant;

public record CreateRestaurantResponse(
    String id,
    String name,
    String address,
    String cuisineType,
    String openingHours,
    String ownerId,
    String ownerName
) {}
