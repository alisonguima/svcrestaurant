package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.exception.UserTypeAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserTypeInUseException;
import com.techchallenge.restaurant.application.exception.UserTypeInvalidNameException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.usecase.usertype.CreateUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.DeleteUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.GetUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.GetUserTypesUseCase;
import com.techchallenge.restaurant.application.usecase.usertype.UpdateUserTypeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserTypeServiceTest {

  @Mock
  private UserTypePersistencePort userTypePersistencePort;

  @Mock
  private UserPersistencePort userPersistencePort;

  @Mock
  private TransactionPort transactionPort;

  private CreateUserTypeUseCase createUserTypeUseCase;
  private UpdateUserTypeUseCase updateUserTypeUseCase;
  private GetUserTypeUseCase getUserTypeUseCase;
  private GetUserTypesUseCase getUserTypesUseCase;
  private DeleteUserTypeUseCase deleteUserTypeUseCase;

  @BeforeEach
  void setUp() {
    createUserTypeUseCase = new CreateUserTypeUseCase(userTypePersistencePort, transactionPort);
    updateUserTypeUseCase = new UpdateUserTypeUseCase(userTypePersistencePort, transactionPort);
    getUserTypeUseCase = new GetUserTypeUseCase(userTypePersistencePort, transactionPort);
    getUserTypesUseCase = new GetUserTypesUseCase(userTypePersistencePort, transactionPort);
    deleteUserTypeUseCase = new DeleteUserTypeUseCase(userTypePersistencePort, userPersistencePort, transactionPort);

    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  @Test
  void shouldCreateUserTypeWithClienteNameSuccessfully() {
    UserType userType = new UserType(null, UserType.CLIENTE);
    UserType userTypeSaved = new UserType(1L, UserType.CLIENTE);
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.CLIENTE)).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = createUserTypeUseCase.execute(userType);

    assertEquals(1L, result.getId());
    assertEquals(UserType.CLIENTE, result.getName());
    verify(userTypePersistencePort).existsByNameIgnoreCase(UserType.CLIENTE);
    verify(userTypePersistencePort).save(userType);
  }

  @Test
  void shouldCreateUserTypeWithDonoRestauranteNameSuccessfully() {
    UserType userType = new UserType(null, UserType.DONO);
    UserType userTypeSaved = new UserType(2L, UserType.DONO);
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.DONO)).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = createUserTypeUseCase.execute(userType);

    assertEquals(2L, result.getId());
    assertEquals(UserType.DONO, result.getName());
    verify(userTypePersistencePort).existsByNameIgnoreCase(UserType.DONO);
    verify(userTypePersistencePort).save(userType);
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithDuplicateName() {
    UserType userType = new UserType(null, UserType.CLIENTE);
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.CLIENTE)).thenReturn(true);

    assertThrows(UserTypeAlreadyExistsException.class,
        () -> createUserTypeUseCase.execute(userType));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithInvalidName() {
    UserType userType = new UserType(null, "Admin");

    assertThrows(UserTypeInvalidNameException.class,
        () -> createUserTypeUseCase.execute(userType));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithNullName() {
    UserType userType = new UserType(null, null);

    assertThrows(UserTypeInvalidNameException.class,
        () -> createUserTypeUseCase.execute(userType));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserTypeWithEmptyName() {
    UserType userType = new UserType(null, "   ");

    assertThrows(UserTypeInvalidNameException.class,
        () -> createUserTypeUseCase.execute(userType));
  }

  @Test
  void shouldUpdateUserTypeSuccessfully() {
    UserType existingUserType = new UserType(1L, UserType.CLIENTE);
    UserType updateData = new UserType(null, UserType.DONO);
    UserType updatedUserType = new UserType(1L, UserType.DONO);

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.DONO)).thenReturn(false);
    when(userTypePersistencePort.save(existingUserType)).thenReturn(updatedUserType);

    UserType result = updateUserTypeUseCase.execute(1L, updateData);

    assertEquals(1L, result.getId());
    assertEquals(UserType.DONO, result.getName());
    verify(userTypePersistencePort).findById(1L);
    verify(userTypePersistencePort).existsByNameIgnoreCase(UserType.DONO);
    verify(userTypePersistencePort).save(existingUserType);
  }

  @Test
  void shouldUpdateUserTypeWithSameName() {
    UserType existingUserType = new UserType(1L, UserType.CLIENTE);
    UserType updateData = new UserType(null, UserType.CLIENTE);

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.CLIENTE)).thenReturn(true);
    when(userTypePersistencePort.save(existingUserType)).thenReturn(existingUserType);

    UserType result = updateUserTypeUseCase.execute(1L, updateData);

    assertEquals(UserType.CLIENTE, result.getName());
    verify(userTypePersistencePort).findById(1L);
    verify(userTypePersistencePort).existsByNameIgnoreCase(UserType.CLIENTE);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserTypeWithDuplicateName() {
    UserType existingUserType = new UserType(1L, UserType.CLIENTE);
    UserType updateData = new UserType(null, UserType.DONO);

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));
    when(userTypePersistencePort.existsByNameIgnoreCase(UserType.DONO)).thenReturn(true);

    assertThrows(UserTypeAlreadyExistsException.class,
        () -> updateUserTypeUseCase.execute(1L, updateData));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserTypeWithInvalidName() {
    UserType existingUserType = new UserType(1L, UserType.CLIENTE);
    UserType updateData = new UserType(null, "SuperAdmin");

    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existingUserType));

    assertThrows(UserTypeInvalidNameException.class,
        () -> updateUserTypeUseCase.execute(1L, updateData));
  }

  @Test
  void shouldGetUserTypeSuccessfully() {
    UserType userType = new UserType(1L, UserType.CLIENTE);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));

    UserType result = getUserTypeUseCase.execute(1L);

    assertEquals(1L, result.getId());
    assertEquals(UserType.CLIENTE, result.getName());
    verify(userTypePersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentUserType() {
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    UserTypeNotFoundException ex = assertThrows(UserTypeNotFoundException.class,
        () -> getUserTypeUseCase.execute(999L));

    assertTrue(ex.getMessage().contains("999"));
  }

  @Test
  void shouldGetAllUserTypes() {
    UserType userType1 = new UserType(1L, UserType.CLIENTE);
    UserType userType2 = new UserType(2L, UserType.DONO);
    List<UserType> userTypes = Arrays.asList(userType1, userType2);

    when(userTypePersistencePort.findAll()).thenReturn(userTypes);

    List<UserType> result = getUserTypesUseCase.execute();

    assertEquals(2, result.size());
    assertEquals(userType1, result.get(0));
    assertEquals(userType2, result.get(1));
    verify(userTypePersistencePort).findAll();
  }

  @Test
  void shouldGetEmptyListWhenNoUserTypesExist() {
    when(userTypePersistencePort.findAll()).thenReturn(List.of());

    List<UserType> result = getUserTypesUseCase.execute();

    assertTrue(result.isEmpty());
    verify(userTypePersistencePort).findAll();
  }

  @Test
  void shouldDeleteUserTypeSuccessfully() {
    UserType existing = new UserType(1L, UserType.CLIENTE);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existing));
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(0L);

    deleteUserTypeUseCase.execute(1L);

    verify(userTypePersistencePort).findById(1L);
    verify(userPersistencePort).countByUserTypeId(1L);
    verify(userTypePersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingUserTypeInUse() {
    UserType existing = new UserType(1L, UserType.CLIENTE);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existing));
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(5L);

    assertThrows(UserTypeInUseException.class,
        () -> deleteUserTypeUseCase.execute(1L));

    verify(userTypePersistencePort).findById(1L);
    verify(userPersistencePort).countByUserTypeId(1L);
  }

  @Test
  void shouldValidateAllowedNameWithClienteIgnoreCase() {
    UserType userType = new UserType(null, "CLIENTE");
    UserType userTypeSaved = new UserType(1L, "CLIENTE");
    when(userTypePersistencePort.existsByNameIgnoreCase("CLIENTE")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = createUserTypeUseCase.execute(userType);

    assertEquals(1L, result.getId());
    verify(userTypePersistencePort).existsByNameIgnoreCase("CLIENTE");
  }

  @Test
  void shouldValidateAllowedNameWithDonoRestauranteIgnoreCase() {
    UserType userType = new UserType(null, "DONO");
    UserType userTypeSaved = new UserType(1L, "DONO");
    when(userTypePersistencePort.existsByNameIgnoreCase("DONO")).thenReturn(false);
    when(userTypePersistencePort.save(userType)).thenReturn(userTypeSaved);

    UserType result = createUserTypeUseCase.execute(userType);

    assertEquals(1L, result.getId());
    verify(userTypePersistencePort).existsByNameIgnoreCase("DONO");
  }

  @Test
  void shouldReturnFalseWhenUserTypeIsNotInUse() {
    UserType existing = new UserType(1L, UserType.CLIENTE);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(existing));
    when(userPersistencePort.countByUserTypeId(1L)).thenReturn(0L);

    deleteUserTypeUseCase.execute(1L);

    verify(userTypePersistencePort).findById(1L);
    verify(userPersistencePort).countByUserTypeId(1L);
  }
}
