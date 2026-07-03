package com.techchallenge.restaurant.adapter.output.postgres.mapper;

import com.techchallenge.restaurant.adapter.output.postgres.model.UserEntity;
import com.techchallenge.restaurant.application.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserTypePersistenceMapper.class)
public interface UserPersistenceMapper {

  UserPersistenceMapper INSTANCE = Mappers.getMapper(UserPersistenceMapper.class);

  User toDomain(UserEntity userEntity);

  UserEntity toEntity(User user);
}
