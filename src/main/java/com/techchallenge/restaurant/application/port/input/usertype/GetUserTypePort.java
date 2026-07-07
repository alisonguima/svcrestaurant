package com.techchallenge.restaurant.application.port.input.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;

public interface GetUserTypePort {
  UserType execute(Long userTypeId);
}
