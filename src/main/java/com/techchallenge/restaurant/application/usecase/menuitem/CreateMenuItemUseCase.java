package com.techchallenge.restaurant.application.usecase.menuitem;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.MenuItemAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.port.input.menuitem.CreateMenuItemPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CreateMenuItemUseCase implements CreateMenuItemPort {

  private static final Logger log = LoggerFactory.getLogger(CreateMenuItemUseCase.class);

  private final MenuItemPersistencePort menuItemPersistencePort;
  private final RestaurantPersistencePort restaurantPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public MenuItem execute(Long restaurantId, Long ownerId, MenuItem menuItem) {
    return transactionPort.execute(() -> {
      log.info("CreateMenuItemUseCase - restaurantId={}, ownerId={}", restaurantId, ownerId);

      Restaurant restaurant = restaurantPersistencePort.findById(restaurantId)
          .orElseThrow(MenuItemRestaurantNotFoundException::new);

      if (!restaurant.isOwnedBy(ownerId)) {
        throw new MenuItemOwnerUnauthorizedException();
      }

      if (menuItemPersistencePort.existsByNameIgnoreCaseAndRestaurantId(menuItem.getName(), restaurantId)) {
        throw new MenuItemAlreadyExistsException();
      }

      menuItem.assignRestaurant(restaurant);
      menuItem.stamp(dateTimeProviderPort.nowUtc());
      return menuItemPersistencePort.save(menuItem);
    });
  }
}
