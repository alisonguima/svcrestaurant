package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.UserWebMapper;
import com.techchallenge.restaurant.adapter.input.request.user.CreateUserRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserPasswordRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserRequest;
import com.techchallenge.restaurant.adapter.input.response.user.CreateUserResponse;
import com.techchallenge.restaurant.adapter.input.response.user.GetUserResponse;
import com.techchallenge.restaurant.application.port.input.user.AssignUserTypePort;
import com.techchallenge.restaurant.application.port.input.user.CreateUserPort;
import com.techchallenge.restaurant.application.port.input.user.DeleteUserPort;
import com.techchallenge.restaurant.application.port.input.user.GetUserPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPasswordPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping(value = {"/api/v1/user"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuários", description = "Cadastro, consulta e manutenção de usuários")
public class UserController {

  private final CreateUserPort createUserUseCase;
  private final UpdateUserPort updateUserUseCase;
  private final UpdateUserPasswordPort updateUserPasswordUseCase;
  private final AssignUserTypePort assignUserTypeUseCase;
  private final GetUserPort getUserUseCase;
  private final DeleteUserPort deleteUserUseCase;

  @Operation(summary = "Criar usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "422", description = "E-mail ou login já cadastrado")
  })
  @PostMapping
  public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
    log.info("createUser - email={}, login={}", userRequest.email(), userRequest.login());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(UserWebMapper.INSTANCE.domainToCreateUserResponse(
            createUserUseCase.execute(
                UserWebMapper.INSTANCE.createUserRequestToDomain(userRequest))));
  }

  @Operation(summary = "Buscar usuário por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<GetUserResponse> getUser(
      @Parameter(description = "Id do usuário") @PathVariable Long id) {
    return ResponseEntity.ok(
        UserWebMapper.INSTANCE.domainToGetUserResponse(getUserUseCase.execute(id)));
  }

  @Operation(summary = "Atualizar usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado"),
      @ApiResponse(responseCode = "422", description = "E-mail ou login já cadastrado")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateUser(
      @Parameter(description = "Id do usuário") @PathVariable Long id,
      @Valid @RequestBody UpdateUserRequest userRequest) {
    updateUserUseCase.execute(id, UserWebMapper.INSTANCE.updateUserRequestToDomain(userRequest));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Alterar senha")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
      @ApiResponse(responseCode = "422", description = "Senha atual informada não confere")
  })
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> updatePassword(
      @Parameter(description = "Id do usuário") @PathVariable Long id,
      @Valid @RequestBody UpdateUserPasswordRequest request) {
    updateUserPasswordUseCase.execute(id, request.currentPassword(), request.newPassword());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Atribuir tipo de usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Tipo de usuário atribuído com sucesso"),
      @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado")
  })
  @PatchMapping("/{id}/user-type/{userTypeId}")
  public ResponseEntity<Void> assignUserType(
      @Parameter(description = "Id do usuário") @PathVariable Long id,
      @Parameter(description = "Id do tipo de usuário") @PathVariable Long userTypeId) {
    assignUserTypeUseCase.execute(id, userTypeId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Excluir usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "Id do usuário") @PathVariable Long id) {
    deleteUserUseCase.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
