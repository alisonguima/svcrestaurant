package com.techchallenge.restaurant.application.port.output;

public interface PasswordEncryptionPort {

  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
