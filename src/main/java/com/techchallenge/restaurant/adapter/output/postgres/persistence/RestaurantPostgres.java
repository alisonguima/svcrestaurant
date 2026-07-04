package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.mapper.RestaurantPersistenceMapper;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.RestaurantRepository;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
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
    return RestaurantPersistenceMapper.INSTANCE.toDomain(
        restaurantRepository.save(RestaurantPersistenceMapper.INSTANCE.toEntity(restaurant)));
  }

  @Override
  public Optional<Restaurant> findById(Long id) {
    return restaurantRepository.findById(id).map(RestaurantPersistenceMapper.INSTANCE::toDomain);
  }

  @Override
  public List<Restaurant> findAll() {
    return restaurantRepository.findAll().stream()
        .map(RestaurantPersistenceMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public boolean existsByNameIgnoreCase(String name) {
    return restaurantRepository.existsByNameIgnoreCase(name);
  }

  @Override
  public void deleteById(Long id) {
    restaurantRepository.deleteById(id);
  }
}
