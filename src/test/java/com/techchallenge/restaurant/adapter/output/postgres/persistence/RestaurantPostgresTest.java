package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.model.RestaurantEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.UserEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.RestaurantRepository;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantPostgresTest {

  @Mock
  private RestaurantRepository restaurantRepository;

  @InjectMocks
  private RestaurantPostgres restaurantPostgres;

  private RestaurantEntity restaurantEntity() {
    UserTypeEntity userTypeEntity = UserTypeEntity.builder().id(1L).name(UserType.DONO).build();
    UserEntity ownerEntity = UserEntity.builder()
        .id(10L).name("Owner").email("owner@test.com").login("owner")
        .password("$2a$10$hashed").userType(userTypeEntity).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
    return RestaurantEntity.builder()
        .id(1L).name("Restaurante A").address("Rua A, 123")
        .cuisineType("Brasileira").openingHours("10:00-22:00")
        .owner(ownerEntity).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
  }

  private Restaurant restaurantDomain() {
    UserType userType = UserType.builder().id(1L).name(UserType.DONO).build();
    User owner = User.builder()
        .id(10L).name("Owner").email("owner@test.com").login("owner")
        .password("raw").userType(userType).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
    return Restaurant.builder()
        .name("Restaurante A").address("Rua A, 123")
        .cuisineType("Brasileira").openingHours("10:00-22:00")
        .owner(owner).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
  }

  @Test
  void save_shouldMapToEntitySaveAndReturnDomain() {
    when(restaurantRepository.save(any())).thenReturn(restaurantEntity());

    Restaurant result = restaurantPostgres.save(restaurantDomain());

    assertEquals(1L, result.getId());
    assertEquals("Restaurante A", result.getName());
    assertEquals("Rua A, 123", result.getAddress());
    verify(restaurantRepository).save(any(RestaurantEntity.class));
  }

  @Test
  void findById_shouldReturnMappedDomain_whenEntityExists() {
    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurantEntity()));

    Optional<Restaurant> result = restaurantPostgres.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("Restaurante A", result.get().getName());
  }

  @Test
  void findById_shouldReturnEmpty_whenEntityDoesNotExist() {
    when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

    Optional<Restaurant> result = restaurantPostgres.findById(99L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_shouldReturnMappedList() {
    when(restaurantRepository.findAll()).thenReturn(List.of(restaurantEntity(), restaurantEntity()));

    List<Restaurant> result = restaurantPostgres.findAll();

    assertEquals(2, result.size());
    result.forEach(r -> assertEquals("Restaurante A", r.getName()));
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoEntitiesExist() {
    when(restaurantRepository.findAll()).thenReturn(List.of());

    List<Restaurant> result = restaurantPostgres.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void existsByNameIgnoreCase_shouldReturnTrue_whenNameExists() {
    when(restaurantRepository.existsByNameIgnoreCase("Restaurante A")).thenReturn(true);

    assertTrue(restaurantPostgres.existsByNameIgnoreCase("Restaurante A"));
    verify(restaurantRepository).existsByNameIgnoreCase("Restaurante A");
  }

  @Test
  void existsByNameIgnoreCase_shouldReturnFalse_whenNameDoesNotExist() {
    when(restaurantRepository.existsByNameIgnoreCase("Desconhecido")).thenReturn(false);

    assertFalse(restaurantPostgres.existsByNameIgnoreCase("Desconhecido"));
  }

  @Test
  void deleteById_shouldDelegateToRepository() {
    doNothing().when(restaurantRepository).deleteById(1L);

    restaurantPostgres.deleteById(1L);

    verify(restaurantRepository).deleteById(1L);
  }
}
