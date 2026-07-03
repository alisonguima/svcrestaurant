package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = ApiConstants.NAME_REQUIRED)
    @Size(min = 2, max = 100, message = ApiConstants.NAME_SIZE)
    String name,

    @NotBlank(message = ApiConstants.EMAIL_REQUIRED)
    @Email(message = ApiConstants.EMAIL_INVALID)
    String email,

    @NotBlank(message = ApiConstants.LOGIN_REQUIRED)
    @Size(min = 3, max = 50, message = ApiConstants.LOGIN_SIZE)
    String login,

    @NotBlank(message = ApiConstants.PASSWORD_REQUIRED)
    @Pattern(
        regexp = ApiConstants.PASSWORD_PATTERN,
        message = ApiConstants.PASSWORD_INVALID)
    String password,

    @NotNull(message = ApiConstants.USER_TYPE_REQUIRED)
    Long userTypeId) {}
