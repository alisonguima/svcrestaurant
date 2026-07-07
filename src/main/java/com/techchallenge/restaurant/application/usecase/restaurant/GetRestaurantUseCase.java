package com.techchallenge.restaurant.application.usecase.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetRestaurantUseCase implements GetRestaurantPort {

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final TransactionPort transactionPort;

  public Restaurant execute(Long restaurantId) {
    return transactionPort.executeReadOnly(() ->
        restaurantPersistencePort.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId)));
  }
}
