package com.techchallenge.restaurant.adapter.output.postgres.persistence;

import com.techchallenge.restaurant.adapter.output.postgres.mapper.MenuItemPersistenceMapper;
import com.techchallenge.restaurant.adapter.output.postgres.persistence.repository.MenuItemRepository;
import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.port.output.MenuItemPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuItemPostgres implements MenuItemPersistencePort {

  private final MenuItemRepository menuItemRepository;

  @Override
  public MenuItem save(MenuItem menuItem) {
    return MenuItemPersistenceMapper.INSTANCE.toDomain(
        menuItemRepository.save(MenuItemPersistenceMapper.INSTANCE.toEntity(menuItem)));
  }

  @Override
  public Optional<MenuItem> findById(Long id) {
    return menuItemRepository.findById(id).map(MenuItemPersistenceMapper.INSTANCE::toDomain);
  }

  @Override
  public List<MenuItem> findByRestaurantId(Long restaurantId) {
    return menuItemRepository.findAllByRestaurantId(restaurantId).stream()
        .map(MenuItemPersistenceMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId) {
    return menuItemRepository.existsByNameIgnoreCaseAndRestaurantId(name, restaurantId);
  }

  @Override
  public void deleteById(Long id) {
    menuItemRepository.deleteById(id);
  }
}
