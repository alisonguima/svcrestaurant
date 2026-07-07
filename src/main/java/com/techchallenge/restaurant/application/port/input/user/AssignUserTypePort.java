package com.techchallenge.restaurant.application.port.input.user;

public interface AssignUserTypePort {
  void execute(Long userId, Long userTypeId);
}
