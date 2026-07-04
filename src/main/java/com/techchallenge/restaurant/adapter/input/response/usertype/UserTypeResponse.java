package com.techchallenge.restaurant.adapter.input.response.usertype;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representação resumida de um tipo de usuário, usada como sub-recurso
 * dentro de outras respostas (ex.: {@code GetUserResponse}).
 *
 * @param id   id do tipo de usuário
 * @param name nome do tipo de usuário
 */
public record UserTypeResponse(
    @Schema(description = "Id do tipo de usuário")
    String id,

    @Schema(description = "Nome do tipo de usuário", example = "Cliente")
    String name
) {}
