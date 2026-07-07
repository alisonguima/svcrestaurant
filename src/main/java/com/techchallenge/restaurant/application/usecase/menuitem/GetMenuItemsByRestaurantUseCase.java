package com.techchallenge.restaurant.application.usecase.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemsByRestaurantPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetMenuItemsByRestaurantUseCase implements GetMenuItemsByRestaurantPort {

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final RestaurantPersistencePort restaurantPersistencePort;
  private final TransactionPort transactionPort;

  public List<MenuItem> execute(Long restaurantId) {
    return transactionPort.executeReadOnly(() -> {
      restaurantPersistencePort.findById(restaurantId)
          .orElseThrow(MenuItemRestaurantNotFoundException::new);
      return menuItemPersistencePort.findByRestaurantId(restaurantId);
    });
  }
}
