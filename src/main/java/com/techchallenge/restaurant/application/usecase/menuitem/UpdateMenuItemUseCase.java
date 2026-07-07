package com.techchallenge.restaurant.application.usecase.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.menuitem.UpdateMenuItemPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateMenuItemUseCase implements UpdateMenuItemPort {

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public MenuItem execute(Long restaurantId, Long menuItemId, Long ownerId, MenuItem patch) {
    return transactionPort.execute(() -> {
      MenuItem existing = menuItemPersistencePort.findById(menuItemId)
          .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

      if (!existing.belongsToRestaurant(restaurantId)) {
        throw new MenuItemRestaurantNotFoundException();
      }

      if (!existing.getRestaurant().isOwnedBy(ownerId)) {
        throw new MenuItemOwnerUnauthorizedException();
      }

      existing.applyUpdate(patch);
      existing.stamp(dateTimeProviderPort.nowUtc());
      return menuItemPersistencePort.save(existing);
    });
  }
}
