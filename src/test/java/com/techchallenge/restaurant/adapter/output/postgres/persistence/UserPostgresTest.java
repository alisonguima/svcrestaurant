package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.UserRepository;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPostgresTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserPostgres userPostgres;

  private UserTypeEntity userTypeEntity() {
    return UserTypeEntity.builder().id(1L).name(UserType.CLIENTE).build();
  }

  private UserEntity userEntity() {
    return UserEntity.builder()
        .id(1L)
        .name("João Silva")
        .email("joao@test.com")
        .login("joao")
        .password("$2a$10$hashed")
        .userType(userTypeEntity())
        .lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
  }

  private User userDomain() {
    return User.builder()
        .name("João Silva")
        .email("joao@test.com")
        .login("joao")
        .password("rawPassword")
        .userType(UserType.builder().id(1L).name(UserType.CLIENTE).build())
        .lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
  }

  @Test
  void save_shouldMapToEntitySaveAndReturnDomain() {
    when(userRepository.save(any())).thenReturn(userEntity());

    User result = userPostgres.save(userDomain());

    assertEquals(1L, result.getId());
    assertEquals("joao@test.com", result.getEmail());
    assertEquals("joao", result.getLogin());
    verify(userRepository).save(any(UserEntity.class));
  }

  @Test
  void findById_shouldReturnMappedDomain_whenEntityExists() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity()));

    Optional<User> result = userPostgres.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("joao@test.com", result.get().getEmail());
  }

  @Test
  void findById_shouldReturnEmpty_whenEntityDoesNotExist() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    Optional<User> result = userPostgres.findById(99L);

    assertTrue(result.isEmpty());
  }

  @Test
  void existsByLogin_shouldReturnTrue_whenLoginExists() {
    when(userRepository.existsByLogin("joao")).thenReturn(true);

    assertTrue(userPostgres.existsByLogin("joao"));
    verify(userRepository).existsByLogin("joao");
  }

  @Test
  void existsByLogin_shouldReturnFalse_whenLoginDoesNotExist() {
    when(userRepository.existsByLogin("unknown")).thenReturn(false);

    assertFalse(userPostgres.existsByLogin("unknown"));
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    when(userRepository.existsByEmail("joao@test.com")).thenReturn(true);

    assertTrue(userPostgres.existsByEmail("joao@test.com"));
    verify(userRepository).existsByEmail("joao@test.com");
  }

  @Test
  void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
    when(userRepository.existsByEmail("notfound@test.com")).thenReturn(false);

    assertFalse(userPostgres.existsByEmail("notfound@test.com"));
  }

  @Test
  void countByUserTypeId_shouldReturnRepositoryCount() {
    when(userRepository.countByUserTypeId(1L)).thenReturn(7L);

    assertEquals(7L, userPostgres.countByUserTypeId(1L));
    verify(userRepository).countByUserTypeId(1L);
  }

  @Test
  void deleteById_shouldDelegateToRepository() {
    doNothing().when(userRepository).deleteById(1L);

    userPostgres.deleteById(1L);

    verify(userRepository).deleteById(1L);
  }
}
