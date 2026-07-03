package com.techchallenge.restaurant.adapter.output.postgres.mapper;

import com.techchallenge.restaurant.adapter.output.postgres.model.MenuItemEntity;
import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {RestaurantPersistenceMapper.class})
public interface MenuItemPersistenceMapper {

  MenuItemPersistenceMapper INSTANCE = Mappers.getMapper(MenuItemPersistenceMapper.class);

  MenuItem toDomain(MenuItemEntity entity);

  MenuItemEntity toEntity(MenuItem domain);
}
