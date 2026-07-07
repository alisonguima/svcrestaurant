package com.techchallenge.restaurant.application.domain.restaurant;

import com.techchallenge.restaurant.application.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  private Long id;
  private String name;
  private String address;
  private String cuisineType;
  private String openingHours;
  private User owner;
  private ZonedDateTime lastUpdateAt;

  public void applyUpdate(Restaurant patch) {
    if (patch.name != null) this.name = patch.name;
    if (patch.address != null) this.address = patch.address;
    if (patch.cuisineType != null) this.cuisineType = patch.cuisineType;
    if (patch.openingHours != null) this.openingHours = patch.openingHours;
  }

  public void assignOwner(User owner) {
    this.owner = owner;
  }

  public void stamp(ZonedDateTime now) {
    this.lastUpdateAt = now;
  }

  public boolean isOwnedBy(Long userId) {
    return owner != null && owner.getId().equals(userId);
  }
}
