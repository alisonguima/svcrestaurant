package com.techchallenge.restaurant.application.usecase.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.RestaurantAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.port.input.restaurant.CreateRestaurantPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CreateRestaurantUseCase implements CreateRestaurantPort {

  private static final Logger log = LoggerFactory.getLogger(CreateRestaurantUseCase.class);

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public Restaurant execute(Restaurant restaurant) {
    return transactionPort.execute(() -> {
      log.info("CreateRestaurantUseCase - name={}", restaurant.getName());

      if (restaurantPersistencePort.existsByNameIgnoreCase(restaurant.getName())) {
        throw new RestaurantAlreadyExistsException();
      }

      User owner = userPersistencePort.findById(restaurant.getOwner().getId())
          .orElseThrow(RestaurantOwnerNotFoundException::new);

      if (!owner.isRestaurantOwner()) {
        throw new RestaurantOwnerUnauthorizedException();
      }

      restaurant.assignOwner(owner);
      restaurant.stamp(dateTimeProviderPort.nowUtc());
      return restaurantPersistencePort.save(restaurant);
    });
  }
}
