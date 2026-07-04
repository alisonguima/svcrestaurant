package com.techchallenge.restaurant.adapter.input.response.usertype;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados retornados na consulta de um tipo de usuário.
 *
 * @param id   id do tipo de usuário
 * @param name nome do tipo de usuário
 */
public record GetUserTypeResponse(
    @Schema(description = "Id do tipo de usuário")
    String id,

    @Schema(description = "Nome do tipo de usuário", example = "Cliente")
    String name
) {}
