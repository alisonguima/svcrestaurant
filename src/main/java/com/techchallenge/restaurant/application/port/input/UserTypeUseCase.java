package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.enums.UserType;

import java.util.List;

public interface UserTypeUseCase {

  UserType createUserType(UserType userType);
  UserType updateUserType(Long userTypeId, UserType userType);
  UserType getUserType(Long userTypeId);
  List<UserType> getUserTypes();
  void deleteUserType(Long userTypeId);
}
