package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.usertype.CreateUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.DeleteUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypesPort;
import com.techchallenge.restaurant.application.port.input.usertype.UpdateUserTypePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.usecase.usertype.CreateUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.DeleteUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.GetUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.GetUserTypesUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.UpdateUserTypeUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTypeUseCaseConfig {

  @Bean
  public CreateUserTypePort createUserTypePort(UserTypePersistencePort ut, TransactionPort tx) {
    return new CreateUserTypeUseCase(ut, tx);
  }

  @Bean
  public UpdateUserTypePort updateUserTypePort(UserTypePersistencePort ut, TransactionPort tx) {
    return new UpdateUserTypeUseCase(ut, tx);
  }

  @Bean
  public GetUserTypePort getUserTypePort(UserTypePersistencePort ut, TransactionPort tx) {
    return new GetUserTypeUseCase(ut, tx);
  }

  @Bean
  public GetUserTypesPort getUserTypesPort(UserTypePersistencePort ut, TransactionPort tx) {
    return new GetUserTypesUseCase(ut, tx);
  }

  @Bean
  public DeleteUserTypePort deleteUserTypePort(UserTypePersistencePort ut,
      UserPersistencePort u, TransactionPort tx) {
    return new DeleteUserTypeUseCase(ut, u, tx);
  }
}
