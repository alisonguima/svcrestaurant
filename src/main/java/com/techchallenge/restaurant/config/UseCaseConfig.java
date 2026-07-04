package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
import com.techchallenge.restaurant.application.port.input.UserUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.service.MenuItemService;
import com.techchallenge.restaurant.application.service.RestaurantService;
import com.techchallenge.restaurant.application.service.UserService;
import com.techchallenge.restaurant.application.service.UserTypeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public UserUseCase userUseCase(UserPersistencePort userPersistencePort,
                                 PasswordEncryptionPort passwordEncryptionPort,
                                 DateTimeProviderPort dateTimeProviderPort,
                                 UserTypePersistencePort userTypePersistencePort,
                                 TransactionPort transactionPort) {
    return new UserService(userPersistencePort, passwordEncryptionPort, dateTimeProviderPort,
        userTypePersistencePort, transactionPort);
  }

  @Bean
  public UserTypeUseCase userTypeUseCase(UserTypePersistencePort userTypePersistencePort,
                                         UserPersistencePort userPersistencePort,
                                         TransactionPort transactionPort) {
    return new UserTypeService(userTypePersistencePort, userPersistencePort, transactionPort);
  }

  @Bean
  public RestaurantUseCase restaurantUseCase(RestaurantPersistencePort restaurantPersistencePort,
                                             UserPersistencePort userPersistencePort,
                                             DateTimeProviderPort dateTimeProviderPort,
                                             TransactionPort transactionPort) {
    return new RestaurantService(restaurantPersistencePort, userPersistencePort, dateTimeProviderPort,
        transactionPort);
  }

  @Bean
  public MenuItemUseCase menuItemUseCase(MenuItemPersistencePort menuItemPersistencePort,
                                         RestaurantPersistencePort restaurantPersistencePort,
                                         DateTimeProviderPort dateTimeProviderPort,
                                         TransactionPort transactionPort) {
    return new MenuItemService(menuItemPersistencePort, restaurantPersistencePort, dateTimeProviderPort,
        transactionPort);
  }
}
