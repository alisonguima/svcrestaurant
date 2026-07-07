package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.input.user.AssignUserTypePort;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class AssignUserTypeUseCase implements AssignUserTypePort {

  private static final Logger log = LoggerFactory.getLogger(AssignUserTypeUseCase.class);

  private final UserPersistencePort userPersistencePort;
  private final UserTypePersistencePort userTypePersistencePort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final TransactionPort transactionPort;

  public void execute(Long userId, Long userTypeId) {
    transactionPort.executeVoid(() -> {
      log.info("AssignUserTypeUseCase - userId={}, userTypeId={}", userId, userTypeId);

      User existing = userPersistencePort.findById(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));

      UserType userType = userTypePersistencePort.findById(userTypeId)
          .orElseThrow(() -> new UserTypeNotFoundException(userTypeId));

      existing.assignUserType(userType);
      existing.stamp(dateTimeProviderPort.nowUtc());
      userPersistencePort.save(existing);
    });
  }
}
