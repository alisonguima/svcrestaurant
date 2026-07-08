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

class MenuItemIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldCreateGetListUpdateAndDeleteMenuItem() throws Exception {
    RestaurantFixture restaurant = createRestaurantWithOwner("Casa do Chef");

    MvcResult createResult = mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId())
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Picanha","description":"Picanha na brasa","price":79.9,"onlyAtRestaurant":true,"photoPath":"/tmp/picanha.jpg","ownerId":%d}
                """.formatted(restaurant.ownerId())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Picanha"))
        .andExpect(jsonPath("$.restaurantId").value(restaurant.restaurantId().toString()))
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.restaurantId(), id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Picanha"));

    mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", hasItem(id)));

    mockMvc.perform(patch("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.restaurantId(), id)
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"Picanha Premium\",\"ownerId\":%d}".formatted(restaurant.ownerId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Picanha Premium"));

    mockMvc.perform(delete("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.restaurantId(), id)
            .param("ownerId", restaurant.ownerId().toString()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.restaurantId(), id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn400WhenPriceIsInvalid() throws Exception {
    RestaurantFixture restaurant = createRestaurantWithOwner("Casa do Chef");

    mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId())
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Picanha","description":"Picanha na brasa","price":0,"onlyAtRestaurant":true,"photoPath":"/tmp/picanha.jpg","ownerId":%d}
                """.formatted(restaurant.ownerId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.price").exists());
  }

  @Test
  void shouldReturn404WhenRestaurantDoesNotExist() throws Exception {
    mockMvc.perform(post("/api/v1/restaurants/999999/menu-items")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Picanha","description":"Picanha na brasa","price":79.9,"onlyAtRestaurant":true,"photoPath":"/tmp/picanha.jpg","ownerId":1}
                """))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn403WhenOwnerIsNotRestaurantOwner() throws Exception {
    RestaurantFixture restaurant = createRestaurantWithOwner("Casa do Chef");

    mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId())
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Picanha","description":"Picanha na brasa","price":79.9,"onlyAtRestaurant":true,"photoPath":"/tmp/picanha.jpg","ownerId":999999}
                """))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldReturn422WhenMenuItemNameAlreadyExistsInSameRestaurant() throws Exception {
    RestaurantFixture restaurant = createRestaurantWithOwner("Casa do Chef");
    createMenuItem(restaurant, "Picanha");

    mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId())
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"Picanha","description":"Outra descrição","price":59.9,"onlyAtRestaurant":false,"photoPath":"/tmp/picanha2.jpg","ownerId":%d}
                """.formatted(restaurant.ownerId())))
        .andExpect(status().isUnprocessableEntity());
  }

  private void createMenuItem(RestaurantFixture restaurant, String name) throws Exception {
    mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.restaurantId())
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"%s","description":"Descrição","price":79.9,"onlyAtRestaurant":true,"photoPath":"/tmp/foto.jpg","ownerId":%d}
                """.formatted(name, restaurant.ownerId())))
        .andExpect(status().isCreated());
  }
}
