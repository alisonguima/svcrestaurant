package com.techchallenge.restaurant.application.domain.usertype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class UserType {

  public static final String DONO = "Dono";
  public static final String CLIENTE = "Cliente";

  private Long id;
  private String name;

  public void applyUpdate(UserType patch) {
    if (patch.name != null) this.name = patch.name;
  }

  public boolean isRestaurantOwnerType() {
    return name != null && DONO.equalsIgnoreCase(name.trim());
  }

  public boolean isAllowedName() {
    return name != null && (
        DONO.equalsIgnoreCase(name.trim()) ||
        CLIENTE.equalsIgnoreCase(name.trim()));
  }
}
