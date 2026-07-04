package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.menu.MenuItem;

import java.util.List;

/**
 * Casos de uso relacionados ao ciclo de vida dos itens de cardápio,
 * sempre vinculados a um restaurante existente.
 */
public interface MenuItemUseCase {

  /**
   * Cria um novo item de cardápio para o restaurante informado.
   *
   * @param restaurantId id do restaurante ao qual o item pertencerá
   * @param menuItem     dados do item de cardápio a ser criado
   * @return item de cardápio criado, com id e demais campos preenchidos
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante não existir ou já houver um item com o mesmo nome nele
   */
  MenuItem createMenuItem(Long restaurantId, MenuItem menuItem);

  /**
   * Atualiza parcialmente os dados de um item de cardápio existente.
   *
   * @param restaurantId id do restaurante ao qual o item pertence
   * @param menuItemId   id do item de cardápio a ser atualizado
   * @param menuItem     campos a serem alterados; campos nulos são ignorados
   * @return item de cardápio atualizado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante ou o item de cardápio não existirem
   */
  MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem menuItem);

  /**
   * Busca um item de cardápio pelo seu identificador, dentro do restaurante informado.
   *
   * @param restaurantId id do restaurante ao qual o item pertence
   * @param menuItemId   id do item de cardápio
   * @return item de cardápio encontrado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante ou o item de cardápio não existirem
   */
  MenuItem getMenuItem(Long restaurantId, Long menuItemId);

  /**
   * Lista todos os itens de cardápio de um restaurante.
   *
   * @param restaurantId id do restaurante
   * @return lista de itens de cardápio do restaurante
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante não existir
   */
  List<MenuItem> getMenuItemsByRestaurant(Long restaurantId);

  /**
   * Remove um item de cardápio.
   *
   * @param restaurantId id do restaurante ao qual o item pertence
   * @param menuItemId   id do item de cardápio a ser removido
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o restaurante ou o item de cardápio não existirem
   */
  void deleteMenuItem(Long restaurantId, Long menuItemId);
}
