package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.request.user.CreateUserRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserPasswordRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserRequest;
import com.techchallenge.restaurant.adapter.input.response.user.CreateUserResponse;
import com.techchallenge.restaurant.adapter.input.response.user.GetUserResponse;
import com.techchallenge.restaurant.adapter.input.mapper.UserWebMapper;
import com.techchallenge.restaurant.application.port.input.UserUseCase;
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

/**
 * Expõe as operações de CRUD de usuários, além de casos de uso específicos
 * como troca de senha e atribuição de tipo de usuário.
 */
@RestController
@RequestMapping(value = {"/api/v1/user"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuários", description = "Cadastro, consulta e manutenção de usuários")
public class UserController {

  private final UserUseCase userUseCase;

  /**
   * Cria um novo usuário. {@code login} e {@code email} devem ser únicos.
   */
  @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário no sistema.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Tipo de usuário informado não encontrado"),
      @ApiResponse(responseCode = "422", description = "E-mail ou login já cadastrado")
  })
  @PostMapping
  public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest) {

    log.info("createUser - Receiving request to create user: name={}, email={}, login={}, userTypeId={}",
        userRequest.name(), userRequest.email(), userRequest.login(), userRequest.userTypeId());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(UserWebMapper.INSTANCE.domainToCreateUserResponse(
            userUseCase.createUser(
                UserWebMapper.INSTANCE.createUserRequestToDomain(userRequest))));
  }

  /**
   * Busca um usuário pelo seu identificador.
   */
  @Operation(summary = "Buscar usuário por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<GetUserResponse> getUser(@Parameter(description = "Id do usuário") @PathVariable Long id) {
    log.info("getUser - Receiving request to get user: id={}", id);
    return ResponseEntity.ok(
        UserWebMapper.INSTANCE.domainToGetUserResponse(userUseCase.getUser(id)));
  }

  /**
   * Atualiza parcialmente os dados cadastrais de um usuário.
   * Campos não informados no corpo da requisição permanecem inalterados.
   */
  @Operation(summary = "Atualizar usuário", description = "Atualização parcial dos dados cadastrais do usuário.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado"),
      @ApiResponse(responseCode = "422", description = "E-mail ou login já cadastrado")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateUser(@Parameter(description = "Id do usuário") @PathVariable Long id,
                                          @Valid @RequestBody UpdateUserRequest userRequest) {
    log.info("updateUser - Receiving request to update user: id={}, name={}, email={}, login={}, userTypeId={}",
        id, userRequest.name(), userRequest.email(), userRequest.login(), userRequest.userTypeId());

    userUseCase.updateUser(id,
        UserWebMapper.INSTANCE.updateUserRequestToDomain(userRequest));

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  /**
   * Altera a senha do usuário, exigindo a confirmação da senha atual.
   */
  @Operation(summary = "Alterar senha", description = "Troca a senha do usuário mediante confirmação da senha atual.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
      @ApiResponse(responseCode = "422", description = "Senha atual informada não confere")
  })
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> updatePassword(@Parameter(description = "Id do usuário") @PathVariable Long id,
                                              @Valid @RequestBody UpdateUserPasswordRequest updatePasswordRequest) {
    log.info("updatePassword - Receiving request to update password for userId={}", id);

    userUseCase.updatePassword(id,
        updatePasswordRequest.currentPassword(),
        updatePasswordRequest.newPassword());

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  /**
   * Atribui um tipo de usuário já existente ao usuário informado.
   */
  @Operation(summary = "Atribuir tipo de usuário", description = "Associa um tipo de usuário existente ao usuário informado.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Tipo de usuário atribuído com sucesso"),
      @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado")
  })
  @PatchMapping("/{id}/user-type/{userTypeId}")
  public ResponseEntity<Void> assignUserType(@Parameter(description = "Id do usuário") @PathVariable Long id,
                                              @Parameter(description = "Id do tipo de usuário") @PathVariable Long userTypeId) {
    log.info("assignUserType - Receiving request to assign userTypeId={} to userId={}", userTypeId, id);

    userUseCase.assignUserType(id, userTypeId);

    return ResponseEntity.noContent().build();
  }

  /**
   * Remove um usuário pelo seu identificador.
   */
  @Operation(summary = "Excluir usuário")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@Parameter(description = "Id do usuário") @PathVariable Long id) {
    log.info("deleteUser - Receiving request to delete user with userId={}", id);

    userUseCase.deleteUser(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
