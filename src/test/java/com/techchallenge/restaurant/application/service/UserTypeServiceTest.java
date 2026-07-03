package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.enums.UserType;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTypeServiceTest {

  @Mock
  private UserTypePersistencePort userTypePersistencePort;

  @Mock
  private UserPersistencePort userPersistencePort;

  @InjectMocks
  private UserTypeService userTypeService;

  @Test
  void shouldCreateUserTypeWithClienteNameSuccessfully() {
    UserType userType = UserType.builder().name("Cliente").build();
    UserType userTypeSaved = UserType.builder().id(1L).name("Cliente").build();
    when(userTypePersistencePort.existsByNameIgnoreCase("Cliente")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = userTypeService.createUserType(userType);

    assertEquals(1L, result.getId());
    assertEquals("Cliente", result.getName());
    verify(userTypePersistencePort).existsByNameIgnoreCase("Cliente");
    verify(userTypePersistencePort).save(userType);
  }

  @Test
  void shouldCreateUserTypeWithDonoRestauranteNameSuccessfully() {
    UserType userType = UserType.builder().name("Dono de Restaurante").build();
    UserType userTypeSaved = UserType.builder().id(2L).name("Dono de Restaurante").build();
    when(userTypePersistencePort.existsByNameIgnoreCase("Dono de Restaurante")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = userTypeService.createUserType(userType);

    assertEquals(2L, result.getId());
    assertEquals("Dono de Restaurante", result.getName());
    verify(userTypePersistencePort).existsByNameIgnoreCase("Dono de Restaurante");
    verify(userTypePersistencePort).save(userType);
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithDuplicateName() {
    UserType userType = UserType.builder().name("Cliente").build();
    when(userTypePersistencePort.existsByNameIgnoreCase("Cliente")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.createUserType(userType));

    assertEquals(ErrorCode.USER_TYPE_ALREADY_EXISTS, exception.getCode());
    assertEquals(ApiConstants.USER_TYPE_ALREADY_EXISTS, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithInvalidName() {
    UserType userType = UserType.builder().name("Admin").build();

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.createUserType(userType));

    assertEquals(ErrorCode.USER_TYPE_INVALID_NAME, exception.getCode());
    assertEquals(ApiConstants.USER_TYPE_INVALID_NAME, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithNullName() {
    UserType userType = UserType.builder().name(null).build();

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.createUserType(userType));

    assertEquals(ErrorCode.USER_TYPE_INVALID_NAME, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithEmptyName() {
    UserType userType = UserType.builder().name("   ").build();

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.createUserType(userType));

    assertEquals(ErrorCode.USER_TYPE_INVALID_NAME, exception.getCode());
  }

  @Test
  void shouldUpdateUserTypeSuccessfully() {
    UserType existingUserType = UserType.builder().id(1L).name("Cliente").build();
    UserType updateData = UserType.builder().name("Dono de Restaurante").build();
    UserType updatedUserType = UserType.builder().id(1L).name("Dono de Restaurante").build();

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase("Dono de Restaurante")).thenReturn(false);
    when(userTypePersistencePort.save(existingUserType)).thenReturn(updatedUserType);

    UserType result = userTypeService.updateUserType(1L, updateData);

    assertEquals(1L, result.getId());
    assertEquals("Dono de Restaurante", result.getName());
    verify(userTypePersistencePort).findById(1L);
    verify(userTypePersistencePort).existsByNameIgnoreCase("Dono de Restaurante");
    verify(userTypePersistencePort).save(existingUserType);
  }

  @Test
  void shouldUpdateUserTypeWithSameName() {
    UserType existingUserType = UserType.builder().id(1L).name("Cliente").build();
    UserType updateData = UserType.builder().name("Cliente").build();

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase("Cliente")).thenReturn(true);
    when(userTypePersistencePort.save(existingUserType)).thenReturn(existingUserType);

    UserType result = userTypeService.updateUserType(1L, updateData);

    assertEquals("Cliente", result.getName());
    verify(userTypePersistencePort).findById(1L);
    verify(userTypePersistencePort).existsByNameIgnoreCase("Cliente");
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserTypeWithDuplicateName() {
    UserType existingUserType = UserType.builder().id(1L).name("Cliente").build();
    UserType updateData = UserType.builder().name("Dono de Restaurante").build();

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase("Dono de Restaurante")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.updateUserType(1L, updateData));

    assertEquals(ErrorCode.USER_TYPE_ALREADY_EXISTS, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserTypeWithInvalidName() {
    UserType existingUserType = UserType.builder().id(1L).name("Cliente").build();
    UserType updateData = UserType.builder().name("SuperAdmin").build();

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.updateUserType(1L, updateData));

    assertEquals(ErrorCode.USER_TYPE_INVALID_NAME, exception.getCode());
  }

  @Test
  void shouldGetUserTypeSuccessfully() {
    UserType userType = UserType.builder().id(1L).name("Cliente").build();
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));

    UserType result = userTypeService.getUserType(1L);

    assertEquals(1L, result.getId());
    assertEquals("Cliente", result.getName());
    verify(userTypePersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentUserType() {
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.getUserType(999L));

    assertEquals(ErrorCode.USER_TYPE_NOT_FOUND, exception.getCode());
    assertTrue(exception.getMessage().contains("999"));
  }

  @Test
  void shouldGetAllUserTypes() {
    UserType userType1 = UserType.builder().id(1L).name("Cliente").build();
    UserType userType2 = UserType.builder().id(2L).name("Dono de Restaurante").build();
    List<UserType> userTypes = Arrays.asList(userType1, userType2);

    when(userTypePersistencePort.findAll()).thenReturn(userTypes);

    List<UserType> result = userTypeService.getUserTypes();

    assertEquals(2, result.size());
    assertEquals(userType1, result.get(0));
    assertEquals(userType2, result.get(1));
    verify(userTypePersistencePort).findAll();
  }

  @Test
  void shouldGetEmptyListWhenNoUserTypesExist() {
    when(userTypePersistencePort.findAll()).thenReturn(List.of());

    List<UserType> result = userTypeService.getUserTypes();

    assertTrue(result.isEmpty());
    verify(userTypePersistencePort).findAll();
  }

  @Test
  void shouldDeleteUserTypeSuccessfully() {
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(0L);

    userTypeService.deleteUserType(1L);

    verify(userPersistencePort).countByUserTypeId(1L);
    verify(userTypePersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingUserTypeInUse() {
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(5L);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userTypeService.deleteUserType(1L));

    assertEquals(ErrorCode.USER_TYPE_IN_USE, exception.getCode());
    assertEquals(ApiConstants.USER_TYPE_IN_USE, exception.getMessage());
    verify(userPersistencePort).countByUserTypeId(1L);
  }

  @Test
  void shouldValidateAllowedNameWithClienteIgnoreCase() {
    UserType userType = UserType.builder().name("CLIENTE").build();
    UserType userTypeSaved = UserType.builder().id(1L).name("CLIENTE").build();
    when(userTypePersistencePort.existsByNameIgnoreCase("CLIENTE")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = userTypeService.createUserType(userType);

    assertEquals(1L, result.getId());
    verify(userTypePersistencePort).existsByNameIgnoreCase("CLIENTE");
  }

  @Test
  void shouldValidateAllowedNameWithDonoRestauranteIgnoreCase() {
    UserType userType = UserType.builder().name("DONO DE RESTAURANTE").build();
    UserType userTypeSaved = UserType.builder().id(1L).name("DONO DE RESTAURANTE").build();
    when(userTypePersistencePort.existsByNameIgnoreCase("DONO DE RESTAURANTE")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = userTypeService.createUserType(userType);

    assertEquals(1L, result.getId());
    verify(userTypePersistencePort).existsByNameIgnoreCase("DONO DE RESTAURANTE");
  }

  @Test
  void shouldReturnFalseWhenUserTypeIsNotInUse() {
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(0L);

    userTypeService.deleteUserType(1L);

    verify(userPersistencePort).countByUserTypeId(1L);
  }
}
