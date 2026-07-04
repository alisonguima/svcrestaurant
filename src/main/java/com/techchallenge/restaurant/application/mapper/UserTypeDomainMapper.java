package com.techchallenge.restaurant.application.mapper;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserTypeDomainMapper {

  UserTypeDomainMapper INSTANCE = Mappers.getMapper(UserTypeDomainMapper.class);

  @Mapping(target = "id", ignore = true)
  void merge(UserType source, @MappingTarget UserType target);
}
