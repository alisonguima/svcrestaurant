package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.enums.UserType;
import com.techchallenge.restaurant.application.port.input.UserTypeUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserTypeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private UserTypeUseCase userTypeUseCase;

  @Test
  void shouldCreateUserType() throws Exception {
    when(userTypeUseCase.createUserType(any(UserType.class)))
        .thenReturn(UserType.builder().id(1L).name("Cliente").build());

    mockMvc.perform(post("/api/v1/user-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Cliente\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value("Cliente"));
  }
}
