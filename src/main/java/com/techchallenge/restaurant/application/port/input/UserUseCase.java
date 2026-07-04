package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.user.User;

/**
 * Casos de uso relacionados ao ciclo de vida de usuários.
 */
public interface UserUseCase {

  /**
   * Cria um novo usuário.
   *
   * @param user dados do usuário a ser criado
   * @return usuário criado, com id e demais campos preenchidos
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o e-mail/login já estiverem em uso ou o tipo de usuário informado não existir
   */
  User createUser(User user);

  /**
   * Atualiza parcialmente os dados cadastrais de um usuário existente.
   *
   * @param userId id do usuário a ser atualizado
   * @param user   campos a serem alterados; campos nulos são ignorados
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o usuário não existir, o e-mail/login já estiverem em uso
   *         ou o tipo de usuário informado não existir
   */
  void updateUser(Long userId, User user);

  /**
   * Altera a senha de um usuário, validando a senha atual antes da troca.
   *
   * @param userId          id do usuário
   * @param currentPassword senha atual, validada contra o hash armazenado
   * @param newPassword     nova senha a ser armazenada (com hash)
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o usuário não existir ou a senha atual não conferir
   */
  void updatePassword(Long userId, String currentPassword, String newPassword);

  /**
   * Associa um tipo de usuário já existente a um usuário.
   *
   * @param userId     id do usuário
   * @param userTypeId id do tipo de usuário a ser associado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o usuário ou o tipo de usuário não existirem
   */
  void assignUserType(Long userId, Long userTypeId);

  /**
   * Remove um usuário.
   *
   * @param userId id do usuário a ser removido
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o usuário não existir
   */
  void deleteUser(Long userId);

  /**
   * Busca um usuário pelo seu identificador.
   *
   * @param userId id do usuário
   * @return usuário encontrado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o usuário não existir
   */
  User getUser(Long userId);
}
