package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.mapper.RestaurantPersistenceMapper;
import com.techchallenge.restaurant.adapter.output.postgres.model.RestaurantEntity;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.RestaurantRepository;
import com.techchallenge.restaurant.application.domain.ApiConstants;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
import com.techchallenge.restaurant.application.port.output.RestaurantPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantPostgres implements RestaurantPersistencePort {

  private final RestaurantRepository restaurantRepository;

  @Override
  public Restaurant save(Restaurant restaurant) {
    RestaurantEntity saved = restaurantRepository.save(RestaurantPersistenceMapper.INSTANCE.toEntity(restaurant));
    return RestaurantPersistenceMapper.INSTANCE.toDomain(saved);
  }

  @Override
  public Optional<Restaurant> findById(Long id) {
    return restaurantRepository.findById(id).map(RestaurantPersistenceMapper.INSTANCE::toDomain);
  }

  @Override
  public List<Restaurant> findAll() {
    return restaurantRepository.findAll().stream().map(RestaurantPersistenceMapper.INSTANCE::toDomain).toList();
  }

  @Override
  public void deleteById(Long id) {
    restaurantRepository.findById(id)
        .ifPresentOrElse(restaurantRepository::delete, () -> {
          throw new DefaultException(ErrorCode.RESTAURANT_NOT_FOUND, ApiConstants.RESTAURANT_NOT_FOUND_WITH_ID + id);
        });
  }
}
