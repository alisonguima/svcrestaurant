package com.techchallenge.restaurant.adapter.input.response.user;


import com.techchallenge.restaurant.adapter.input.response.usertype.UserTypeResponse;

public record CreateUserResponse(
    String id,
    String name,
    String email,
    UserTypeResponse userType,
    String login
) {}
