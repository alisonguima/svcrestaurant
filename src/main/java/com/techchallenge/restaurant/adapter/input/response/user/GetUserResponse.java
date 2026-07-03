package com.techchallenge.restaurant.adapter.input.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techchallenge.restaurant.adapter.input.response.usertype.UserTypeResponse;

import java.time.ZonedDateTime;

public record GetUserResponse(
    String id,
    String name,
    String email,
    String login,
    UserTypeResponse userType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
