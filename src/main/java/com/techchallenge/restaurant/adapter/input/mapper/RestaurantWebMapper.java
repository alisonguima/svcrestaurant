package com.techchallenge.restaurant.adapter.input.mapper;

import com.techchallenge.restaurant.adapter.input.request.restaurant.CreateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.request.restaurant.UpdateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.response.restaurant.CreateRestaurantResponse;
import com.techchallenge.restaurant.adapter.input.response.restaurant.GetRestaurantResponse;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RestaurantWebMapper {

  RestaurantWebMapper INSTANCE = Mappers.getMapper(RestaurantWebMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "owner", source = "ownerUserId")
  Restaurant createRequestToDomain(CreateRestaurantRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "owner", source = "ownerUserId")
  Restaurant updateRequestToDomain(UpdateRestaurantRequest request);

  default User map(Long ownerUserId) {
    if (ownerUserId == null) return null;
    return new User(ownerUserId, null, null, null, null, null, null);
  }

  @Mapping(target = "ownerId", source = "owner.id")
  @Mapping(target = "ownerName", source = "owner.name")
  CreateRestaurantResponse domainToCreateResponse(Restaurant restaurant);

  @Mapping(target = "ownerId", source = "owner.id")
  @Mapping(target = "ownerName", source = "owner.name")
  GetRestaurantResponse domainToGetResponse(Restaurant restaurant);
}
