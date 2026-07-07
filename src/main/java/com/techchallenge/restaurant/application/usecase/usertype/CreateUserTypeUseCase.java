package com.techchallenge.restaurant.application.usecase.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.UserTypeAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserTypeInvalidNameException;
import com.techchallenge.restaurant.application.port.input.usertype.CreateUserTypePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CreateUserTypeUseCase implements CreateUserTypePort {

  private static final Logger log = LoggerFactory.getLogger(CreateUserTypeUseCase.class);

  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  public UserType execute(UserType userType) {
    return transactionPort.execute(() -> {
      log.info("CreateUserTypeUseCase - name={}", userType.getName());

      if (!userType.isAllowedName()) {
        throw new UserTypeInvalidNameException();
      }
      if (userTypePersistencePort.existsByNameIgnoreCase(userType.getName())) {
        throw new UserTypeAlreadyExistsException();
      }

      return userTypePersistencePort.save(userType);
    });
  }
}
