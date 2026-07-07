package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.menuitem.CreateMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.DeleteMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemsByRestaurantPort;
import com.techchallenge.restaurant.application.port.input.menuitem.UpdateMenuItemPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.usecase.menuitem.CreateMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.DeleteMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.GetMenuItemUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.GetMenuItemsByRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.menuitem.UpdateMenuItemUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemUseCaseConfig {

  @Bean
  public CreateMenuItemPort createMenuItemPort(MenuItemPersistencePort m,
      RestaurantPersistencePort r, DateTimeProviderPort dt, TransactionPort tx) {
    return new CreateMenuItemUseCase(m, r, dt, tx);
  }

  @Bean
  public UpdateMenuItemPort updateMenuItemPort(MenuItemPersistencePort m,
      DateTimeProviderPort dt, TransactionPort tx) {
    return new UpdateMenuItemUseCase(m, dt, tx);
  }

  @Bean
  public GetMenuItemPort getMenuItemPort(MenuItemPersistencePort m, TransactionPort tx) {
    return new GetMenuItemUseCase(m, tx);
  }

  @Bean
  public GetMenuItemsByRestaurantPort getMenuItemsByRestaurantPort(MenuItemPersistencePort m,
      RestaurantPersistencePort r, TransactionPort tx) {
    return new GetMenuItemsByRestaurantUseCase(m, r, tx);
  }

  @Bean
  public DeleteMenuItemPort deleteMenuItemPort(MenuItemPersistencePort m, TransactionPort tx) {
    return new DeleteMenuItemUseCase(m, tx);
  }
}
