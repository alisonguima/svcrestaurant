package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.mapper.RestaurantDomainMapper;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantService implements RestaurantUseCase {

  private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  @Override
  public Restaurant createRestaurant(Restaurant restaurant) {
    return transactionPort.execute(() -> {
      ConflictValidatorUtils.throwIfExists(
          restaurantPersistencePort.existsByNameIgnoreCase(restaurant.getName()),
          "createRestaurant - Restaurant already exists: name={}", restaurant.getName(),
          ErrorCode.RESTAURANT_ALREADY_EXISTS,
          ApiConstants.RESTAURANT_ALREADY_EXISTS);

      User owner = getOwner(restaurant.getOwner().getId());

      if (owner.getUserType() == null ||
          !ApiConstants.USER_TYPE_DONO_RESTAURANTE.equals(owner.getUserType().getName())) {
        throw new DefaultException(ErrorCode.RESTAURANT_OWNER_UNAUTHORIZED, ApiConstants.RESTAURANT_OWNER_UNAUTHORIZED);
      }

      return restaurantPersistencePort.save(
          RestaurantDomainMapper.INSTANCE.prepareForCreate(restaurant, owner, dateTimeProviderPort.nowUtc()));
    });
  }

  @Override
  public Restaurant updateRestaurant(Long restaurantId, Restaurant restaurant) {
    return transactionPort.execute(() -> {
      Restaurant existing = getRestaurant(restaurantId);

      User resolvedOwner = (restaurant.getOwner() != null && restaurant.getOwner().getId() != null)
          ? getOwner(restaurant.getOwner().getId())
          : existing.getOwner();

      return restaurantPersistencePort.save(
          RestaurantDomainMapper.INSTANCE.mergeForUpdate(restaurant, existing, resolvedOwner, dateTimeProviderPort.nowUtc()));
    });
  }

  @Override
  public Restaurant getRestaurant(Long restaurantId) {
    return transactionPort.executeReadOnly(() ->
        restaurantPersistencePort.findById(restaurantId)
            .orElseThrow(() -> new DefaultException(ErrorCode.RESTAURANT_NOT_FOUND, ApiConstants.RESTAURANT_NOT_FOUND_WITH_ID + restaurantId)));
  }

  @Override
  public List<Restaurant> getRestaurants() {
    return transactionPort.executeReadOnly(restaurantPersistencePort::findAll);
  }

  @Override
  public void deleteRestaurant(Long restaurantId) {
    transactionPort.executeVoid(() -> {
      getRestaurant(restaurantId);
      restaurantPersistencePort.deleteById(restaurantId);
    });
  }

  private User getOwner(Long ownerId) {
    return userPersistencePort.findById(ownerId)
        .orElseThrow(() -> new DefaultException(ErrorCode.RESTAURANT_OWNER_NOT_FOUND, ApiConstants.RESTAURANT_OWNER_NOT_FOUND));
  }
}
