package com.techchallenge.restaurant.application.usecase.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserTypeUseCase implements GetUserTypePort {

  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  public UserType execute(Long userTypeId) {
    return transactionPort.executeReadOnly(() ->
        userTypePersistencePort.findById(userTypeId)
            .orElseThrow(() -> new UserTypeNotFoundException(userTypeId)));
  }
}
