package com.techchallenge.restaurant.adapter.input.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techchallenge.restaurant.adapter.input.response.usertype.UserTypeResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

/**
 * Dados retornados na consulta de um usuário.
 *
 * @param id           id do usuário
 * @param name         nome completo do usuário
 * @param email        e-mail do usuário
 * @param login        login do usuário
 * @param userType     tipo de usuário associado
 * @param lastUpdateAt data/hora (UTC) da última atualização do usuário
 */
public record GetUserResponse(
    @Schema(description = "Id do usuário")
    String id,

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    String name,

    @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
    String email,

    @Schema(description = "Login do usuário", example = "joaosilva")
    String login,

    @Schema(description = "Tipo de usuário associado")
    UserTypeResponse userType,

    @Schema(description = "Data/hora (UTC) da última atualização", example = "2026-07-04T10:15:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
