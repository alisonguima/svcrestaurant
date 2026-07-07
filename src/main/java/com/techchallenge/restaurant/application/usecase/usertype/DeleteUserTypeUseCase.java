package com.techchallenge.restaurant.application.usecase.usertype;

import com.techchallenge.restaurant.application.exception.UserTypeInUseException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.input.usertype.DeleteUserTypePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUserTypeUseCase implements DeleteUserTypePort {

  private final UserTypePersistencePort userTypePersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final TransactionPort transactionPort;

  public void execute(Long userTypeId) {
    transactionPort.executeVoid(() -> {
      userTypePersistencePort.findById(userTypeId)
          .orElseThrow(() -> new UserTypeNotFoundException(userTypeId));

      if (userPersistencePort.countByUserTypeId(userTypeId) > 0L) {
        throw new UserTypeInUseException();
      }
      userTypePersistencePort.deleteById(userTypeId);
    });
  }
}
