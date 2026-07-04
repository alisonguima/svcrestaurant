package com.techchallenge.restaurant.adapter.input.controller;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;
import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;
import com.techchallenge.restaurant.application.port.input.MenuItemUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MenuItemUseCase menuItemUseCase;

  private MenuItem menuItemDomain() {
    return MenuItem.builder()
        .id(11L)
        .name("Picanha")
        .description("Picanha na brasa")
        .price(BigDecimal.valueOf(79.9))
        .onlyAtRestaurant(true)
        .photoPath("/tmp/picanha.jpg")
        .restaurant(Restaurant.builder().id(7L).name("Casa do Chef").build())
        .lastUpdateAt(ZonedDateTime.parse("2026-07-03T15:00:00Z"))
        .build();
  }

  @Test
  void create_shouldReturn201WithBody() throws Exception {
    when(menuItemUseCase.createMenuItem(any(Long.class), any(MenuItem.class))).thenReturn(menuItemDomain());

    mockMvc.perform(post("/api/v1/restaurants/7/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Picanha\",\"description\":\"Picanha na brasa\",\"price\":79.9,\"onlyAtRestaurant\":true,\"photoPath\":\"/tmp/picanha.jpg\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("11"))
        .andExpect(jsonPath("$.name").value("Picanha"))
        .andExpect(jsonPath("$.restaurantId").value("7"))
        .andExpect(jsonPath("$.restaurantName").value("Casa do Chef"));
  }

  @Test
  void get_shouldReturn200WithBody() throws Exception {
    when(menuItemUseCase.getMenuItem(7L, 11L)).thenReturn(menuItemDomain());

    mockMvc.perform(get("/api/v1/restaurants/7/menu-items/11"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("11"))
        .andExpect(jsonPath("$.name").value("Picanha"))
        .andExpect(jsonPath("$.restaurantId").value("7"));
  }

  @Test
  void getAll_shouldReturn200WithList() throws Exception {
    when(menuItemUseCase.getMenuItemsByRestaurant(7L)).thenReturn(List.of(menuItemDomain()));

    mockMvc.perform(get("/api/v1/restaurants/7/menu-items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("11"))
        .andExpect(jsonPath("$[0].name").value("Picanha"));
  }

  @Test
  void update_shouldReturn200WithBody() throws Exception {
    when(menuItemUseCase.updateMenuItem(anyLong(), anyLong(), any(MenuItem.class))).thenReturn(menuItemDomain());

    mockMvc.perform(patch("/api/v1/restaurants/7/menu-items/11")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Picanha Atualizada\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("11"))
        .andExpect(jsonPath("$.name").value("Picanha"));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(menuItemUseCase).deleteMenuItem(anyLong(), anyLong());

    mockMvc.perform(delete("/api/v1/restaurants/7/menu-items/11"))
        .andExpect(status().isNoContent());
  }
}
