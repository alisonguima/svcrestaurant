package com.techchallenge.restaurant.adapter.input.request.user;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Dados para atualização parcial de um usuário. Todos os campos são
 * opcionais; apenas os informados são alterados.
 *
 * @param name       novo nome do usuário
 * @param email      novo e-mail (deve permanecer único)
 * @param login      novo login (deve permanecer único)
 * @param userTypeId id do novo tipo de usuário a ser associado
 */
public record UpdateUserRequest(
    @Schema(description = "Novo nome completo do usuário", example = "João da Silva")
    @Size(min = 2, max = 100, message = InputValidationConstants.NAME_SIZE)
    String name,

    @Schema(description = "Novo e-mail único do usuário", example = "joao.silva@email.com")
    @Email(message = InputValidationConstants.EMAIL_INVALID)
    String email,

    @Schema(description = "Novo login único do usuário", example = "joaosilva")
    @Size(min = 3, max = 50, message = InputValidationConstants.LOGIN_SIZE)
    String login,

    @Schema(description = "Id do novo tipo de usuário a ser associado")
    Long userTypeId) {}
