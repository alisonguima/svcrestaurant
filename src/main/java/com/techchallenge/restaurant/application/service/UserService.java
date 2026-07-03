package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.input.UserUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserService implements UserUseCase {

  private final UserPersistencePort userPersistencePort;
  private final PasswordEncryptionPort passwordEncryptionPort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final UserTypePersistencePort userTypePersistencePort;

  @Override
  public User createUser(User user) {

    log.info("createUser - Receiving request to create user: email={}, login={}, userType={}",
        user.getEmail(), user.getLogin(), user.getUserType());

    ConflictValidatorUtils.throwIfExists(userPersistencePort.existsByEmail(user.getEmail()),
        "createUser - Email already in use: email={}", user.getEmail(),
        ErrorCode.EMAIL_ALREADY_EXISTS,
        ApiConstants.EMAIL_ALREADY_EXISTS);

    ConflictValidatorUtils.throwIfExists(userPersistencePort.existsByLogin(user.getLogin()),
        "createUser - Login already in use: login={}", user.getLogin(),
        ErrorCode.LOGIN_ALREADY_EXISTS,
        ApiConstants.LOGIN_ALREADY_EXISTS);

    user.setUserType(getUserType(user.getUserType().getId()));
    user.setPassword(passwordEncryptionPort.encode(user.getPassword()));
    user.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    User userSaved = userPersistencePort.save(user);

    log.info("createUser - User created successfully: id={}, email={}, login={}",
        userSaved.getId(), userSaved.getEmail(), userSaved.getLogin());
    return userSaved;
  }

  @Override
  public void updateUser(Long userId, User user) {
    log.info("updateUser - Updating user with userId={}", userId);

    User existingUser = getExistingUser(userId);

    Optional.ofNullable(user.getEmail())
        .ifPresent(email -> ConflictValidatorUtils.throwIfExists(
            userPersistencePort.existsByEmail(email) && !email.equals(existingUser.getEmail()),
            "updateUser - Email already in use: email={}", email,
            ErrorCode.EMAIL_ALREADY_EXISTS,
            ApiConstants.EMAIL_ALREADY_EXISTS));

    Optional.ofNullable(user.getLogin())
        .ifPresent(login -> ConflictValidatorUtils.throwIfExists(
            userPersistencePort.existsByLogin(login) && !login.equals(existingUser.getLogin()),
            "updateUser - Login already in use: login={}", login,
            ErrorCode.LOGIN_ALREADY_EXISTS,
            ApiConstants.LOGIN_ALREADY_EXISTS));

    userPersistencePort.save(mergeUserForUpdate(existingUser, user, userId));

    log.info("updateUser - User updated successfully: userId={}", userId);
  }

  @Override
  public void updatePassword(Long userId, String currentPassword, String newPassword) {
    log.info("updatePassword - Updating password for userId={}", userId);
    User existingUser = getExistingUser(userId);

    Optional.of(passwordEncryptionPort.matches(currentPassword, existingUser.getPassword()))
        .filter(Boolean::booleanValue)
        .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_PASSWORD, ApiConstants.INVALID_PASSWORD));

    Optional.of(passwordEncryptionPort.matches(newPassword, existingUser.getPassword()))
        .filter(match -> !match)
        .ifPresent(match -> {
          existingUser.setPassword(passwordEncryptionPort.encode(newPassword));
          existingUser.setLastUpdateAt(dateTimeProviderPort.nowUtc());
          userPersistencePort.save(existingUser);
          log.info("updatePassword - Password updated successfully for userId={}", userId);
        });
  }

  @Override
  public void assignUserType(Long userId, Long userTypeId) {
    log.info("assignUserType - Assigning userTypeId={} to userId={}", userTypeId, userId);

    User existingUser = getExistingUser(userId);
    existingUser.setUserType(getUserType(userTypeId));
    existingUser.setLastUpdateAt(dateTimeProviderPort.nowUtc());
    userPersistencePort.save(existingUser);
  }

  @Override
  public void deleteUser(Long userId) {
    log.info("deleteUser - Deleting user with userId={}", userId);

    getExistingUser(userId);
    userPersistencePort.deleteById(userId);

    log.info("deleteUser - User deleted successfully: userId={}", userId);
  }

  @Override
  public User getUser(Long userId) {

    log.info("getUser - Fetching user with userId={}", userId);

    return getExistingUser(userId);
  }

  private User getExistingUser(Long userId) {
    return userPersistencePort.findById(userId)
        .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND, ApiConstants.USER_NOT_FOUND_WITH_ID + userId));
  }

  private com.techchallenge.restaurant.application.domain.enums.UserType getUserType(Long userTypeId) {
    return userTypePersistencePort.findById(userTypeId)
        .orElseThrow(() -> new DefaultException(ErrorCode.USER_TYPE_NOT_FOUND, ApiConstants.USER_TYPE_NOT_FOUND_WITH_ID + userTypeId));
  }

  private User mergeUserForUpdate(User existingUser, User user, Long userId) {
    return User.builder()
        .id(userId)
        .name(Optional.ofNullable(user.getName()).orElse(existingUser.getName()))
        .email(Optional.ofNullable(user.getEmail()).orElse(existingUser.getEmail()))
        .login(Optional.ofNullable(user.getLogin()).orElse(existingUser.getLogin()))
        .password(existingUser.getPassword())
        .userType(Optional.ofNullable(user.getUserType()).map(u -> getUserType(u.getId())).orElse(existingUser.getUserType()))
        .lastUpdateAt(dateTimeProviderPort.nowUtc())
        .build();
  }
}
