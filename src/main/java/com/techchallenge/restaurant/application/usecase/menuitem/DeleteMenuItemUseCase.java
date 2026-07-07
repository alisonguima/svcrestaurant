package com.techchallenge.restaurant.application.usecase.menuitem;

import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.menuitem.DeleteMenuItemPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteMenuItemUseCase implements DeleteMenuItemPort {

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final TransactionPort transactionPort;

  public void execute(Long restaurantId, Long menuItemId, Long ownerId) {
    transactionPort.executeVoid(() -> {
      var item = menuItemPersistencePort.findById(menuItemId)
          .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

      if (!item.belongsToRestaurant(restaurantId)) {
        throw new MenuItemRestaurantNotFoundException();
      }

      if (!item.getRestaurant().isOwnedBy(ownerId)) {
        throw new MenuItemOwnerUnauthorizedException();
      }

      menuItemPersistencePort.deleteById(menuItemId);
    });
  }
}
