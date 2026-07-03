package com.techchallenge.restaurant.adapter.input.mapper;

import com.techchallenge.restaurant.adapter.input.request.usertype.CreateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.request.usertype.UpdateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.response.usertype.CreateUserTypeResponse;
import com.techchallenge.restaurant.adapter.input.response.usertype.GetUserTypeResponse;
import com.techchallenge.restaurant.application.domain.enums.UserType;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserTypeWebMapper {

  UserTypeWebMapper INSTANCE = Mappers.getMapper(UserTypeWebMapper.class);

  @Mapping(target = "id", ignore = true)
  UserType createUserTypeRequestToDomain(CreateUserTypeRequest request);

  @Mapping(target = "id", ignore = true)
  UserType updateUserTypeRequestToDomain(UpdateUserTypeRequest request);

  CreateUserTypeResponse domainToCreateUserTypeResponse(UserType userType);

  GetUserTypeResponse domainToGetUserTypeResponse(UserType userType);
}
