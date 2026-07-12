package com.techchallenge.restaurant.config;

import com.techchallenge.restaurant.application.port.input.user.AssignUserTypePort;
import com.techchallenge.restaurant.application.port.input.user.CreateUserPort;
import com.techchallenge.restaurant.application.port.input.user.DeleteUserPort;
import com.techchallenge.restaurant.application.port.input.user.GetUserPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPasswordPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.usecase.user.AssignUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.user.CreateUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.DeleteUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.GetUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.UpdateUserPasswordUseCase;
import com.techchallenge.restaurant.application.usecase.user.UpdateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

  @Bean
  public CreateUserPort createUserPort(UserPersistencePort u, PasswordEncryptionPort p,
      DateTimeProviderPort dt, TransactionPort tx) {
    return new CreateUserUseCase(u, p, dt, tx);
  }

  @Bean
  public UpdateUserPort updateUserPort(UserPersistencePort u, DateTimeProviderPort dt,
      UserTypePersistencePort ut, TransactionPort tx) {
    return new UpdateUserUseCase(u, dt, ut, tx);
  }

  @Bean
  public UpdateUserPasswordPort updateUserPasswordPort(UserPersistencePort u,
      PasswordEncryptionPort p, DateTimeProviderPort dt, TransactionPort tx) {
    return new UpdateUserPasswordUseCase(u, p, dt, tx);
  }

  @Bean
  public AssignUserTypePort assignUserTypePort(UserPersistencePort u,
      UserTypePersistencePort ut, DateTimeProviderPort dt, TransactionPort tx) {
    return new AssignUserTypeUseCase(u, ut, dt, tx);
  }

  @Bean
  public GetUserPort getUserPort(UserPersistencePort u, TransactionPort tx) {
    return new GetUserUseCase(u, tx);
  }

  @Bean
  public DeleteUserPort deleteUserPort(UserPersistencePort u, TransactionPort tx) {
    return new DeleteUserUseCase(u, tx);
  }
}
