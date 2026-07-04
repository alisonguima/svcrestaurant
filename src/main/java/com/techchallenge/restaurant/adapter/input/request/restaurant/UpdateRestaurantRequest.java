package com.techchallenge.restaurant.adapter.input.request.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Dados para atualização parcial de um restaurante. Todos os campos são
 * opcionais; apenas os informados são alterados.
 *
 * @param name          novo nome do restaurante
 * @param address       novo endereço
 * @param cuisineType   novo tipo de cozinha
 * @param openingHours  novo horário de funcionamento
 * @param ownerUserId   id do novo usuário dono, para transferência de titularidade
 */
public record UpdateRestaurantRequest(
    @Schema(description = "Novo nome do restaurante", example = "Cantina da Nonna")
    @Size(min = 2, max = 120)
    String name,

    @Schema(description = "Novo endereço do restaurante", example = "Rua das Flores, 123")
    @Size(min = 5, max = 255)
    String address,

    @Schema(description = "Novo tipo de cozinha servida", example = "Italiana")
    @Size(min = 2, max = 100)
    String cuisineType,

    @Schema(description = "Novo horário de funcionamento", example = "11h às 23h")
    @Size(min = 2, max = 100)
    String openingHours,

    @Schema(description = "Id do novo usuário dono do restaurante")
    Long ownerUserId) {}
