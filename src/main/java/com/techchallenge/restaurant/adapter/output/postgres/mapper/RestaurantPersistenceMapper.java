package com.techchallenge.restaurant.adapter.output.postgres.mapper;

import com.techchallenge.restaurant.adapter.output.postgres.model.RestaurantEntity;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserPersistenceMapper.class})
public interface RestaurantPersistenceMapper {

  RestaurantPersistenceMapper INSTANCE = Mappers.getMapper(RestaurantPersistenceMapper.class);

  Restaurant toDomain(RestaurantEntity entity);

  RestaurantEntity toEntity(Restaurant domain);
}
