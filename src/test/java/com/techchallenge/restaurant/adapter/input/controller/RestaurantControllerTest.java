package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.port.input.restaurant.CreateRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.DeleteRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantPort;
import com.techchallenge.restaurant.application.port.input.restaurant.GetRestaurantsPort;
import com.techchallenge.restaurant.application.port.input.restaurant.UpdateRestaurantPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
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

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CreateRestaurantPort createRestaurantUseCase;

  @MockBean
  private UpdateRestaurantPort updateRestaurantUseCase;

  @MockBean
  private GetRestaurantPort getRestaurantUseCase;

  @MockBean
  private GetRestaurantsPort getRestaurantsUseCase;

  @MockBean
  private DeleteRestaurantPort deleteRestaurantUseCase;

  private Restaurant restaurantDomain() {
    return Restaurant.builder()
        .id(7L)
        .name("Casa do Chef")
        .address("Rua A, 123")
        .cuisineType("Brasileira")
        .openingHours("10:00-22:00")
        .owner(User.builder().id(2L).name("Joao").build())
        .lastUpdateAt(ZonedDateTime.parse("2026-07-03T15:00:00Z"))
        .build();
  }

  @Test
  void create_shouldReturn201WithBody() throws Exception {
    when(createRestaurantUseCase.execute(any(Restaurant.class))).thenReturn(restaurantDomain());

    mockMvc.perform(post("/api/v1/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Casa do Chef\",\"address\":\"Rua A, 123\",\"cuisineType\":\"Brasileira\",\"openingHours\":\"10:00-22:00\",\"ownerUserId\":2}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.name").value("Casa do Chef"))
        .andExpect(jsonPath("$.ownerId").value("2"))
        .andExpect(jsonPath("$.ownerName").value("Joao"));
  }

  @Test
  void get_shouldReturn200WithBody() throws Exception {
    when(getRestaurantUseCase.execute(7L)).thenReturn(restaurantDomain());

    mockMvc.perform(get("/api/v1/restaurants/7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.name").value("Casa do Chef"))
        .andExpect(jsonPath("$.ownerId").value("2"));
  }

  @Test
  void getAll_shouldReturn200WithList() throws Exception {
    when(getRestaurantsUseCase.execute()).thenReturn(List.of(restaurantDomain()));

    mockMvc.perform(get("/api/v1/restaurants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("7"))
        .andExpect(jsonPath("$[0].name").value("Casa do Chef"));
  }

  @Test
  void update_shouldReturn200WithBody() throws Exception {
    when(updateRestaurantUseCase.execute(anyLong(), any(Restaurant.class))).thenReturn(restaurantDomain());

    mockMvc.perform(patch("/api/v1/restaurants/7")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Casa do Chef Atualizado\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.name").value("Casa do Chef"));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(deleteRestaurantUseCase).execute(anyLong());

    mockMvc.perform(delete("/api/v1/restaurants/7"))
        .andExpect(status().isNoContent());
  }
}
