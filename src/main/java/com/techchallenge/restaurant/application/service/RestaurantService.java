package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RestaurantService implements RestaurantUseCase {

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;

  @Override
  public Restaurant createRestaurant(Restaurant restaurant) {
    restaurant.setOwner(getOwner(restaurant.getOwner().getId()));
    restaurant.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    return restaurantPersistencePort.save(restaurant);
  }

  @Override
  public Restaurant updateRestaurant(Long restaurantId, Restaurant restaurant) {
    Restaurant existing = getRestaurant(restaurantId);
    existing.setName(Optional.ofNullable(restaurant.getName()).orElse(existing.getName()));
    existing.setAddress(Optional.ofNullable(restaurant.getAddress()).orElse(existing.getAddress()));
    existing.setCuisineType(Optional.ofNullable(restaurant.getCuisineType()).orElse(existing.getCuisineType()));
    existing.setOpeningHours(Optional.ofNullable(restaurant.getOpeningHours()).orElse(existing.getOpeningHours()));
    if (restaurant.getOwner() != null && restaurant.getOwner().getId() != null) {
      existing.setOwner(getOwner(restaurant.getOwner().getId()));
    }
    existing.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    return restaurantPersistencePort.save(existing);
  }

  @Override
  public Restaurant getRestaurant(Long restaurantId) {
    return restaurantPersistencePort.findById(restaurantId)
        .orElseThrow(() -> new DefaultException(ErrorCode.RESTAURANT_NOT_FOUND, ApiConstants.RESTAURANT_NOT_FOUND_WITH_ID + restaurantId));
  }

  @Override
  public List<Restaurant> getRestaurants() {
    return restaurantPersistencePort.findAll();
  }

  @Override
  public void deleteRestaurant(Long restaurantId) {
    getRestaurant(restaurantId);
    restaurantPersistencePort.deleteById(restaurantId);
  }

  private User getOwner(Long ownerId) {
    return userPersistencePort.findById(ownerId)
        .orElseThrow(() -> new DefaultException(ErrorCode.RESTAURANT_OWNER_NOT_FOUND, ApiConstants.RESTAURANT_OWNER_NOT_FOUND));
  }
}
