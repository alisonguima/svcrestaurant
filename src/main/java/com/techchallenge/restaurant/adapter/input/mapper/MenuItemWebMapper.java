package com.techchallenge.restaurant.adapter.input.mapper;

import com.techchallenge.restaurant.adapter.input.request.menu.CreateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.request.menu.UpdateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.response.menu.CreateMenuItemResponse;
import com.techchallenge.restaurant.adapter.input.response.menu.GetMenuItemResponse;
import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuItemWebMapper {

  MenuItemWebMapper INSTANCE = Mappers.getMapper(MenuItemWebMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "restaurant.id", ignore = true)
  MenuItem createRequestToDomain(CreateMenuItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastUpdateAt", ignore = true)
  @Mapping(target = "restaurant.id", ignore = true)
  MenuItem updateRequestToDomain(UpdateMenuItemRequest request);

  @Mapping(target = "restaurantId", source = "restaurant.id")
  @Mapping(target = "restaurantName", source = "restaurant.name")
  CreateMenuItemResponse domainToCreateResponse(MenuItem menuItem);

  @Mapping(target = "restaurantId", source = "restaurant.id")
  @Mapping(target = "restaurantName", source = "restaurant.name")
  GetMenuItemResponse domainToGetResponse(MenuItem menuItem);
}
