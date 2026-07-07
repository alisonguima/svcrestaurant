package com.techchallenge.restaurant.adapter.input.request.menu;

import com.techchallenge.restaurant.adapter.input.validation.InputValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateMenuItemRequest(
    @Schema(description = "Nome do item de cardápio", example = "Risoto de Camarão")
    @NotBlank(message = InputValidationConstants.MENU_ITEM_NAME_REQUIRED)
    @Size(min = 2, max = 120)
    String name,

    @Schema(description = "Descrição do item de cardápio", example = "Risoto cremoso com camarões grelhados")
    @NotBlank(message = InputValidationConstants.MENU_ITEM_DESCRIPTION_REQUIRED)
    @Size(min = 2, max = 500)
    String description,

    @Schema(description = "Preço do item (mínimo de 0.01)", example = "49.90")
    @NotNull(message = InputValidationConstants.MENU_ITEM_PRICE_REQUIRED)
    @DecimalMin(value = "0.01", message = InputValidationConstants.MENU_ITEM_PRICE_INVALID)
    BigDecimal price,

    @Schema(description = "Indica se o item só pode ser consumido no restaurante")
    boolean onlyAtRestaurant,

    @Schema(description = "Caminho ou URL da foto do item")
    @NotBlank(message = InputValidationConstants.MENU_ITEM_PHOTO_PATH_REQUIRED)
    @Size(max = 255)
    String photoPath,

    @Schema(description = "Id do dono do restaurante (deve ser o proprietário)")
    @NotNull(message = InputValidationConstants.MENU_ITEM_OWNER_REQUIRED)
    Long ownerId) {}
