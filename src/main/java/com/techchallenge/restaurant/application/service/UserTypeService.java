package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.enums.UserType;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class UserTypeService implements UserTypeUseCase {

  private final UserTypePersistencePort userTypePersistencePort;
  private final UserPersistencePort userPersistencePort;

  @Override
  public UserType createUserType(UserType userType) {
    log.info("createUserType - Creating user type name={}", userType.getName());

    validateAllowedName(userType.getName());
    ConflictValidatorUtils.throwIfExists(
        userTypePersistencePort.existsByNameIgnoreCase(userType.getName()),
        "createUserType - User type already exists: name={}", userType.getName(),
        ErrorCode.USER_TYPE_ALREADY_EXISTS,
        ApiConstants.USER_TYPE_ALREADY_EXISTS);

    return userTypePersistencePort.save(userType);
  }

  @Override
  public UserType updateUserType(Long userTypeId, UserType userType) {
    UserType existing = getUserType(userTypeId);

    validateAllowedName(userType.getName());
    ConflictValidatorUtils.throwIfExists(
        userTypePersistencePort.existsByNameIgnoreCase(userType.getName())
            && !userType.getName().equalsIgnoreCase(existing.getName()),
        "updateUserType - User type already exists: name={}", userType.getName(),
        ErrorCode.USER_TYPE_ALREADY_EXISTS,
        ApiConstants.USER_TYPE_ALREADY_EXISTS);

    existing.setName(userType.getName());
    return userTypePersistencePort.save(existing);
  }

  @Override
  public UserType getUserType(Long userTypeId) {
    return userTypePersistencePort.findById(userTypeId)
        .orElseThrow(() -> new DefaultException(ErrorCode.USER_TYPE_NOT_FOUND, ApiConstants.USER_TYPE_NOT_FOUND_WITH_ID + userTypeId));
  }

  @Override
  public List<UserType> getUserTypes() {
    return userTypePersistencePort.findAll();
  }

  @Override
  public void deleteUserType(Long userTypeId) {
    if (isUserTypeInUse(userTypeId)) {
      throw new DefaultException(ErrorCode.USER_TYPE_IN_USE, ApiConstants.USER_TYPE_IN_USE);
    }

    userTypePersistencePort.deleteById(userTypeId);
  }

  private boolean isUserTypeInUse(Long userTypeId) {
    return userPersistencePort.countByUserTypeId(userTypeId) > 0L;
  }

  private void validateAllowedName(String name) {
    String normalized = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    boolean allowed = "dono de restaurante".equals(normalized) || "cliente".equals(normalized);

    if (!allowed) {
      throw new DefaultException(ErrorCode.USER_TYPE_INVALID_NAME, ApiConstants.USER_TYPE_INVALID_NAME);
    }
  }
}
