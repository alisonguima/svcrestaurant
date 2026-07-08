package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.EmailAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.InvalidPasswordException;
import com.techchallenge.restaurant.application.exception.LoginAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import com.techchallenge.restaurant.application.usecase.user.AssignUserTypeUseCase;
import com.techchallenge.restaurant.application.usecase.user.CreateUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.DeleteUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.GetUserUseCase;
import com.techchallenge.restaurant.application.usecase.user.UpdateUserPasswordUseCase;
import com.techchallenge.restaurant.application.usecase.user.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

  @Mock
  private UserPersistencePort userPersistencePort;

  @Mock
  private PasswordEncryptionPort passwordEncryptionPort;

  @Mock
  private DateTimeProviderPort dateTimeProviderPort;

  @Mock
  private UserTypePersistencePort userTypePersistencePort;

  @Mock
  private TransactionPort transactionPort;

  private CreateUserUseCase createUserUseCase;
  private UpdateUserUseCase updateUserUseCase;
  private UpdateUserPasswordUseCase updateUserPasswordUseCase;
  private AssignUserTypeUseCase assignUserTypeUseCase;
  private GetUserUseCase getUserUseCase;
  private DeleteUserUseCase deleteUserUseCase;

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");

  @BeforeEach
  void setUp() {
    createUserUseCase = new CreateUserUseCase(userPersistencePort, passwordEncryptionPort,
        dateTimeProviderPort, userTypePersistencePort, transactionPort);
    updateUserUseCase = new UpdateUserUseCase(userPersistencePort, dateTimeProviderPort,
        userTypePersistencePort, transactionPort);
    updateUserPasswordUseCase = new UpdateUserPasswordUseCase(userPersistencePort,
        passwordEncryptionPort, dateTimeProviderPort, transactionPort);
    assignUserTypeUseCase = new AssignUserTypeUseCase(userPersistencePort, userTypePersistencePort,
        dateTimeProviderPort, transactionPort);
    getUserUseCase = new GetUserUseCase(userPersistencePort, transactionPort);
    deleteUserUseCase = new DeleteUserUseCase(userPersistencePort, transactionPort);

    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private UserType createUserType(Long id, String name) {
    return new UserType(id, name);
  }

  private User createTestUser(Long id, String name, String email, String login, UserType userType) {
    return new User(id, name, email, login, "encodedPassword", userType, NOW);
  }

  @Test
  void shouldCreateUserSuccessfully() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User user = new User(null, "João Silva", "joao@email.com", "joao.silva", "Senha@123", userType, null);

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(false);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));
    when(passwordEncryptionPort.encode("Senha@123")).thenReturn("encodedPassword");
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    User savedUser = createTestUser(1L, "João Silva", "joao@email.com", "joao.silva", userType);
    when(userPersistencePort.save(any(User.class))).thenReturn(savedUser);

    User result = createUserUseCase.execute(user);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("João Silva", result.getName());
    assertEquals("joao@email.com", result.getEmail());
    assertEquals("joao.silva", result.getLogin());
    assertEquals(NOW, result.getLastUpdateAt());
    verify(userPersistencePort).existsByEmail("joao@email.com");
    verify(userPersistencePort).existsByLogin("joao.silva");
    verify(userTypePersistencePort).findById(1L);
    verify(passwordEncryptionPort).encode("Senha@123");
    verify(userPersistencePort).save(any(User.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserWithDuplicateEmail() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User user = new User(null, "João Silva", "joao@email.com", "joao.silva", "Senha@123", userType, null);

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(true);

    assertThrows(EmailAlreadyExistsException.class,
        () -> createUserUseCase.execute(user));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserWithDuplicateLogin() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User user = new User(null, "João Silva", "joao@email.com", "joao.silva", "Senha@123", userType, null);

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(true);

    assertThrows(LoginAlreadyExistsException.class,
        () -> createUserUseCase.execute(user));
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserWithNonExistentUserType() {
    UserType userType = createUserType(999L, UserType.CLIENTE);
    User user = new User(null, "João Silva", "joao@email.com", "joao.silva", "Senha@123", userType, null);

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(false);
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserTypeNotFoundException.class,
        () -> createUserUseCase.execute(user));
  }

  @Test
  void shouldUpdateUserSuccessfully() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, "João Silva Atualizado", "joao.novo@email.com", "joao.silva.novo", null, userType, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByEmail("joao.novo@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva.novo")).thenReturn(false);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserUseCase.execute(1L, updateData);

    verify(userPersistencePort).findById(1L);
    verify(userPersistencePort).existsByEmail("joao.novo@email.com");
    verify(userPersistencePort).existsByLogin("joao.silva.novo");
    verify(userPersistencePort).save(any(User.class));
  }

  @Test
  void shouldUpdateUserWithPartialData() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, "João Atualizado", null, null, null, null, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserUseCase.execute(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("João Atualizado", savedUser.getName());
    assertEquals("joao@email.com", savedUser.getEmail());
    assertEquals("joao.silva", savedUser.getLogin());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmail() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, null, "outro@email.com", null, null, null, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByEmail("outro@email.com")).thenReturn(true);

    assertThrows(EmailAlreadyExistsException.class,
        () -> updateUserUseCase.execute(1L, updateData));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithDuplicateLogin() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, null, null, "outro.login", null, null, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByLogin("outro.login")).thenReturn(true);

    assertThrows(LoginAlreadyExistsException.class,
        () -> updateUserUseCase.execute(1L, updateData));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentUser() {
    User updateData = new User(null, "Novo Nome", null, null, null, null, null);

    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> updateUserUseCase.execute(999L, updateData));
  }

  @Test
  void shouldUpdatePasswordSuccessfully() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("Senha@123", "encodedPassword")).thenReturn(true);
    when(passwordEncryptionPort.matches("NovaSenha@123", "encodedPassword")).thenReturn(false);
    when(passwordEncryptionPort.encode("NovaSenha@123")).thenReturn("encodedNewPassword");
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserPasswordUseCase.execute(1L, "Senha@123", "NovaSenha@123");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("encodedNewPassword", savedUser.getPassword());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingPasswordWithInvalidCurrentPassword() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("SenhaErrada@123", "encodedPassword")).thenReturn(false);

    assertThrows(InvalidPasswordException.class,
        () -> updateUserPasswordUseCase.execute(1L, "SenhaErrada@123", "NovaSenha@123"));
  }

  @Test
  void shouldNotUpdatePasswordWhenNewPasswordIsSameAsCurrent() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("Senha@123", "encodedPassword")).thenReturn(true);

    updateUserPasswordUseCase.execute(1L, "Senha@123", "Senha@123");

    verify(userPersistencePort).findById(1L);
  }

  @Test
  void shouldAssignUserTypeSuccessfully() {
    UserType oldUserType = createUserType(1L, UserType.CLIENTE);
    UserType newUserType = createUserType(2L, UserType.DONO);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", oldUserType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(2L)).thenReturn(Optional.of(newUserType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assignUserTypeUseCase.execute(1L, 2L);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals(2L, savedUser.getUserType().getId());
  }

  @Test
  void shouldThrowExceptionWhenAssigningNonExistentUserType() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserTypeNotFoundException.class,
        () -> assignUserTypeUseCase.execute(1L, 999L));
  }

  @Test
  void shouldDeleteUserSuccessfully() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));

    deleteUserUseCase.execute(1L);

    verify(userPersistencePort).findById(1L);
    verify(userPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> deleteUserUseCase.execute(999L));
  }

  @Test
  void shouldGetUserSuccessfully() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User user = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

    User result = getUserUseCase.execute(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("João", result.getName());
    assertEquals("joao@email.com", result.getEmail());
    verify(userPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    UserNotFoundException ex = assertThrows(UserNotFoundException.class,
        () -> getUserUseCase.execute(999L));

    assertTrue(ex.getMessage().contains("999"));
  }

  @Test
  void shouldUpdateUserWithNewUserType() {
    UserType oldUserType = createUserType(1L, UserType.CLIENTE);
    UserType newUserType = createUserType(2L, UserType.DONO);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", oldUserType);
    User updateData = new User(null, null, null, null, null, newUserType, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(2L)).thenReturn(Optional.of(newUserType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserUseCase.execute(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals(2L, savedUser.getUserType().getId());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithNonExistentUserType() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, null, null, null, null, new UserType(999L, null), null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserTypeNotFoundException.class,
        () -> updateUserUseCase.execute(1L, updateData));
  }

  @Test
  void shouldUpdateUserWithEmailSameAsExisting() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, null, "joao@email.com", null, null, null, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserUseCase.execute(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("joao@email.com", savedUser.getEmail());
  }

  @Test
  void shouldUpdateUserWithLoginSameAsExisting() {
    UserType userType = createUserType(1L, UserType.CLIENTE);
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = new User(null, null, null, "joao.silva", null, null, null);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    updateUserUseCase.execute(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("joao.silva", savedUser.getLogin());
  }

  @Test
  void shouldThrowExceptionWhenAssigningUserTypeToNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> assignUserTypeUseCase.execute(999L, 1L));
  }
}
