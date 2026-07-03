package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private MenuItemUseCase menuItemUseCase;

  @Test
  void shouldCreateMenuItem() throws Exception {
    when(menuItemUseCase.createMenuItem(any(Long.class), any(MenuItem.class)))
        .thenReturn(MenuItem.builder()
            .id(11L)
            .name("Picanha")
            .description("Picanha na brasa")
            .price(BigDecimal.valueOf(79.9))
            .onlyAtRestaurant(true)
            .photoPath("/tmp/picanha.jpg")
            .restaurant(Restaurant.builder().id(7L).name("Casa do Chef").build())
            .lastUpdateAt(ZonedDateTime.parse("2026-07-03T15:00:00Z"))
            .build());

    mockMvc.perform(post("/api/v1/restaurants/7/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Picanha\",\"description\":\"Picanha na brasa\",\"price\":79.9,\"onlyAtRestaurant\":true,\"photoPath\":\"/tmp/picanha.jpg\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("11"))
        .andExpect(jsonPath("$.restaurantId").value("7"))
        .andExpect(jsonPath("$.restaurantName").value("Casa do Chef"));
  }
}
