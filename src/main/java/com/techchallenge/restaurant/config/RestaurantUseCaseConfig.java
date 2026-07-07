package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.restaurant.CreateRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.DeleteRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantsPort;
import com.techchallenge.restaurant.application.port.input.restaurant.UpdateRestaurantPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.usecase.restaurant.CreateRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.DeleteRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.GetRestaurantUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.GetRestaurantsUseCase;
import com.techchallenge.restaurant.application.usecase.restaurant.UpdateRestaurantUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantUseCaseConfig {

  @Bean
  public CreateRestaurantPort createRestaurantPort(RestaurantPersistencePort r,
      UserPersistencePort u, DateTimeProviderPort dt, TransactionPort tx) {
    return new CreateRestaurantUseCase(r, u, dt, tx);
  }

  @Bean
  public UpdateRestaurantPort updateRestaurantPort(RestaurantPersistencePort r,
      UserPersistencePort u, DateTimeProviderPort dt, TransactionPort tx) {
    return new UpdateRestaurantUseCase(r, u, dt, tx);
  }

  @Bean
  public GetRestaurantPort getRestaurantPort(RestaurantPersistencePort r, TransactionPort tx) {
    return new GetRestaurantUseCase(r, tx);
  }

  @Bean
  public GetRestaurantsPort getRestaurantsPort(RestaurantPersistencePort r, TransactionPort tx) {
    return new GetRestaurantsUseCase(r, tx);
  }

  @Bean
  public DeleteRestaurantPort deleteRestaurantPort(RestaurantPersistencePort r, TransactionPort tx) {
    return new DeleteRestaurantUseCase(r, tx);
  }
}
