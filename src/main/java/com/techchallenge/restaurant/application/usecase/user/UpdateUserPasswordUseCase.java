package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.InvalidPasswordException;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPasswordPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UpdateUserPasswordUseCase implements UpdateUserPasswordPort {

  private static final Logger log = LoggerFactory.getLogger(UpdateUserPasswordUseCase.class);

  private final UserPersistencePort userPersistencePort;
  private final PasswordEncryptionPort passwordEncryptionPort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public void execute(Long userId, String currentPassword, String newPassword) {
    transactionPort.executeVoid(() -> {
      log.info("UpdateUserPasswordUseCase - userId={}", userId);

      User existing = userPersistencePort.findById(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));

      if (!passwordEncryptionPort.matches(currentPassword, existing.getPassword())) {
        throw new InvalidPasswordException();
      }

      if (!passwordEncryptionPort.matches(newPassword, existing.getPassword())) {
        existing.changePassword(passwordEncryptionPort.encode(newPassword));
        existing.stamp(dateTimeProviderPort.nowUtc());
        userPersistencePort.save(existing);
        log.info("UpdateUserPasswordUseCase - password updated for userId={}", userId);
      }
    });
  }
}
