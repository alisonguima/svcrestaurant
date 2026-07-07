package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.MenuItemWebMapper;
import com.techchallenge.restaurant.adapter.input.request.menu.CreateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.request.menu.UpdateMenuItemRequest;
import com.techchallenge.restaurant.adapter.input.response.menu.CreateMenuItemResponse;
import com.techchallenge.restaurant.adapter.input.response.menu.GetMenuItemResponse;
import com.techchallenge.restaurant.application.port.input.menuitem.CreateMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.DeleteMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemPort;
import com.techchallenge.restaurant.application.port.input.menuitem.GetMenuItemsByRestaurantPort;
import com.techchallenge.restaurant.application.port.input.menuitem.UpdateMenuItemPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@Tag(name = "Itens de Cardápio", description = "Cadastro, consulta e manutenção dos itens de cardápio de um restaurante")
public class MenuItemController {

  private final CreateMenuItemPort createMenuItemUseCase;
  private final UpdateMenuItemPort updateMenuItemUseCase;
  private final GetMenuItemPort getMenuItemUseCase;
  private final GetMenuItemsByRestaurantPort getMenuItemsByRestaurantUseCase;
  private final DeleteMenuItemPort deleteMenuItemUseCase;

  @Operation(summary = "Criar item de cardápio")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Item de cardápio criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "403", description = "Usuário informado não é o dono do restaurante"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
      @ApiResponse(responseCode = "422", description = "Já existe um item com o mesmo nome no restaurante")
  })
  @PostMapping
  public ResponseEntity<CreateMenuItemResponse> create(
      @Parameter(description = "Id do restaurante") @PathVariable Long restaurantId,
      @Valid @RequestBody CreateMenuItemRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(MenuItemWebMapper.INSTANCE.domainToCreateResponse(
            createMenuItemUseCase.execute(restaurantId, request.ownerId(),
                MenuItemWebMapper.INSTANCE.createRequestToDomain(request))));
  }

  @Operation(summary = "Buscar item de cardápio por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Item de cardápio encontrado"),
      @ApiResponse(responseCode = "404", description = "Restaurante ou item de cardápio não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<GetMenuItemResponse> get(
      @Parameter(description = "Id do restaurante") @PathVariable Long restaurantId,
      @Parameter(description = "Id do item de cardápio") @PathVariable Long id) {
    return ResponseEntity.ok(
        MenuItemWebMapper.INSTANCE.domainToGetResponse(
            getMenuItemUseCase.execute(restaurantId, id)));
  }

  @Operation(summary = "Listar itens de cardápio de um restaurante")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista de itens de cardápio"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @GetMapping
  public ResponseEntity<List<GetMenuItemResponse>> getAll(
      @Parameter(description = "Id do restaurante") @PathVariable Long restaurantId) {
    return ResponseEntity.ok(getMenuItemsByRestaurantUseCase.execute(restaurantId).stream()
        .map(MenuItemWebMapper.INSTANCE::domainToGetResponse)
        .toList());
  }

  @Operation(summary = "Atualizar item de cardápio")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Item de cardápio atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "403", description = "Usuário informado não é o dono do restaurante"),
      @ApiResponse(responseCode = "404", description = "Restaurante ou item de cardápio não encontrado")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<CreateMenuItemResponse> update(
      @Parameter(description = "Id do restaurante") @PathVariable Long restaurantId,
      @Parameter(description = "Id do item de cardápio") @PathVariable Long id,
      @Valid @RequestBody UpdateMenuItemRequest request) {
    return ResponseEntity.ok(MenuItemWebMapper.INSTANCE.domainToCreateResponse(
        updateMenuItemUseCase.execute(restaurantId, id, request.ownerId(),
            MenuItemWebMapper.INSTANCE.updateRequestToDomain(request))));
  }

  @Operation(summary = "Excluir item de cardápio")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Item de cardápio removido com sucesso"),
      @ApiResponse(responseCode = "403", description = "Usuário informado não é o dono do restaurante"),
      @ApiResponse(responseCode = "404", description = "Restaurante ou item de cardápio não encontrado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Id do restaurante") @PathVariable Long restaurantId,
      @Parameter(description = "Id do item de cardápio") @PathVariable Long id,
      @Parameter(description = "Id do dono do restaurante") @RequestParam Long ownerId) {
    deleteMenuItemUseCase.execute(restaurantId, id, ownerId);
    return ResponseEntity.noContent().build();
  }
}
