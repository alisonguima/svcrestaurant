package com.techchallenge.restaurant.adapter.output.postgres.mapper;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserTypePersistenceMapper {

  UserTypePersistenceMapper INSTANCE = Mappers.getMapper(UserTypePersistenceMapper.class);

  UserType toDomain(UserTypeEntity entity);

  UserTypeEntity toEntity(UserType userType);
}
