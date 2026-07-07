package com.techchallenge.restaurant.application.domain.user;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Long id;
  private String name;
  private String email;
  private String login;
  private String password;
  private UserType userType;
  private ZonedDateTime lastUpdateAt;

  public void applyUpdate(User patch) {
    if (patch.name != null) this.name = patch.name;
    if (patch.email != null) this.email = patch.email;
    if (patch.login != null) this.login = patch.login;
  }

  public void assignUserType(UserType type) {
    this.userType = type;
  }

  public void changePassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public void stamp(ZonedDateTime now) {
    this.lastUpdateAt = now;
  }

  public boolean isRestaurantOwner() {
    return userType != null && userType.isRestaurantOwnerType();
  }
}
