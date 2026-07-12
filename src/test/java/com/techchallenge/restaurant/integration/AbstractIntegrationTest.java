package com.techchallenge.restaurant.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Sobe o contexto Spring completo (controllers, use cases, JPA, banco H2 embarcado)
 * e roda cada teste dentro de uma transação revertida ao final, garantindo isolamento
 * entre testes sem precisar limpar o banco manualmente.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected record RestaurantFixture(Long restaurantId, Long ownerId) {}

  protected Long createUserType(String name) throws Exception {
    return extractId(mockMvc.perform(post("/api/v1/user-types")
            .contentType(APPLICATION_JSON)
            .content("{\"name\":\"%s\"}".formatted(name)))
        .andReturn());
  }

  protected Long createUser(String name, String email, String login, Long userTypeId) throws Exception {
    Long userId = extractId(mockMvc.perform(post("/api/v1/user")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"%s","email":"%s","login":"%s","password":"Senha@123"}
                """.formatted(name, email, login)))
        .andReturn());
    mockMvc.perform(patch("/api/v1/user/{id}/user-type/{userTypeId}", userId, userTypeId));
    return userId;
  }

  protected Long createRestaurant(String name, Long ownerUserId) throws Exception {
    return extractId(mockMvc.perform(post("/api/v1/restaurants")
            .contentType(APPLICATION_JSON)
            .content("""
                {"name":"%s","address":"Rua A, 123","cuisineType":"Brasileira","openingHours":"10:00-22:00","ownerUserId":%d}
                """.formatted(name, ownerUserId)))
        .andReturn());
  }

  protected RestaurantFixture createRestaurantWithOwner(String restaurantName) throws Exception {
    String slug = restaurantName.toLowerCase().replace(" ", "");
    Long ownerTypeId = createUserType("Dono");
    Long ownerId = createUser(restaurantName + " Owner", slug + "@test.com", slug + "owner", ownerTypeId);
    Long restaurantId = createRestaurant(restaurantName, ownerId);
    return new RestaurantFixture(restaurantId, ownerId);
  }

  private Long extractId(MvcResult result) throws Exception {
    return Long.valueOf(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
  }
}
