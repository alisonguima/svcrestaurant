package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.restaurant.Restaurant;

import java.util.List;

/**
 * Casos de uso relacionados ao ciclo de vida de restaurantes.
 */
public interface RestaurantUseCase {

  /**
   * Cadastra um novo restaurante.
   *
   * @param restaurant dados do restaurante, incluindo o usuário dono
   * @return restaurante criado, com id e demais campos preenchidos
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se já existir um restaurante com o mesmo nome, o dono não existir
   *         ou não for do tipo {@code "Dono de Restaurante"}
   */
  Restaurant createRestaurant(Restaurant restaurant);

  /**
   * Atualiza parcialmente os dados de um restaurante existente, podendo
   * transferir a titularidade para outro usuário dono.
   *
   * @param restaurantId id do restaurante a ser atualizado
   * @param restaurant   campos a serem alterados; campos nulos são ignorados
   * @return restaurante atualizado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante não existir ou o novo dono informado não existir
   */
  Restaurant updateRestaurant(Long restaurantId, Restaurant restaurant);

  /**
   * Busca um restaurante pelo seu identificador.
   *
   * @param restaurantId id do restaurante
   * @return restaurante encontrado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante não existir
   */
  Restaurant getRestaurant(Long restaurantId);

  /**
   * Lista todos os restaurantes cadastrados.
   *
   * @return lista de restaurantes
   */
  List<Restaurant> getRestaurants();

  /**
   * Remove um restaurante.
   *
   * @param restaurantId id do restaurante a ser removido
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante não existir
   */
  void deleteRestaurant(Long restaurantId);
}
