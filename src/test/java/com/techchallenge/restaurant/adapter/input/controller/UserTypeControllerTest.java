package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.port.input.usertype.CreateUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.DeleteUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypePort;
import com.techchallenge.restaurant.application.port.input.usertype.GetUserTypesPort;
import com.techchallenge.restaurant.application.port.input.usertype.UpdateUserTypePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserTypeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CreateUserTypePort createUserTypeUseCase;

  @MockBean
  private UpdateUserTypePort updateUserTypeUseCase;

  @MockBean
  private GetUserTypePort getUserTypeUseCase;

  @MockBean
  private GetUserTypesPort getUserTypesUseCase;

  @MockBean
  private DeleteUserTypePort deleteUserTypeUseCase;

  private UserType cliente() {
    return new UserType(1L, UserType.CLIENTE);
  }

  @Test
  void create_shouldReturn201WithBody() throws Exception {
    when(createUserTypeUseCase.execute(any(UserType.class))).thenReturn(cliente());

    mockMvc.perform(post("/api/v1/user-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Cliente\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(UserType.CLIENTE));
  }

  @Test
  void get_shouldReturn200WithBody() throws Exception {
    when(getUserTypeUseCase.execute(1L)).thenReturn(cliente());

    mockMvc.perform(get("/api/v1/user-types/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(UserType.CLIENTE));
  }

  @Test
  void getAll_shouldReturn200WithList() throws Exception {
    when(getUserTypesUseCase.execute()).thenReturn(List.of(
        cliente(),
        new UserType(2L, UserType.DONO)
    ));

    mockMvc.perform(get("/api/v1/user-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("1"))
        .andExpect(jsonPath("$[1].name").value(UserType.DONO));
  }

  @Test
  void update_shouldReturn200WithBody() throws Exception {
    when(updateUserTypeUseCase.execute(anyLong(), any(UserType.class)))
        .thenReturn(new UserType(1L, UserType.DONO));

    mockMvc.perform(patch("/api/v1/user-types/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Dono\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(UserType.DONO));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(deleteUserTypeUseCase).execute(anyLong());

    mockMvc.perform(delete("/api/v1/user-types/1"))
        .andExpect(status().isNoContent());
  }
}
