package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.request.user.CreateUserRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserPasswordRequest;
import com.techchallenge.restaurant.adapter.input.request.user.UpdateUserRequest;
import com.techchallenge.restaurant.adapter.input.response.user.CreateUserResponse;
import com.techchallenge.restaurant.adapter.input.response.user.GetUserResponse;
import com.techchallenge.restaurant.adapter.input.mapper.UserWebMapper;
import com.techchallenge.restaurant.application.port.input.UserUseCase;
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
public class UserController {

  private final UserUseCase userUseCase;

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

  @GetMapping("/{id}")
  public ResponseEntity<GetUserResponse> getUser(@PathVariable Long id) {
    log.info("getUser - Receiving request to get user: id={}", id);
    return ResponseEntity.ok(
        UserWebMapper.INSTANCE.domainToGetUserResponse(userUseCase.getUser(id)));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest userRequest) {
    log.info("updateUser - Receiving request to update user: id={}, name={}, email={}, login={}, userTypeId={}",
        id, userRequest.name(), userRequest.email(), userRequest.login(), userRequest.userTypeId());

    userUseCase.updateUser(id,
        UserWebMapper.INSTANCE.updateUserRequestToDomain(userRequest));

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPasswordRequest updatePasswordRequest) {
    log.info("updatePassword - Receiving request to update password for userId={}", id);

    userUseCase.updatePassword(id,
        updatePasswordRequest.currentPassword(),
        updatePasswordRequest.newPassword());

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PatchMapping("/{id}/user-type/{userTypeId}")
  public ResponseEntity<Void> assignUserType(@PathVariable Long id, @PathVariable Long userTypeId) {
    log.info("assignUserType - Receiving request to assign userTypeId={} to userId={}", userTypeId, id);

    userUseCase.assignUserType(id, userTypeId);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    log.info("deleteUser - Receiving request to delete user with userId={}", id);

    userUseCase.deleteUser(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
