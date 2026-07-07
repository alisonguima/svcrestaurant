package com.techchallenge.restaurant.application.usecase.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetMenuItemUseCase implements GetMenuItemPort {

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final TransactionPort transactionPort;

  public MenuItem execute(Long restaurantId, Long menuItemId) {
    return transactionPort.executeReadOnly(() -> {
      MenuItem item = menuItemPersistencePort.findById(menuItemId)
          .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

      if (!item.belongsToRestaurant(restaurantId)) {
        throw new MenuItemRestaurantNotFoundException();
      }
      return item;
    });
  }
}
