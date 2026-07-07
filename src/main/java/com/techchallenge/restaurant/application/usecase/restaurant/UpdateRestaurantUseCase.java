package com.techchallenge.restaurant.application.usecase.restaurant;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerNotFoundException;
import com.techchallenge.restaurant.application.port.input.restaurant.UpdateRestaurantPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateRestaurantUseCase implements UpdateRestaurantPort {

  private final RestaurantPersistencePort restaurantPersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public Restaurant execute(Long restaurantId, Restaurant patch) {
    return transactionPort.execute(() -> {
      Restaurant existing = restaurantPersistencePort.findById(restaurantId)
          .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

      existing.applyUpdate(patch);

      if (patch.getOwner() != null && patch.getOwner().getId() != null) {
        User newOwner = userPersistencePort.findById(patch.getOwner().getId())
            .orElseThrow(RestaurantOwnerNotFoundException::new);
        existing.assignOwner(newOwner);
      }

      existing.stamp(dateTimeProviderPort.nowUtc());
      return restaurantPersistencePort.save(existing);
    });
  }
}
