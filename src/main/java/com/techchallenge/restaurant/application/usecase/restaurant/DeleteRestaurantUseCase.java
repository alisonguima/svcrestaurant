package com.techchallenge.restaurant.application.usecase.restaurant;

import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.restaurant.DeleteRestaurantPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteRestaurantUseCase implements DeleteRestaurantPort {

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final TransactionPort transactionPort;

  public void execute(Long restaurantId) {
    transactionPort.executeVoid(() -> {
      restaurantPersistencePort.findById(restaurantId)
          .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
      restaurantPersistencePort.deleteById(restaurantId);
    });
  }
}
