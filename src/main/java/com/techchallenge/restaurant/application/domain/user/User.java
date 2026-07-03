package com.techchallenge.restaurant.application.domain.user;

import com.techchallenge.restaurant.application.domain.enums.UserType;
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
}
