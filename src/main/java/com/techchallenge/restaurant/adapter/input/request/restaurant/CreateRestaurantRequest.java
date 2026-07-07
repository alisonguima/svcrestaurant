package com.techchallenge.restaurant.adapter.input.request.restaurant;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados necessários para o cadastro de um novo restaurante.
 * O usuário indicado em {@code ownerUserId} deve existir e ser do tipo
 * {@code "Dono"}.
 *
 * @param name          nome do restaurante (único)
 * @param address       endereço do restaurante
 * @param cuisineType   tipo de cozinha servida
 * @param openingHours  horário de funcionamento
 * @param ownerUserId   id do usuário dono do restaurante
 */
public record CreateRestaurantRequest(
    @Schema(description = "Nome do restaurante", example = "Cantina da Nonna")
    @NotBlank(message = InputValidationConstants.RESTAURANT_NAME_REQUIRED)
    @Size(min = 2, max = 120)
    String name,

    @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123")
    @NotBlank(message = InputValidationConstants.RESTAURANT_ADDRESS_REQUIRED)
    @Size(min = 5, max = 255)
    String address,

    @Schema(description = "Tipo de cozinha servida", example = "Italiana")
    @NotBlank(message = InputValidationConstants.RESTAURANT_CUISINE_REQUIRED)
    @Size(min = 2, max = 100)
    String cuisineType,

    @Schema(description = "Horário de funcionamento", example = "11h às 23h")
    @NotBlank(message = InputValidationConstants.RESTAURANT_OPENING_HOURS_REQUIRED)
    @Size(min = 2, max = 100)
    String openingHours,

    @Schema(description = "Id do usuário dono do restaurante (deve ser do tipo \"Dono\")")
    @NotNull(message = InputValidationConstants.RESTAURANT_OWNER_REQUIRED)
    Long ownerUserId) {}
