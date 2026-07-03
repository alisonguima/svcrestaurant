package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.mapper.UserPersistenceMapper;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.UserRepository;
import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserPostgres implements UserPersistencePort {

  private final UserRepository userRepository;

  @Override
  public User save(User user) {
    return UserPersistenceMapper.INSTANCE.toDomain(
        userRepository.save(UserPersistenceMapper.INSTANCE.toEntity(user)));
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id)
        .map(UserPersistenceMapper.INSTANCE::toDomain);
  }

  @Override
  public boolean existsByLogin(String login) {
    return userRepository.existsByLogin(login);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public long countByUserTypeId(Long userTypeId) {
    return userRepository.countByUserTypeId(userTypeId);
  }

  @Override
  public void deleteById(Long id) {
    userRepository.findById(id)
        .ifPresentOrElse(
            user -> userRepository.deleteById(id), () -> {
              throw new DefaultException(ErrorCode.USER_NOT_FOUND, ApiConstants.USER_NOT_FOUND_WITH_ID + id);
            });
  }
}
