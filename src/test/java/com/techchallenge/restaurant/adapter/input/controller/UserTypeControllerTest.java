package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.usertype.UserType;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
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
  private UserTypeUseCase userTypeUseCase;

  private UserType cliente() {
    return UserType.builder().id(1L).name("Cliente").build();
  }

  @Test
  void create_shouldReturn201WithBody() throws Exception {
    when(userTypeUseCase.createUserType(any(UserType.class))).thenReturn(cliente());

    mockMvc.perform(post("/api/v1/user-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Cliente\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("Cliente"));
  }

  @Test
  void get_shouldReturn200WithBody() throws Exception {
    when(userTypeUseCase.getUserType(1L)).thenReturn(cliente());

    mockMvc.perform(get("/api/v1/user-types/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("Cliente"));
  }

  @Test
  void getAll_shouldReturn200WithList() throws Exception {
    when(userTypeUseCase.getUserTypes()).thenReturn(List.of(
        cliente(),
        UserType.builder().id(2L).name("Dono de Restaurante").build()
    ));

    mockMvc.perform(get("/api/v1/user-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("1"))
        .andExpect(jsonPath("$[1].name").value("Dono de Restaurante"));
  }

  @Test
  void update_shouldReturn200WithBody() throws Exception {
    when(userTypeUseCase.updateUserType(anyLong(), any(UserType.class)))
        .thenReturn(UserType.builder().id(1L).name("Dono de Restaurante").build());

    mockMvc.perform(patch("/api/v1/user-types/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Dono de Restaurante\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("Dono de Restaurante"));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(userTypeUseCase).deleteUserType(anyLong());

    mockMvc.perform(delete("/api/v1/user-types/1"))
        .andExpect(status().isNoContent());
  }
}
