package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.adapter.input.mapper.UserTypeWebMapper;
import com.techchallenge.restaurant.adapter.input.request.usertype.CreateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.request.usertype.UpdateUserTypeRequest;
import com.techchallenge.restaurant.adapter.input.response.usertype.CreateUserTypeResponse;
import com.techchallenge.restaurant.adapter.input.response.usertype.GetUserTypeResponse;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
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
@RequestMapping("/api/v1/user-types")
@RequiredArgsConstructor
public class UserTypeController {

  private final UserTypeUseCase userTypeUseCase;

  @PostMapping
  public ResponseEntity<CreateUserTypeResponse> create(@Valid @RequestBody CreateUserTypeRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(UserTypeWebMapper.INSTANCE.domainToCreateUserTypeResponse(
            userTypeUseCase.createUserType(UserTypeWebMapper.INSTANCE.createUserTypeRequestToDomain(request))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetUserTypeResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(UserTypeWebMapper.INSTANCE.domainToGetUserTypeResponse(userTypeUseCase.getUserType(id)));
  }

  @GetMapping
  public ResponseEntity<List<GetUserTypeResponse>> getAll() {
    return ResponseEntity.ok(
        userTypeUseCase.getUserTypes().stream()
            .map(UserTypeWebMapper.INSTANCE::domainToGetUserTypeResponse)
            .toList());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CreateUserTypeResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserTypeRequest request) {
    return ResponseEntity.ok(UserTypeWebMapper.INSTANCE.domainToCreateUserTypeResponse(
        userTypeUseCase.updateUserType(id, UserTypeWebMapper.INSTANCE.updateUserTypeRequestToDomain(request))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userTypeUseCase.deleteUserType(id);
    return ResponseEntity.noContent().build();
  }
}
