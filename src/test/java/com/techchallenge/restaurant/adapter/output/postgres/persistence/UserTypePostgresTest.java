package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.UserTypeRepository;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTypePostgresTest {

  @Mock
  private UserTypeRepository userTypeRepository;

  @InjectMocks
  private UserTypePostgres userTypePostgres;

  private UserTypeEntity entity(Long id, String name) {
    return UserTypeEntity.builder().id(id).name(name).build();
  }

  @Test
  void save_shouldMapToEntitySaveAndReturnDomain() {
    when(userTypeRepository.save(any())).thenReturn(entity(1L, UserType.CLIENTE));

    UserType result = userTypePostgres.save(new UserType(null, UserType.CLIENTE));

    assertEquals(1L, result.getId());
    assertEquals(UserType.CLIENTE, result.getName());
    verify(userTypeRepository).save(any(UserTypeEntity.class));
  }

  @Test
  void findById_shouldReturnMappedDomain_whenEntityExists() {
    when(userTypeRepository.findById(1L)).thenReturn(Optional.of(entity(1L, UserType.CLIENTE)));

    Optional<UserType> result = userTypePostgres.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals(UserType.CLIENTE, result.get().getName());
  }

  @Test
  void findById_shouldReturnEmpty_whenEntityDoesNotExist() {
    when(userTypeRepository.findById(99L)).thenReturn(Optional.empty());

    Optional<UserType> result = userTypePostgres.findById(99L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_shouldReturnMappedList() {
    when(userTypeRepository.findAll()).thenReturn(List.of(
        entity(1L, UserType.CLIENTE),
        entity(2L, UserType.DONO)
    ));

    List<UserType> result = userTypePostgres.findAll();

    assertEquals(2, result.size());
    assertEquals(UserType.CLIENTE, result.get(0).getName());
    assertEquals(UserType.DONO, result.get(1).getName());
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoEntitiesExist() {
    when(userTypeRepository.findAll()).thenReturn(List.of());

    List<UserType> result = userTypePostgres.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void existsByNameIgnoreCase_shouldReturnTrue_whenNameExists() {
    when(userTypeRepository.existsByNameIgnoreCase(UserType.CLIENTE)).thenReturn(true);

    assertTrue(userTypePostgres.existsByNameIgnoreCase(UserType.CLIENTE));
    verify(userTypeRepository).existsByNameIgnoreCase(UserType.CLIENTE);
  }

  @Test
  void existsByNameIgnoreCase_shouldReturnFalse_whenNameDoesNotExist() {
    when(userTypeRepository.existsByNameIgnoreCase("Admin")).thenReturn(false);

    assertFalse(userTypePostgres.existsByNameIgnoreCase("Admin"));
  }

  @Test
  void deleteById_shouldDelegateToRepository() {
    doNothing().when(userTypeRepository).deleteById(1L);

    userTypePostgres.deleteById(1L);

    verify(userTypeRepository).deleteById(1L);
  }
}
