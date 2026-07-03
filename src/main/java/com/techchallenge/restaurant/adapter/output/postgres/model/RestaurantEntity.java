package com.techchallenge.restaurant.adapter.output.postgres.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@ToString(exclude = {"owner"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String cuisineType;

  @Column(nullable = false)
  private String openingHours;

  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private UserEntity owner;

  @Column(nullable = false)
  private ZonedDateTime lastUpdateAt;
}
