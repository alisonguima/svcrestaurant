package com.techchallenge.restaurant.adapter.input.mapper;

import com.techchallenge.restaurant.adapter.input.request.user.CreateUserRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserRequest;
import com.techchallenge.restaurant.adapter.input.response.user.CreateUserResponse;
import com.techchallenge.restaurant.adapter.input.response.user.GetUserResponse;
import com.techchallenge.restaurant.adapter.input.response.usertype.UserTypeResponse;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.domain.enums.UserType;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWebMapper {

  UserWebMapper INSTANCE = Mappers.getMapper(UserWebMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "userType", source = "userTypeId")
  User createUserRequestToDomain(CreateUserRequest createUserRequest);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "userType", source = "userTypeId")
  User updateUserRequestToDomain(UpdateUserRequest updateUserRequest);

  CreateUserResponse domainToCreateUserResponse(User user);

  GetUserResponse domainToGetUserResponse(User user);

  UserTypeResponse domainToUserTypeResponse(UserType userType);

  default UserType map(Long userTypeId) {
    if (userTypeId == null) {
      return null;
    }

    return UserType.builder()
        .id(userTypeId)
        .build();
  }
}
