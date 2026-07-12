package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.EmailAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.LoginAlreadyExistsException;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.input.user.CreateUserPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUserPort {

  private static final Logger log = LoggerFactory.getLogger(CreateUserUseCase.class);

  private final UserPersistencePort userPersistencePort;
  private final PasswordEncryptionPort passwordEncryptionPort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public User execute(User user) {
    return transactionPort.execute(() -> {
      log.info("CreateUserUseCase - email={}, login={}", user.getEmail(), user.getLogin());

      if (userPersistencePort.existsByEmail(user.getEmail())) {
        throw new EmailAlreadyExistsException();
      }
      if (userPersistencePort.existsByLogin(user.getLogin())) {
        throw new LoginAlreadyExistsException();
      }

      user.changePassword(passwordEncryptionPort.encode(user.getPassword()));
      user.stamp(dateTimeProviderPort.nowUtc());

      User saved = userPersistencePort.save(user);
      log.info("CreateUserUseCase - created id={}", saved.getId());
      return saved;
    });
  }
}
