package com.techchallenge.restaurant.application.port.input.user;

import com.techchallenge.restaurant.application.domain.user.User;

public interface GetUserPort {
  User execute(Long userId);
}
