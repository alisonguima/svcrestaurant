package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.EmailAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.LoginAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UpdateUserUseCase implements UpdateUserPort {

  private static final Logger log = LoggerFactory.getLogger(UpdateUserUseCase.class);

  private final UserPersistencePort userPersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  public void execute(Long userId, User patch) {
    transactionPort.executeVoid(() -> {
      log.info("UpdateUserUseCase - userId={}", userId);

      User existing = userPersistencePort.findById(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));

      if (patch.getEmail() != null &&
          !patch.getEmail().equals(existing.getEmail()) &&
          userPersistencePort.existsByEmail(patch.getEmail())) {
        throw new EmailAlreadyExistsException();
      }
      if (patch.getLogin() != null &&
          !patch.getLogin().equals(existing.getLogin()) &&
          userPersistencePort.existsByLogin(patch.getLogin())) {
        throw new LoginAlreadyExistsException();
      }

      existing.applyUpdate(patch);

      UserType resolvedType = (patch.getUserType() != null)
          ? userTypePersistencePort.findById(patch.getUserType().getId())
              .orElseThrow(() -> new UserTypeNotFoundException(patch.getUserType().getId()))
          : existing.getUserType();
      existing.assignUserType(resolvedType);

      existing.stamp(dateTimeProviderPort.nowUtc());
      userPersistencePort.save(existing);
    });
  }
}
