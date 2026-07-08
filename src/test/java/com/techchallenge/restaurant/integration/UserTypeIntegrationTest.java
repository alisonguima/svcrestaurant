package com.techchallenge.restaurant.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserTypeIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldCreateGetListUpdateAndDeleteUserType() throws Exception {
    MvcResult createResult = mockMvc.perform(post("/api/v1/user-types")
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Cliente\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Cliente"))
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(get("/api/v1/user-types/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Cliente"));

    mockMvc.perform(get("/api/v1/user-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", hasItem(id)));

    mockMvc.perform(patch("/api/v1/user-types/{id}", id)
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Dono\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Dono"));

    mockMvc.perform(delete("/api/v1/user-types/{id}", id))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/user-types/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn400WhenCreatingUserTypeWithInvalidName() throws Exception {
    mockMvc.perform(post("/api/v1/user-types")
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Invalido\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").exists());
  }

  @Test
  void shouldReturn422WhenCreatingUserTypeWithDuplicateName() throws Exception {
    createUserType("Cliente");

    mockMvc.perform(post("/api/v1/user-types")
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Cliente\"}"))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void shouldReturn404WhenGettingNonExistentUserType() throws Exception {
    mockMvc.perform(get("/api/v1/user-types/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn409WhenDeletingUserTypeInUse() throws Exception {
    Long userTypeId = createUserType("Cliente");
    createUser("Cliente Teste", "cliente@test.com", "clienteteste", userTypeId);

    mockMvc.perform(delete("/api/v1/user-types/{id}", userTypeId))
        .andExpect(status().isConflict());
  }
}
