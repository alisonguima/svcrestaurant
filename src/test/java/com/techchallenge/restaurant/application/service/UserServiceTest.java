package com.techchallenge.restaurant.application.service;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.DateTimeProviderPort;
import com.techchallenge.restaurant.application.port.output.PasswordEncryptionPort;
import com.techchallenge.restaurant.application.port.output.TransactionPort;
import com.techchallenge.restaurant.application.port.output.UserPersistencePort;
import com.techchallenge.restaurant.application.port.output.UserTypePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private UserService userService;

  private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-07-03T15:00:00Z");

  @BeforeEach
  void setUp() {
    when(transactionPort.execute(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    when(transactionPort.executeReadOnly(any())).thenAnswer(inv -> ((Supplier<?>) inv.getArgument(0)).get());
    doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(transactionPort).executeVoid(any());
  }

  private UserType createUserType(Long id, String name) {
    return UserType.builder().id(id).name(name).build();
  }

  private User createTestUser(Long id, String name, String email, String login, UserType userType) {
    return User.builder()
        .id(id)
        .name(name)
        .email(email)
        .login(login)
        .password("encodedPassword")
        .userType(userType)
        .lastUpdateAt(NOW)
        .build();
  }

  @Test
  void shouldCreateUserSuccessfully() {
    UserType userType = createUserType(1L, "Cliente");
    User user = User.builder()
        .name("João Silva")
        .email("joao@email.com")
        .login("joao.silva")
        .password("Senha@123")
        .userType(userType)
        .build();

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(false);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));
    when(passwordEncryptionPort.encode("Senha@123")).thenReturn("encodedPassword");
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    User savedUser = createTestUser(1L, "João Silva", "joao@email.com", "joao.silva", userType);
    when(userPersistencePort.save(any(User.class))).thenReturn(savedUser);

    User result = userService.createUser(user);

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
    UserType userType = createUserType(1L, "Cliente");
    User user = User.builder()
        .name("João Silva")
        .email("joao@email.com")
        .login("joao.silva")
        .password("Senha@123")
        .userType(userType)
        .build();

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.createUser(user));

    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getCode());
    assertEquals(ApiConstants.EMAIL_ALREADY_EXISTS, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserWithDuplicateLogin() {
    UserType userType = createUserType(1L, "Cliente");
    User user = User.builder()
        .name("João Silva")
        .email("joao@email.com")
        .login("joao.silva")
        .password("Senha@123")
        .userType(userType)
        .build();

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.createUser(user));

    assertEquals(ErrorCode.LOGIN_ALREADY_EXISTS, exception.getCode());
    assertEquals(ApiConstants.LOGIN_ALREADY_EXISTS, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCreatingUserWithNonExistentUserType() {
    UserType userType = createUserType(999L, "Cliente");
    User user = User.builder()
        .name("João Silva")
        .email("joao@email.com")
        .login("joao.silva")
        .password("Senha@123")
        .userType(userType)
        .build();

    when(userPersistencePort.existsByEmail("joao@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva")).thenReturn(false);
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.createUser(user));

    assertEquals(ErrorCode.USER_TYPE_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldUpdateUserSuccessfully() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .name("João Silva Atualizado")
        .email("joao.novo@email.com")
        .login("joao.silva.novo")
        .userType(userType)
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByEmail("joao.novo@email.com")).thenReturn(false);
    when(userPersistencePort.existsByLogin("joao.silva.novo")).thenReturn(false);
    when(userTypePersistencePort.findById(1L)).thenReturn(Optional.of(userType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updateUser(1L, updateData);

    verify(userPersistencePort).findById(1L);
    verify(userPersistencePort).existsByEmail("joao.novo@email.com");
    verify(userPersistencePort).existsByLogin("joao.silva.novo");
    verify(userPersistencePort).save(any(User.class));
  }

  @Test
  void shouldUpdateUserWithPartialData() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .name("João Atualizado")
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updateUser(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("João Atualizado", savedUser.getName());
    assertEquals("joao@email.com", savedUser.getEmail());
    assertEquals("joao.silva", savedUser.getLogin());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmail() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .email("outro@email.com")
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByEmail("outro@email.com")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.updateUser(1L, updateData));

    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithDuplicateLogin() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .login("outro.login")
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userPersistencePort.existsByLogin("outro.login")).thenReturn(true);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.updateUser(1L, updateData));

    assertEquals(ErrorCode.LOGIN_ALREADY_EXISTS, exception.getCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentUser() {
    User updateData = User.builder().name("Novo Nome").build();

    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.updateUser(999L, updateData));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldUpdatePasswordSuccessfully() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("Senha@123", "encodedPassword")).thenReturn(true);
    when(passwordEncryptionPort.matches("NovaSenha@123", "encodedPassword")).thenReturn(false);
    when(passwordEncryptionPort.encode("NovaSenha@123")).thenReturn("encodedNewPassword");
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updatePassword(1L, "Senha@123", "NovaSenha@123");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("encodedNewPassword", savedUser.getPassword());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingPasswordWithInvalidCurrentPassword() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("SenhaErrada@123", "encodedPassword")).thenReturn(false);

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.updatePassword(1L, "SenhaErrada@123", "NovaSenha@123"));

    assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    assertEquals(ApiConstants.INVALID_PASSWORD, exception.getMessage());
  }

  @Test
  void shouldNotUpdatePasswordWhenNewPasswordIsSameAsCurrent() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncryptionPort.matches("Senha@123", "encodedPassword")).thenReturn(true);
    when(passwordEncryptionPort.matches("Senha@123", "encodedPassword")).thenReturn(true);

    userService.updatePassword(1L, "Senha@123", "Senha@123");

    verify(userPersistencePort).findById(1L);
  }

  @Test
  void shouldAssignUserTypeSuccessfully() {
    UserType oldUserType = createUserType(1L, "Cliente");
    UserType newUserType = createUserType(2L, "Dono de Restaurante");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", oldUserType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(2L)).thenReturn(Optional.of(newUserType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.assignUserType(1L, 2L);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals(2L, savedUser.getUserType().getId());
  }

  @Test
  void shouldThrowExceptionWhenAssigningNonExistentUserType() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.assignUserType(1L, 999L));

    assertEquals(ErrorCode.USER_TYPE_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldDeleteUserSuccessfully() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));

    userService.deleteUser(1L);

    verify(userPersistencePort).findById(1L);
    verify(userPersistencePort).deleteById(1L);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.deleteUser(999L));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldGetUserSuccessfully() {
    UserType userType = createUserType(1L, "Cliente");
    User user = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

    User result = userService.getUser(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("João", result.getName());
    assertEquals("joao@email.com", result.getEmail());
    verify(userPersistencePort).findById(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.getUser(999L));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
    assertTrue(exception.getMessage().contains("999"));
  }

  @Test
  void shouldUpdateUserWithNewUserType() {
    UserType oldUserType = createUserType(1L, "Cliente");
    UserType newUserType = createUserType(2L, "Dono de Restaurante");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", oldUserType);
    User updateData = User.builder()
        .userType(newUserType)
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(2L)).thenReturn(Optional.of(newUserType));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updateUser(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals(2L, savedUser.getUserType().getId());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingUserWithNonExistentUserType() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .userType(UserType.builder().id(999L).build())
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userTypePersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.updateUser(1L, updateData));

    assertEquals(ErrorCode.USER_TYPE_NOT_FOUND, exception.getCode());
  }

  @Test
  void shouldUpdateUserWithEmailSameasExisting() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .email("joao@email.com")
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updateUser(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("joao@email.com", savedUser.getEmail());
  }

  @Test
  void shouldUpdateUserWithLoginSameAsExisting() {
    UserType userType = createUserType(1L, "Cliente");
    User existingUser = createTestUser(1L, "João", "joao@email.com", "joao.silva", userType);
    User updateData = User.builder()
        .login("joao.silva")
        .build();

    when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existingUser));
    when(dateTimeProviderPort.nowUtc()).thenReturn(NOW);
    when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.updateUser(1L, updateData);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userPersistencePort).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("joao.silva", savedUser.getLogin());
  }

  @Test
  void shouldThrowExceptionWhenAssigningUserTypeToNonExistentUser() {
    when(userPersistencePort.findById(999L)).thenReturn(Optional.empty());

    DefaultException exception = assertThrows(DefaultException.class,
        () -> userService.assignUserType(999L, 1L));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
  }
}
