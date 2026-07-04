package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.mapper.UserTypePersistenceMapper;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.UserTypeRepository;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserTypePostgres implements UserTypePersistencePort {

  private final UserTypeRepository userTypeRepository;

  @Override
  public UserType save(UserType userType) {
    return UserTypePersistenceMapper.INSTANCE.toDomain(
        userTypeRepository.save(UserTypePersistenceMapper.INSTANCE.toEntity(userType)));
  }

  @Override
  public Optional<UserType> findById(Long id) {
    return userTypeRepository.findById(id)
        .map(UserTypePersistenceMapper.INSTANCE::toDomain);
  }

  @Override
  public List<UserType> findAll() {
    return userTypeRepository.findAll().stream()
        .map(UserTypePersistenceMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public boolean existsByNameIgnoreCase(String name) {
    return userTypeRepository.existsByNameIgnoreCase(name);
  }

  @Override
  public void deleteById(Long id) {
    userTypeRepository.deleteById(id);
  }
}
