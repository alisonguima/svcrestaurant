package com.techchallenge.restaurant.adapter.input.request.usertype;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import com.techchallenge.restaurant.application.exception.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados necessários para o cadastro de um novo tipo de usuário.
 * Apenas os nomes {@code "Dono de Restaurante"} e {@code "Cliente"} são aceitos.
 *
 * @param name nome do tipo de usuário
 */
public record CreateUserTypeRequest(
    @Schema(description = "Nome do tipo de usuário", example = "Cliente", allowableValues = {"Dono de Restaurante", "Cliente"})
    @NotBlank(message = InputValidationConstants.USER_TYPE_NAME_REQUIRED)
    @Pattern(regexp = "^(" + InputValidationConstants.USER_TYPE_ALLOWED_NAMES + ")$", message = ApiConstants.USER_TYPE_INVALID_NAME)
    @Size(min = 2, max = 100, message = InputValidationConstants.USER_TYPE_NAME_SIZE)
    String name) {}
