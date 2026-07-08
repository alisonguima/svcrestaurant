package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.model.MenuItemEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.RestaurantEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.UserEntity;
import com.techchallenge.restaurant.adapter.output.postgres.model.UserTypeEntity;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.MenuItemRepository;
import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemPostgresTest {

  @Mock
  private MenuItemRepository menuItemRepository;

  @InjectMocks
  private MenuItemPostgres menuItemPostgres;

  private MenuItemEntity menuItemEntity() {
    UserTypeEntity userTypeEntity = UserTypeEntity.builder().id(1L).name(UserType.DONO).build();
    UserEntity ownerEntity = UserEntity.builder()
        .id(10L).name("Owner").email("owner@test.com").login("owner")
        .password("$2a$10$hashed").userType(userTypeEntity).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
    RestaurantEntity restaurantEntity = RestaurantEntity.builder()
        .id(1L).name("Restaurante A").address("Rua A, 123")
        .cuisineType("Brasileira").openingHours("10:00-22:00")
        .owner(ownerEntity).lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
    return MenuItemEntity.builder()
        .id(1L).name("Picanha").description("Picanha na brasa")
        .price(BigDecimal.valueOf(89.9)).onlyAtRestaurant(true)
        .photoPath("/photos/picanha.jpg").restaurant(restaurantEntity)
        .lastUpdateAt(ZonedDateTime.now(UTC))
        .build();
  }

  private MenuItem menuItemDomain() {
    UserType userType = new UserType(1L, UserType.DONO);
    User owner = new User(10L, "Owner", "owner@test.com", "owner", "raw", userType, ZonedDateTime.now(UTC));
    Restaurant restaurant = new Restaurant(1L, "Restaurante A", "Rua A, 123", "Brasileira", "10:00-22:00", owner, ZonedDateTime.now(UTC));
    return new MenuItem(null, "Picanha", "Picanha na brasa", BigDecimal.valueOf(89.9), true,
        "/photos/picanha.jpg", restaurant, ZonedDateTime.now(UTC));
  }

  @Test
  void save_shouldMapToEntitySaveAndReturnDomain() {
    when(menuItemRepository.save(any())).thenReturn(menuItemEntity());

    MenuItem result = menuItemPostgres.save(menuItemDomain());

    assertEquals(1L, result.getId());
    assertEquals("Picanha", result.getName());
    assertEquals(BigDecimal.valueOf(89.9), result.getPrice());
    verify(menuItemRepository).save(any(MenuItemEntity.class));
  }

  @Test
  void findById_shouldReturnMappedDomain_whenEntityExists() {
    when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItemEntity()));

    Optional<MenuItem> result = menuItemPostgres.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("Picanha", result.get().getName());
  }

  @Test
  void findById_shouldReturnEmpty_whenEntityDoesNotExist() {
    when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

    Optional<MenuItem> result = menuItemPostgres.findById(99L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRestaurantId_shouldReturnMappedList() {
    when(menuItemRepository.findAllByRestaurantId(1L)).thenReturn(List.of(menuItemEntity()));

    List<MenuItem> result = menuItemPostgres.findByRestaurantId(1L);

    assertEquals(1, result.size());
    assertEquals("Picanha", result.get(0).getName());
  }

  @Test
  void findByRestaurantId_shouldReturnEmptyList_whenNoItemsExist() {
    when(menuItemRepository.findAllByRestaurantId(99L)).thenReturn(List.of());

    List<MenuItem> result = menuItemPostgres.findByRestaurantId(99L);

    assertTrue(result.isEmpty());
  }

  @Test
  void existsByNameIgnoreCaseAndRestaurantId_shouldReturnTrue_whenItemExists() {
    when(menuItemRepository.existsByNameIgnoreCaseAndRestaurantId("Picanha", 1L)).thenReturn(true);

    assertTrue(menuItemPostgres.existsByNameIgnoreCaseAndRestaurantId("Picanha", 1L));
    verify(menuItemRepository).existsByNameIgnoreCaseAndRestaurantId("Picanha", 1L);
  }

  @Test
  void existsByNameIgnoreCaseAndRestaurantId_shouldReturnFalse_whenItemDoesNotExist() {
    when(menuItemRepository.existsByNameIgnoreCaseAndRestaurantId("Desconhecido", 1L)).thenReturn(false);

    assertFalse(menuItemPostgres.existsByNameIgnoreCaseAndRestaurantId("Desconhecido", 1L));
  }

  @Test
  void deleteById_shouldDelegateToRepository() {
    doNothing().when(menuItemRepository).deleteById(1L);

    menuItemPostgres.deleteById(1L);

    verify(menuItemRepository).deleteById(1L);
  }
}
