package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.mapper.UserDomainMapper;
import com.techchallenge.restaurant.application.port.input.UserUseCase;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.util.ConflictValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService implements UserUseCase {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserPersistencePort userPersistencePort;
  private final PasswordEncryptionPort passwordEncryptionPort;
  private final DateTimeProviderPort dateTimeProviderPort;
  private final UserTypePersistencePort userTypePersistencePort;
  private final TransactionPort transactionPort;

  @Override
  public User createUser(User user) {
    return transactionPort.execute(() -> {
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

      UserType resolvedType = user.getUserType() != null
          ? getUserType(user.getUserType().getId())
          : null;
      String encodedPassword = passwordEncryptionPort.encode(user.getPassword());
      User userSaved = userPersistencePort.save(
          UserDomainMapper.INSTANCE.prepareForCreate(user, resolvedType, encodedPassword, dateTimeProviderPort.nowUtc()));

      log.info("createUser - User created successfully: id={}, email={}, login={}",
          userSaved.getId(), userSaved.getEmail(), userSaved.getLogin());
      return userSaved;
    });
  }

  @Override
  public void updateUser(Long userId, User user) {
    transactionPort.executeVoid(() -> {
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

      UserType resolvedType = user.getUserType() != null
          ? getUserType(user.getUserType().getId())
          : existingUser.getUserType();

      userPersistencePort.save(
          UserDomainMapper.INSTANCE.mergeForUpdate(user, existingUser, resolvedType, dateTimeProviderPort.nowUtc()));

      log.info("updateUser - User updated successfully: userId={}", userId);
    });
  }

  @Override
  public void updatePassword(Long userId, String currentPassword, String newPassword) {
    transactionPort.executeVoid(() -> {
      log.info("updatePassword - Updating password for userId={}", userId);
      User existingUser = getExistingUser(userId);

      Optional.of(passwordEncryptionPort.matches(currentPassword, existingUser.getPassword()))
          .filter(Boolean::booleanValue)
          .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_PASSWORD, ApiConstants.INVALID_PASSWORD));

      Optional.of(passwordEncryptionPort.matches(newPassword, existingUser.getPassword()))
          .filter(match -> !match)
          .ifPresent(match -> {
            userPersistencePort.save(UserDomainMapper.INSTANCE.withNewPassword(
                existingUser, passwordEncryptionPort.encode(newPassword), dateTimeProviderPort.nowUtc()));
            log.info("updatePassword - Password updated successfully for userId={}", userId);
          });
    });
  }

  @Override
  public void assignUserType(Long userId, Long userTypeId) {
    transactionPort.executeVoid(() -> {
      log.info("assignUserType - Assigning userTypeId={} to userId={}", userTypeId, userId);
      User existingUser = getExistingUser(userId);
      userPersistencePort.save(UserDomainMapper.INSTANCE.withNewUserType(
          existingUser, getUserType(userTypeId), dateTimeProviderPort.nowUtc()));
    });
  }

  @Override
  public void deleteUser(Long userId) {
    transactionPort.executeVoid(() -> {
      log.info("deleteUser - Deleting user with userId={}", userId);
      getExistingUser(userId);
      userPersistencePort.deleteById(userId);
      log.info("deleteUser - User deleted successfully: userId={}", userId);
    });
  }

  @Override
  public User getUser(Long userId) {
    log.info("getUser - Fetching user with userId={}", userId);
    return transactionPort.executeReadOnly(() -> getExistingUser(userId));
  }

  private User getExistingUser(Long userId) {
    return userPersistencePort.findById(userId)
        .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND, ApiConstants.USER_NOT_FOUND_WITH_ID + userId));
  }

  private UserType getUserType(Long userTypeId) {
    return userTypePersistencePort.findById(userTypeId)
        .orElseThrow(() -> new DefaultException(ErrorCode.USER_TYPE_NOT_FOUND, ApiConstants.USER_TYPE_NOT_FOUND_WITH_ID + userTypeId));
  }
}
