package com.techchallenge.restaurant.application.domain.restaurant;

import com.techchallenge.restaurant.application.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@ToString
@Getter
@Setter
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
}
