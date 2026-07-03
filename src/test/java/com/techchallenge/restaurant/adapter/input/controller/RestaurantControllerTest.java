package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.domain.user.User;
import com.techchallenge.restaurant.application.port.input.RestaurantUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private RestaurantUseCase restaurantUseCase;

  @Test
  void shouldCreateRestaurant() throws Exception {
    when(restaurantUseCase.createRestaurant(any(Restaurant.class)))
        .thenReturn(Restaurant.builder()
            .id(7L)
            .name("Casa do Chef")
            .address("Rua A, 123")
            .cuisineType("Brasileira")
            .openingHours("10:00-22:00")
            .owner(User.builder().id(2L).name("Joao").build())
            .lastUpdateAt(ZonedDateTime.parse("2026-07-03T15:00:00Z"))
            .build());

    mockMvc.perform(post("/api/v1/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Casa do Chef\",\"address\":\"Rua A, 123\",\"cuisineType\":\"Brasileira\",\"openingHours\":\"10:00-22:00\",\"ownerUserId\":2}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.ownerId").value("2"))
        .andExpect(jsonPath("$.ownerName").value("Joao"));
  }
}
