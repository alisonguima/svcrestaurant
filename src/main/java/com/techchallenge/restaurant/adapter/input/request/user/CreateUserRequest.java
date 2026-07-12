package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados necessários para o cadastro de um novo usuário.
 * {@code login} e {@code email} devem ser únicos no sistema.
 * O tipo de usuário não é definido na criação; deve ser associado
 * posteriormente via o endpoint de atribuição de tipo de usuário.
 *
 * @param name     nome completo do usuário
 * @param email    e-mail único do usuário
 * @param login    login único usado para autenticação
 * @param password senha em texto plano, armazenada com hash BCrypt
 */
public record CreateUserRequest(
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = InputValidationConstants.NAME_REQUIRED)
    @Size(min = 2, max = 100, message = InputValidationConstants.NAME_SIZE)
    String name,

    @Schema(description = "E-mail único do usuário", example = "joao.silva@email.com")
    @NotBlank(message = InputValidationConstants.EMAIL_REQUIRED)
    @Email(message = InputValidationConstants.EMAIL_INVALID)
    String email,

    @Schema(description = "Login único usado para autenticação", example = "joaosilva")
    @NotBlank(message = InputValidationConstants.LOGIN_REQUIRED)
    @Size(min = 3, max = 50, message = InputValidationConstants.LOGIN_SIZE)
    String login,

    @Schema(description = "Senha do usuário em texto plano (armazenada com hash BCrypt)")
    @NotBlank(message = InputValidationConstants.PASSWORD_REQUIRED)
    @Pattern(
        regexp = InputValidationConstants.PASSWORD_PATTERN,
        message = InputValidationConstants.PASSWORD_INVALID)
    String password) {}
