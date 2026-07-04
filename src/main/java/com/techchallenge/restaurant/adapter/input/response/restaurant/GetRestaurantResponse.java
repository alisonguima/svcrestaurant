package com.techchallenge.restaurant.adapter.input.response.restaurant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

/**
 * Dados retornados na consulta de um restaurante.
 *
 * @param id            id do restaurante
 * @param name          nome do restaurante
 * @param address       endereço do restaurante
 * @param cuisineType   tipo de cozinha servida
 * @param openingHours  horário de funcionamento
 * @param ownerId       id do usuário dono do restaurante
 * @param ownerName     nome do usuário dono do restaurante
 * @param lastUpdateAt  data/hora (UTC) da última atualização do restaurante
 */
public record GetRestaurantResponse(
    @Schema(description = "Id do restaurante")
    String id,

    @Schema(description = "Nome do restaurante", example = "Cantina da Nonna")
    String name,

    @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123")
    String address,

    @Schema(description = "Tipo de cozinha servida", example = "Italiana")
    String cuisineType,

    @Schema(description = "Horário de funcionamento", example = "11h às 23h")
    String openingHours,

    @Schema(description = "Id do usuário dono do restaurante")
    String ownerId,

    @Schema(description = "Nome do usuário dono do restaurante")
    String ownerName,

    @Schema(description = "Data/hora (UTC) da última atualização", example = "2026-07-04T10:15:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
