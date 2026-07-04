package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.mapper.UserTypeDomainMapper;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class UserTypeService implements UserTypeUseCase {

  private static final Logger log = LoggerFactory.getLogger(UserTypeService.class);

  private static final Set<String> ALLOWED_USER_TYPE_NAMES =
      Set.of(ApiConstants.USER_TYPE_DONO_RESTAURANTE, ApiConstants.USER_TYPE_CLIENTE);

  private final UserTypePersistencePort userTypePersistencePort;
  private final UserPersistencePort userPersistencePort;
  private final TransactionPort transactionPort;

  @Override
  public UserType createUserType(UserType userType) {
    return transactionPort.execute(() -> {
      log.info("createUserType - Creating user type name={}", userType.getName());

      validateAllowedName(userType.getName());
      ConflictValidatorUtils.throwIfExists(
          userTypePersistencePort.existsByNameIgnoreCase(userType.getName()),
          "createUserType - User type already exists: name={}", userType.getName(),
          ErrorCode.USER_TYPE_ALREADY_EXISTS,
          ApiConstants.USER_TYPE_ALREADY_EXISTS);

      return userTypePersistencePort.save(userType);
    });
  }

  @Override
  public UserType updateUserType(Long userTypeId, UserType userType) {
    return transactionPort.execute(() -> {
      UserType existing = getUserType(userTypeId);

      validateAllowedName(userType.getName());
      ConflictValidatorUtils.throwIfExists(
          userTypePersistencePort.existsByNameIgnoreCase(userType.getName())
              && !userType.getName().equalsIgnoreCase(existing.getName()),
          "updateUserType - User type already exists: name={}", userType.getName(),
          ErrorCode.USER_TYPE_ALREADY_EXISTS,
          ApiConstants.USER_TYPE_ALREADY_EXISTS);

      UserTypeDomainMapper.INSTANCE.merge(userType, existing);
      return userTypePersistencePort.save(existing);
    });
  }

  @Override
  public UserType getUserType(Long userTypeId) {
    return transactionPort.executeReadOnly(() ->
        userTypePersistencePort.findById(userTypeId)
            .orElseThrow(() -> new DefaultException(ErrorCode.USER_TYPE_NOT_FOUND, ApiConstants.USER_TYPE_NOT_FOUND_WITH_ID + userTypeId)));
  }

  @Override
  public List<UserType> getUserTypes() {
    return transactionPort.executeReadOnly(userTypePersistencePort::findAll);
  }

  @Override
  public void deleteUserType(Long userTypeId) {
    transactionPort.executeVoid(() -> {
      getUserType(userTypeId);
      if (isUserTypeInUse(userTypeId)) {
        throw new DefaultException(ErrorCode.USER_TYPE_IN_USE, ApiConstants.USER_TYPE_IN_USE);
      }
      userTypePersistencePort.deleteById(userTypeId);
    });
  }

  private boolean isUserTypeInUse(Long userTypeId) {
    return userPersistencePort.countByUserTypeId(userTypeId) > 0L;
  }

  private void validateAllowedName(String name) {
    boolean allowed = name != null
        && ALLOWED_USER_TYPE_NAMES.stream().anyMatch(name.trim()::equalsIgnoreCase);

    if (!allowed) {
      throw new DefaultException(ErrorCode.USER_TYPE_INVALID_NAME, ApiConstants.USER_TYPE_INVALID_NAME);
    }
  }
}
