package com.techchallenge.restaurant.application.domain.menu;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@ToString
@Getter
@AllArgsConstructor
public class MenuItem {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Boolean onlyAtRestaurant;
  private String photoPath;
  private Restaurant restaurant;
  private ZonedDateTime lastUpdateAt;

  public void applyUpdate(MenuItem patch) {
    if (patch.name != null) this.name = patch.name;
    if (patch.description != null) this.description = patch.description;
    if (patch.price != null) this.price = patch.price;
    if (patch.onlyAtRestaurant != null) this.onlyAtRestaurant = patch.onlyAtRestaurant;
    if (patch.photoPath != null) this.photoPath = patch.photoPath;
  }

  public void assignRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  public void stamp(ZonedDateTime now) {
    this.lastUpdateAt = now;
  }

  public boolean belongsToRestaurant(Long restaurantId) {
    return restaurant != null && restaurant.getId().equals(restaurantId);
  }
}
