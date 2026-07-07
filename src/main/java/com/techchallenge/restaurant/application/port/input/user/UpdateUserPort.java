package com.techchallenge.restaurant.application.port.input.user;

import com.techchallenge.restaurant.application.domain.user.User;

public interface UpdateUserPort {
  void execute(Long userId, User patch);
}
