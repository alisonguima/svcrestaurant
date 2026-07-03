package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(min = 2, max = 100, message = ApiConstants.NAME_SIZE)
    String name,

    @Email(message = ApiConstants.EMAIL_INVALID)
    String email,

    @Size(min = 3, max = 50, message = ApiConstants.LOGIN_SIZE)
    String login,

    Long userTypeId) {}
