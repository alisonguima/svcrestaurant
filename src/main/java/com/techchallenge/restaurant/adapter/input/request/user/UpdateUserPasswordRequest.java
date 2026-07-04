package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Dados para troca de senha de um usuário. A senha atual é exigida para
 * confirmar a identidade do solicitante antes da troca.
 *
 * @param currentPassword senha atual do usuário, validada contra o hash armazenado
 * @param newPassword     nova senha, aplicada apenas se a senha atual conferir
 */
public record UpdateUserPasswordRequest(
    @Schema(description = "Senha atual do usuário")
    @NotBlank(message = InputValidationConstants.CUR_PASSWORD_REQUIRED)
    String currentPassword,

    @Schema(description = "Nova senha do usuário")
    @NotBlank(message = InputValidationConstants.NEW_PASSWORD_REQUIRED)
    @Pattern(
        regexp = InputValidationConstants.PASSWORD_PATTERN,
        message = InputValidationConstants.PASSWORD_INVALID)
    String newPassword) {}
