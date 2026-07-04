package com.techchallenge.restaurant.application.mapper;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;

@Mapper
public interface UserDomainMapper {

  UserDomainMapper INSTANCE = Mappers.getMapper(UserDomainMapper.class);

  // user.id/name/email/login conflict with userType.id/name — specify source explicitly
  @Mapping(target = "id", source = "user.id")
  @Mapping(target = "name", source = "user.name")
  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "login", source = "user.login")
  @Mapping(target = "userType", source = "userType")
  @Mapping(target = "password", source = "encodedPassword")
  @Mapping(target = "lastUpdateAt", source = "now")
  User prepareForCreate(User user, UserType userType, String encodedPassword, ZonedDateTime now);

  // no ambiguity: encodedPassword is a plain String, now is ZonedDateTime
  @Mapping(target = "password", source = "encodedPassword")
  @Mapping(target = "lastUpdateAt", source = "now")
  User withNewPassword(User user, String encodedPassword, ZonedDateTime now);

  // user.id/name conflict with userType.id/name — specify source explicitly
  @Mapping(target = "id", source = "user.id")
  @Mapping(target = "name", source = "user.name")
  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "login", source = "user.login")
  @Mapping(target = "password", source = "user.password")
  @Mapping(target = "userType", source = "userType")
  @Mapping(target = "lastUpdateAt", source = "now")
  User withNewUserType(User user, UserType userType, ZonedDateTime now);

  @Mapping(target = "id", source = "existing.id")
  @Mapping(target = "name", expression = "java(update.getName() != null ? update.getName() : existing.getName())")
  @Mapping(target = "email", expression = "java(update.getEmail() != null ? update.getEmail() : existing.getEmail())")
  @Mapping(target = "login", expression = "java(update.getLogin() != null ? update.getLogin() : existing.getLogin())")
  @Mapping(target = "password", source = "existing.password")
  @Mapping(target = "userType", source = "resolvedType")
  @Mapping(target = "lastUpdateAt", source = "now")
  User mergeForUpdate(User update, User existing, UserType resolvedType, ZonedDateTime now);
}
