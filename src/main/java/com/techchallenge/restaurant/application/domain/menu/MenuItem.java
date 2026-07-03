package com.techchallenge.restaurant.application.domain.menu;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
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
}
