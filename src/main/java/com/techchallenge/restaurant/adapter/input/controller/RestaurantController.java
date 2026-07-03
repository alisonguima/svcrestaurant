package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.RestaurantWebMapper;
import com.techchallenge.restaurant.adapter.input.request.restaurant.CreateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.request.restaurant.UpdateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.response.restaurant.CreateRestaurantResponse;
import com.techchallenge.restaurant.adapter.input.response.restaurant.GetRestaurantResponse;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantUseCase restaurantUseCase;

  @PostMapping
  public ResponseEntity<CreateRestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(RestaurantWebMapper.INSTANCE.domainToCreateResponse(
            restaurantUseCase.createRestaurant(RestaurantWebMapper.INSTANCE.createRequestToDomain(request))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetRestaurantResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(RestaurantWebMapper.INSTANCE.domainToGetResponse(restaurantUseCase.getRestaurant(id)));
  }

  @GetMapping
  public ResponseEntity<List<GetRestaurantResponse>> getAll() {
    return ResponseEntity.ok(restaurantUseCase.getRestaurants().stream()
        .map(RestaurantWebMapper.INSTANCE::domainToGetResponse)
        .toList());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CreateRestaurantResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateRestaurantRequest request) {
    return ResponseEntity.ok(RestaurantWebMapper.INSTANCE.domainToCreateResponse(
        restaurantUseCase.updateRestaurant(id, RestaurantWebMapper.INSTANCE.updateRequestToDomain(request))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    restaurantUseCase.deleteRestaurant(id);
    return ResponseEntity.noContent().build();
  }
}
