package com.techchallenge.restaurant.adapter.input.response.menu;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados retornados após a criação ou atualização de um item de cardápio.
 *
 * @param id                id gerado para o item de cardápio
 * @param name              nome do item
 * @param description       descrição do item
 * @param price             preço do item, formatado como texto
 * @param onlyAtRestaurant  se o item só pode ser consumido no local
 * @param photoPath         caminho/URL da foto do item
 * @param restaurantId      id do restaurante ao qual o item pertence
 * @param restaurantName    nome do restaurante ao qual o item pertence
 */
public record CreateMenuItemResponse(
    @Schema(description = "Id do item de cardápio")
    String id,

    @Schema(description = "Nome do item de cardápio", example = "Risoto de Camarão")
    String name,

    @Schema(description = "Descrição do item de cardápio", example = "Risoto cremoso com camarões grelhados")
    String description,

    @Schema(description = "Preço do item", example = "49.90")
    String price,

    @Schema(description = "Indica se o item só pode ser consumido no restaurante")
    boolean onlyAtRestaurant,

    @Schema(description = "Caminho ou URL da foto do item")
    String photoPath,

    @Schema(description = "Id do restaurante ao qual o item pertence")
    String restaurantId,

    @Schema(description = "Nome do restaurante ao qual o item pertence")
    String restaurantName
) {}
