package com.techchallenge.restaurant.adapter.output.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BcryptPasswordEncryptionAdapterTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private BcryptPasswordEncryptionAdapter adapter;

  @Test
  void encode_shouldDelegateToPasswordEncoderAndReturnHash() {
    when(passwordEncoder.encode("rawPass")).thenReturn("$2a$10$hashedValue");

    String result = adapter.encode("rawPass");

    assertEquals("$2a$10$hashedValue", result);
    verify(passwordEncoder).encode("rawPass");
  }

  @Test
  void matches_shouldReturnTrue_whenPasswordMatchesHash() {
    when(passwordEncoder.matches("rawPass", "$2a$10$hashedValue")).thenReturn(true);

    assertTrue(adapter.matches("rawPass", "$2a$10$hashedValue"));
    verify(passwordEncoder).matches("rawPass", "$2a$10$hashedValue");
  }

  @Test
  void matches_shouldReturnFalse_whenPasswordDoesNotMatchHash() {
    when(passwordEncoder.matches("wrongPass", "$2a$10$hashedValue")).thenReturn(false);

    assertFalse(adapter.matches("wrongPass", "$2a$10$hashedValue"));
    verify(passwordEncoder).matches("wrongPass", "$2a$10$hashedValue");
  }
}
