package com.techchallenge.restaurant.application.usecase.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.UserTypeAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserTypeInvalidNameException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.input.usertype.UpdateUserTypePort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserTypeUseCase implements UpdateUserTypePort {

  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  public UserType execute(Long userTypeId, UserType patch) {
    return transactionPort.execute(() -> {
      UserType existing = userTypePersistencePort.findById(userTypeId)
          .orElseThrow(() -> new UserTypeNotFoundException(userTypeId));

      if (!patch.isAllowedName()) {
        throw new UserTypeInvalidNameException();
      }
      if (userTypePersistencePort.existsByNameIgnoreCase(patch.getName()) &&
          !patch.getName().equalsIgnoreCase(existing.getName())) {
        throw new UserTypeAlreadyExistsException();
      }

      existing.applyUpdate(patch);
      return userTypePersistencePort.save(existing);
    });
  }
}
