package com.techchallenge.restaurant.adapter.input.response.user;

import com.techchallenge.restaurant.adapter.input.response.usertype.UserTypeResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados retornados após a criação de um usuário.
 *
 * @param id       id gerado para o usuário
 * @param name     nome completo do usuário
 * @param email    e-mail do usuário
 * @param userType tipo de usuário associado
 * @param login    login do usuário
 */
public record CreateUserResponse(
    @Schema(description = "Id do usuário")
    String id,

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    String name,

    @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
    String email,

    @Schema(description = "Tipo de usuário associado")
    UserTypeResponse userType,

    @Schema(description = "Login do usuário", example = "joaosilva")
    String login
) {}
