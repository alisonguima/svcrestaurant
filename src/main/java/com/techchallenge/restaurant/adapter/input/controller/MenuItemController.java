package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.MenuItemWebMapper;
import com.techchallenge.restaurant.adapter.input.request.menu.CreateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.request.menu.UpdateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.response.menu.CreateMenuItemResponse;
import com.techchallenge.restaurant.adapter.input.response.menu.GetMenuItemResponse;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

  private final MenuItemUseCase menuItemUseCase;

  @PostMapping
  public ResponseEntity<CreateMenuItemResponse> create(@PathVariable Long restaurantId, @Valid @RequestBody CreateMenuItemRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(MenuItemWebMapper.INSTANCE.domainToCreateResponse(
            menuItemUseCase.createMenuItem(restaurantId, MenuItemWebMapper.INSTANCE.createRequestToDomain(request))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetMenuItemResponse> get(@PathVariable Long restaurantId, @PathVariable Long id) {
    return ResponseEntity.ok(MenuItemWebMapper.INSTANCE.domainToGetResponse(menuItemUseCase.getMenuItem(restaurantId, id)));
  }

  @GetMapping
  public ResponseEntity<List<GetMenuItemResponse>> getAll(@PathVariable Long restaurantId) {
    return ResponseEntity.ok(menuItemUseCase.getMenuItemsByRestaurant(restaurantId).stream()
        .map(MenuItemWebMapper.INSTANCE::domainToGetResponse)
        .toList());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CreateMenuItemResponse> update(@PathVariable Long restaurantId, @PathVariable Long id,
                                                       @Valid @RequestBody UpdateMenuItemRequest request) {
    return ResponseEntity.ok(MenuItemWebMapper.INSTANCE.domainToCreateResponse(
        menuItemUseCase.updateMenuItem(restaurantId, id, MenuItemWebMapper.INSTANCE.updateRequestToDomain(request))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
    menuItemUseCase.deleteMenuItem(restaurantId, id);
    return ResponseEntity.noContent().build();
  }
}
