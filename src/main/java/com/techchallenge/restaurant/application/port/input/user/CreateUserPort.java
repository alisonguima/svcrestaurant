package com.techchallenge.restaurant.application.port.input.user;

import com.techchallenge.restaurant.application.domain.user.User;

public interface CreateUserPort {
  User execute(User user);
}
