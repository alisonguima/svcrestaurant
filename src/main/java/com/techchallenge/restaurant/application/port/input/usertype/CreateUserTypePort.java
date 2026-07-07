package com.techchallenge.restaurant.application.port.input.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;

public interface CreateUserTypePort {
  UserType execute(UserType userType);
}
