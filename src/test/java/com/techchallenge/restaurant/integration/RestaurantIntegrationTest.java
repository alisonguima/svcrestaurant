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

class RestaurantIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldCreateGetListUpdateAndDeleteRestaurant() throws Exception {
    Long ownerTypeId = createUserType("Dono");
    Long ownerId = createUser("Chef Owner", "chef@test.com", "chefowner", ownerTypeId);

    MvcResult createResult = mockMvc.perform(post("/api/v1/restaurants")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Casa do Chef","address":"Rua A, 123","cuisineType":"Brasileira","openingHours":"10:00-22:00","ownerUserId":%d}
                """.formatted(ownerId)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Casa do Chef"))
        .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
        .andExpect(jsonPath("$.ownerName").value("Chef Owner"))
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(get("/api/v1/restaurants/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Casa do Chef"));

    mockMvc.perform(get("/api/v1/restaurants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", hasItem(id)));

    mockMvc.perform(patch("/api/v1/restaurants/{id}", id)
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Casa do Chef - Novo\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Casa do Chef - Novo"));

    mockMvc.perform(delete("/api/v1/restaurants/{id}", id))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/restaurants/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404WhenOwnerDoesNotExist() throws Exception {
    mockMvc.perform(post("/api/v1/restaurants")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Restaurante Fantasma","address":"Rua A, 123","cuisineType":"Brasileira","openingHours":"10:00-22:00","ownerUserId":999999}
                """))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn403WhenOwnerIsNotRestaurantOwnerType() throws Exception {
    Long clientTypeId = createUserType("Cliente");
    Long clientId = createUser("Cliente Teste", "cliente@test.com", "clienteteste", clientTypeId);

    mockMvc.perform(post("/api/v1/restaurants")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Restaurante Invalido","address":"Rua A, 123","cuisineType":"Brasileira","openingHours":"10:00-22:00","ownerUserId":%d}
                """.formatted(clientId)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldReturn422WhenRestaurantNameAlreadyExists() throws Exception {
    Long ownerTypeId = createUserType("Dono");
    Long ownerId = createUser("Chef Owner", "chef@test.com", "chefowner", ownerTypeId);
    createRestaurant("Casa do Chef", ownerId);

    mockMvc.perform(post("/api/v1/restaurants")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Casa do Chef","address":"Rua B, 456","cuisineType":"Italiana","openingHours":"11:00-23:00","ownerUserId":%d}
                """.formatted(ownerId)))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void shouldReturn404WhenGettingNonExistentRestaurant() throws Exception {
    mockMvc.perform(get("/api/v1/restaurants/999999"))
        .andExpect(status().isNotFound());
  }
}
