package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserPasswordRequest(
    @NotBlank(message = ApiConstants.CUR_PASSWORD_REQUIRED)
    String currentPassword,

    @NotBlank(message = ApiConstants.NEW_PASSWORD_REQUIRED)
    @Pattern(
        regexp = ApiConstants.PASSWORD_PATTERN,
        message = ApiConstants.PASSWORD_INVALID)
    String newPassword) {}
