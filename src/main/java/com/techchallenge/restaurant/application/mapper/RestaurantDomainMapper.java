package com.techchallenge.restaurant.application.mapper;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;

@Mapper
public interface RestaurantDomainMapper {

  RestaurantDomainMapper INSTANCE = Mappers.getMapper(RestaurantDomainMapper.class);

  // restaurant.id/name conflict with owner.id/name — specify source explicitly
  @Mapping(target = "id", source = "restaurant.id")
  @Mapping(target = "name", source = "restaurant.name")
  @Mapping(target = "address", source = "restaurant.address")
  @Mapping(target = "cuisineType", source = "restaurant.cuisineType")
  @Mapping(target = "openingHours", source = "restaurant.openingHours")
  @Mapping(target = "owner", source = "owner")
  @Mapping(target = "lastUpdateAt", source = "now")
  Restaurant prepareForCreate(Restaurant restaurant, User owner, ZonedDateTime now);

  @Mapping(target = "id", source = "existing.id")
  @Mapping(target = "name", expression = "java(update.getName() != null ? update.getName() : existing.getName())")
  @Mapping(target = "address", expression = "java(update.getAddress() != null ? update.getAddress() : existing.getAddress())")
  @Mapping(target = "cuisineType", expression = "java(update.getCuisineType() != null ? update.getCuisineType() : existing.getCuisineType())")
  @Mapping(target = "openingHours", expression = "java(update.getOpeningHours() != null ? update.getOpeningHours() : existing.getOpeningHours())")
  @Mapping(target = "owner", source = "resolvedOwner")
  @Mapping(target = "lastUpdateAt", source = "now")
  Restaurant mergeForUpdate(Restaurant update, Restaurant existing, User resolvedOwner, ZonedDateTime now);
}
