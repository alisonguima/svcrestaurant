package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.port.input.user.GetUserPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserUseCase implements GetUserPort {

  private final UserPersistencePort userPersistencePort;
  private final TransactionPort transactionPort;

  public User execute(Long userId) {
    return transactionPort.executeReadOnly(() ->
        userPersistencePort.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId)));
  }
}
