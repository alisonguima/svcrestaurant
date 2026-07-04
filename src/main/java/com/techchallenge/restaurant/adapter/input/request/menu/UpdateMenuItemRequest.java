package com.techchallenge.restaurant.adapter.input.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados para atualização parcial de um item de cardápio. Todos os campos são
 * opcionais; apenas os informados são alterados.
 *
 * @param name             novo nome do item
 * @param description      nova descrição do item
 * @param price            novo preço do item, mínimo de 0.01
 * @param onlyAtRestaurant se o item só pode ser consumido no local
 * @param photoPath        novo caminho/URL da foto do item
 */
public record UpdateMenuItemRequest(
    @Schema(description = "Novo nome do item de cardápio", example = "Risoto de Camarão")
    @Size(min = 2, max = 120)
    String name,

    @Schema(description = "Nova descrição do item de cardápio", example = "Risoto cremoso com camarões grelhados")
    @Size(min = 2, max = 500)
    String description,

    @Schema(description = "Novo preço do item (mínimo de 0.01)", example = "49.90")
    @DecimalMin(value = "0.01")
    BigDecimal price,

    @Schema(description = "Indica se o item só pode ser consumido no restaurante")
    Boolean onlyAtRestaurant,

    @Schema(description = "Novo caminho ou URL da foto do item")
    @Size(max = 255)
    String photoPath) {}
