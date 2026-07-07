package com.techchallenge.restaurant.application.usecase.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantsPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetRestaurantsUseCase implements GetRestaurantsPort {

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final TransactionPort transactionPort;

  public List<Restaurant> execute() {
    return transactionPort.executeReadOnly(restaurantPersistencePort::findAll);
  }
}
