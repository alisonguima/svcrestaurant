package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.port.input.user.AssignUserTypePort;
import com.techchallenge.restaurant.application.port.input.user.CreateUserPort;
import com.techchallenge.restaurant.application.port.input.user.DeleteUserPort;
import com.techchallenge.restaurant.application.port.input.user.GetUserPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPasswordPort;
import com.techchallenge.restaurant.application.port.input.user.UpdateUserPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CreateUserPort createUserUseCase;

  @MockBean
  private UpdateUserPort updateUserUseCase;

  @MockBean
  private UpdateUserPasswordPort updateUserPasswordUseCase;

  @MockBean
  private AssignUserTypePort assignUserTypeUseCase;

  @MockBean
  private GetUserPort getUserUseCase;

  @MockBean
  private DeleteUserPort deleteUserUseCase;

  private User userDomain() {
    return new User(1L, "João Silva", "joao@test.com", "joao", "$2a$10$hashed",
        new UserType(2L, UserType.CLIENTE), ZonedDateTime.parse("2026-07-03T12:00:00Z"));
  }

  @Test
  void createUser_shouldReturn201WithResponseBody() throws Exception {
    when(createUserUseCase.execute(any(User.class))).thenReturn(userDomain());

    mockMvc.perform(post("/api/v1/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "name": "João Silva",
                  "email": "joao@test.com",
                  "login": "joao",
                  "password": "Password1!",
                  "userTypeId": 2
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("João Silva"))
        .andExpect(jsonPath("$.email").value("joao@test.com"))
        .andExpect(jsonPath("$.login").value("joao"))
        .andExpect(jsonPath("$.userType.id").value("2"))
        .andExpect(jsonPath("$.userType.name").value(UserType.CLIENTE));
  }

  @Test
  void getUser_shouldReturn200WithResponseBody() throws Exception {
    when(getUserUseCase.execute(1L)).thenReturn(userDomain());

    mockMvc.perform(get("/api/v1/user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("João Silva"))
        .andExpect(jsonPath("$.email").value("joao@test.com"))
        .andExpect(jsonPath("$.login").value("joao"))
        .andExpect(jsonPath("$.userType.name").value(UserType.CLIENTE));
  }

  @Test
  void updateUser_shouldReturn204() throws Exception {
    doNothing().when(updateUserUseCase).execute(anyLong(), any(User.class));

    mockMvc.perform(patch("/api/v1/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "name": "João Atualizado",
                  "email": "joao.novo@test.com",
                  "login": "joaonovo"
                }
                """))
        .andExpect(status().isNoContent());
  }

  @Test
  void updatePassword_shouldReturn204() throws Exception {
    doNothing().when(updateUserPasswordUseCase).execute(anyLong(), anyString(), anyString());

    mockMvc.perform(patch("/api/v1/user/1/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "currentPassword": "OldPass1!",
                  "newPassword": "NewPass1!"
                }
                """))
        .andExpect(status().isNoContent());
  }

  @Test
  void assignUserType_shouldReturn204() throws Exception {
    doNothing().when(assignUserTypeUseCase).execute(anyLong(), anyLong());

    mockMvc.perform(patch("/api/v1/user/1/user-type/2"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteUser_shouldReturn204() throws Exception {
    doNothing().when(deleteUserUseCase).execute(anyLong());

    mockMvc.perform(delete("/api/v1/user/1"))
        .andExpect(status().isNoContent());
  }
}
