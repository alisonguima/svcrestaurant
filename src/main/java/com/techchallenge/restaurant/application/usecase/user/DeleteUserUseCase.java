package com.techchallenge.restaurant.application.usecase.user;

import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.port.input.user.DeleteUserPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class DeleteUserUseCase implements DeleteUserPort {

  private static final Logger log = LoggerFactory.getLogger(DeleteUserUseCase.class);

  private final UserPersistencePort userPersistencePort;
  private final TransactionPort transactionPort;

  public void execute(Long userId) {
    transactionPort.executeVoid(() -> {
      log.info("DeleteUserUseCase - userId={}", userId);
      userPersistencePort.findById(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));
      userPersistencePort.deleteById(userId);
    });
  }
}
