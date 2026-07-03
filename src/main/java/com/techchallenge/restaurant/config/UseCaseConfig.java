package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.UserUseCase;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.service.UserService;
import com.techchallenge.restaurant.application.service.UserTypeService;
import com.techchallenge.restaurant.application.service.RestaurantService;
import com.techchallenge.restaurant.application.service.MenuItemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public UserUseCase userUseCase(UserPersistencePort userPersistencePort,
                                 PasswordEncryptionPort passwordEncryptionPort,
                                 DateTimeProviderPort dateTimeProviderPort,
                                 UserTypePersistencePort userTypePersistencePort) {
    return new UserService(userPersistencePort, passwordEncryptionPort, dateTimeProviderPort, userTypePersistencePort);
  }

  @Bean
  public UserTypeUseCase userTypeUseCase(UserTypePersistencePort userTypePersistencePort,
                                         UserPersistencePort userPersistencePort) {
    return new UserTypeService(userTypePersistencePort, userPersistencePort);
  }

  @Bean
  public RestaurantUseCase restaurantUseCase(RestaurantPersistencePort restaurantPersistencePort,
                                             UserPersistencePort userPersistencePort,
                                             DateTimeProviderPort dateTimeProviderPort) {
    return new RestaurantService(restaurantPersistencePort, userPersistencePort, dateTimeProviderPort);
  }

  @Bean
  public MenuItemUseCase menuItemUseCase(MenuItemPersistencePort menuItemPersistencePort,
                                         RestaurantPersistencePort restaurantPersistencePort,
                                         DateTimeProviderPort dateTimeProviderPort) {
    return new MenuItemService(menuItemPersistencePort, restaurantPersistencePort, dateTimeProviderPort);
  }
}
