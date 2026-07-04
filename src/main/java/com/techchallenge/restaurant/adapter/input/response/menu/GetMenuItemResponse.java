package com.techchallenge.restaurant.adapter.input.response.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Dados retornados na consulta de um item de cardápio.
 *
 * @param id                id do item de cardápio
 * @param name              nome do item
 * @param description       descrição do item
 * @param price             preço do item
 * @param onlyAtRestaurant  se o item só pode ser consumido no local
 * @param photoPath         caminho/URL da foto do item
 * @param restaurantId      id do restaurante ao qual o item pertence
 * @param restaurantName    nome do restaurante ao qual o item pertence
 * @param lastUpdateAt      data/hora (UTC) da última atualização do item
 */
public record GetMenuItemResponse(
    @Schema(description = "Id do item de cardápio")
    String id,

    @Schema(description = "Nome do item de cardápio", example = "Risoto de Camarão")
    String name,

    @Schema(description = "Descrição do item de cardápio", example = "Risoto cremoso com camarões grelhados")
    String description,

    @Schema(description = "Preço do item", example = "49.90")
    BigDecimal price,

    @Schema(description = "Indica se o item só pode ser consumido no restaurante")
    boolean onlyAtRestaurant,

    @Schema(description = "Caminho ou URL da foto do item")
    String photoPath,

    @Schema(description = "Id do restaurante ao qual o item pertence")
    String restaurantId,

    @Schema(description = "Nome do restaurante ao qual o item pertence")
    String restaurantName,

    @Schema(description = "Data/hora (UTC) da última atualização", example = "2026-07-04T10:15:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    ZonedDateTime lastUpdateAt
) {}
