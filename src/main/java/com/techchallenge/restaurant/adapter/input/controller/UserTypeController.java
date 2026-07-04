package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.UserTypeWebMapper;
import com.techchallenge.restaurant.adapter.input.request.usertype.CreateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.request.usertype.UpdateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.response.usertype.CreateUserTypeResponse;
import com.techchallenge.restaurant.adapter.input.response.usertype.GetUserTypeResponse;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
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

/**
 * Expõe as operações de CRUD de tipos de usuário. Os únicos valores aceitos
 * para o nome são {@code "Dono de Restaurante"} e {@code "Cliente"}.
 */
@RestController
@RequestMapping("/api/v1/user-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de Usuário", description = "Cadastro, consulta e manutenção dos tipos de usuário")
public class UserTypeController {

  private final UserTypeUseCase userTypeUseCase;

  /**
   * Cria um novo tipo de usuário.
   */
  @Operation(summary = "Criar tipo de usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Tipo de usuário criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "422", description = "Nome não permitido ou já cadastrado")
  })
  @PostMapping
  public ResponseEntity<CreateUserTypeResponse> create(@Valid @RequestBody CreateUserTypeRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(UserTypeWebMapper.INSTANCE.domainToCreateUserTypeResponse(
            userTypeUseCase.createUserType(UserTypeWebMapper.INSTANCE.createUserTypeRequestToDomain(request))));
  }

  /**
   * Busca um tipo de usuário pelo seu identificador.
   */
  @Operation(summary = "Buscar tipo de usuário por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Tipo de usuário encontrado"),
      @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<GetUserTypeResponse> get(@Parameter(description = "Id do tipo de usuário") @PathVariable Long id) {
    return ResponseEntity.ok(UserTypeWebMapper.INSTANCE.domainToGetUserTypeResponse(userTypeUseCase.getUserType(id)));
  }

  /**
   * Lista todos os tipos de usuário cadastrados.
   */
  @Operation(summary = "Listar tipos de usuário")
  @ApiResponse(responseCode = "200", description = "Lista de tipos de usuário")
  @GetMapping
  public ResponseEntity<List<GetUserTypeResponse>> getAll() {
    return ResponseEntity.ok(
        userTypeUseCase.getUserTypes().stream()
            .map(UserTypeWebMapper.INSTANCE::domainToGetUserTypeResponse)
            .toList());
  }

  /**
   * Atualiza o nome de um tipo de usuário existente.
   */
  @Operation(summary = "Atualizar tipo de usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Tipo de usuário atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado"),
      @ApiResponse(responseCode = "422", description = "Nome não permitido ou já cadastrado")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<CreateUserTypeResponse> update(@Parameter(description = "Id do tipo de usuário") @PathVariable Long id,
                                                        @Valid @RequestBody UpdateUserTypeRequest request) {
    return ResponseEntity.ok(UserTypeWebMapper.INSTANCE.domainToCreateUserTypeResponse(
        userTypeUseCase.updateUserType(id, UserTypeWebMapper.INSTANCE.updateUserTypeRequestToDomain(request))));
  }

  /**
   * Remove um tipo de usuário, desde que não haja usuários associados a ele.
   */
  @Operation(summary = "Excluir tipo de usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Tipo de usuário removido com sucesso"),
      @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado"),
      @ApiResponse(responseCode = "409", description = "Tipo de usuário em uso por um ou mais usuários")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@Parameter(description = "Id do tipo de usuário") @PathVariable Long id) {
    userTypeUseCase.deleteUserType(id);
    return ResponseEntity.noContent().build();
  }
}
