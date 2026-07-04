package com.techchallenge.restaurant.application.mapper;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;

@Mapper
public interface MenuItemDomainMapper {

  MenuItemDomainMapper INSTANCE = Mappers.getMapper(MenuItemDomainMapper.class);

  // menuItem.id/name conflict with restaurant.id/name — specify source explicitly
  @Mapping(target = "id", source = "menuItem.id")
  @Mapping(target = "name", source = "menuItem.name")
  @Mapping(target = "description", source = "menuItem.description")
  @Mapping(target = "price", source = "menuItem.price")
  @Mapping(target = "onlyAtRestaurant", source = "menuItem.onlyAtRestaurant")
  @Mapping(target = "photoPath", source = "menuItem.photoPath")
  @Mapping(target = "restaurant", source = "restaurant")
  @Mapping(target = "lastUpdateAt", source = "now")
  MenuItem prepareForCreate(MenuItem menuItem, Restaurant restaurant, ZonedDateTime now);

  @Mapping(target = "id", source = "existing.id")
  @Mapping(target = "name", expression = "java(update.getName() != null ? update.getName() : existing.getName())")
  @Mapping(target = "description", expression = "java(update.getDescription() != null ? update.getDescription() : existing.getDescription())")
  @Mapping(target = "price", expression = "java(update.getPrice() != null ? update.getPrice() : existing.getPrice())")
  @Mapping(target = "onlyAtRestaurant", expression = "java(update.getOnlyAtRestaurant() != null ? update.getOnlyAtRestaurant() : existing.getOnlyAtRestaurant())")
  @Mapping(target = "photoPath", expression = "java(update.getPhotoPath() != null ? update.getPhotoPath() : existing.getPhotoPath())")
  @Mapping(target = "restaurant", source = "existing.restaurant")
  @Mapping(target = "lastUpdateAt", source = "now")
  MenuItem mergeForUpdate(MenuItem update, MenuItem existing, ZonedDateTime now);
}
