package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.RestaurantWebMapper;
import com.techchallenge.restaurant.adapter.input.request.restaurant.CreateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.request.restaurant.UpdateRestaurantRequest;
import com.techchallenge.restaurant.adapter.input.response.restaurant.CreateRestaurantResponse;
import com.techchallenge.restaurant.adapter.input.response.restaurant.GetRestaurantResponse;
import com.techchallenge.restaurant.application.port.input.restaurant.CreateRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.DeleteRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantsPort;
import com.techchallenge.restaurant.application.port.input.restaurant.UpdateRestaurantPort;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "Cadastro, consulta e manutenção de restaurantes")
public class RestaurantController {

  private final CreateRestaurantPort createRestaurantUseCase;
  private final UpdateRestaurantPort updateRestaurantUseCase;
  private final GetRestaurantPort getRestaurantUseCase;
  private final GetRestaurantsPort getRestaurantsUseCase;
  private final DeleteRestaurantPort deleteRestaurantUseCase;

  @Operation(summary = "Criar restaurante")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "403", description = "Dono informado não é do tipo \"Dono\""),
      @ApiResponse(responseCode = "404", description = "Usuário dono não encontrado"),
      @ApiResponse(responseCode = "422", description = "Já existe um restaurante com o mesmo nome")
  })
  @PostMapping
  public ResponseEntity<CreateRestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(RestaurantWebMapper.INSTANCE.domainToCreateResponse(
            createRestaurantUseCase.execute(
                RestaurantWebMapper.INSTANCE.createRequestToDomain(request))));
  }

  @Operation(summary = "Buscar restaurante por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<GetRestaurantResponse> get(
      @Parameter(description = "Id do restaurante") @PathVariable Long id) {
    return ResponseEntity.ok(
        RestaurantWebMapper.INSTANCE.domainToGetResponse(getRestaurantUseCase.execute(id)));
  }

  @Operation(summary = "Listar restaurantes")
  @ApiResponse(responseCode = "200", description = "Lista de restaurantes")
  @GetMapping
  public ResponseEntity<List<GetRestaurantResponse>> getAll() {
    return ResponseEntity.ok(getRestaurantsUseCase.execute().stream()
        .map(RestaurantWebMapper.INSTANCE::domainToGetResponse)
        .toList());
  }

  @Operation(summary = "Atualizar restaurante")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Restaurante ou dono não encontrado")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<CreateRestaurantResponse> update(
      @Parameter(description = "Id do restaurante") @PathVariable Long id,
      @Valid @RequestBody UpdateRestaurantRequest request) {
    return ResponseEntity.ok(RestaurantWebMapper.INSTANCE.domainToCreateResponse(
        updateRestaurantUseCase.execute(id,
            RestaurantWebMapper.INSTANCE.updateRequestToDomain(request))));
  }

  @Operation(summary = "Excluir restaurante")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Restaurante removido com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Id do restaurante") @PathVariable Long id) {
    deleteRestaurantUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
