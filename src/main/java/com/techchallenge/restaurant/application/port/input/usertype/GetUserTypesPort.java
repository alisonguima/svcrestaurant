package com.techchallenge.restaurant.application.port.input.usertype;

import com.techchallenge.restaurant.application.domain.usertype.UserType;

import java.util.List;

public interface GetUserTypesPort {
  List<UserType> execute();
}
