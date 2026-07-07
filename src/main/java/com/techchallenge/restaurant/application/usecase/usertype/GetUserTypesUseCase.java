package com.techchallenge.restaurant.application.usecase.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypesPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetUserTypesUseCase implements GetUserTypesPort {

  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  public List<UserType> execute() {
    return transactionPort.executeReadOnly(userTypePersistencePort::findAll);
  }
}
