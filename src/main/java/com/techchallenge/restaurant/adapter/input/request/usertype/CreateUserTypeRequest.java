package com.techchallenge.restaurant.adapter.input.request.usertype;

import com.techchallenge.restaurant.application.domain.ApiConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserTypeRequest(
    @NotBlank(message = ApiConstants.USER_TYPE_NAME_REQUIRED)
    @Pattern(regexp = "^(" + ApiConstants.USER_TYPE_ALLOWED_NAMES + ")$", message = ApiConstants.USER_TYPE_INVALID_NAME)
    @Size(min = 2, max = 100, message = ApiConstants.USER_TYPE_NAME_SIZE)
    String name) {}
