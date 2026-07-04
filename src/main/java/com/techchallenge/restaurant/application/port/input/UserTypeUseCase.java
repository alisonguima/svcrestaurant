package com.techchallenge.restaurant.application.port.input;

import com.techchallenge.restaurant.application.domain.usertype.UserType;

import java.util.List;

/**
 * Casos de uso relacionados ao ciclo de vida de tipos de usuário.
 * Apenas os nomes {@code "Dono de Restaurante"} e {@code "Cliente"} são aceitos.
 */
public interface UserTypeUseCase {

  /**
   * Cria um novo tipo de usuário.
   *
   * @param userType dados do tipo de usuário a ser criado
   * @return tipo de usuário criado, com id preenchido
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o nome não for permitido ou já existir (case-insensitive)
   */
  UserType createUserType(UserType userType);

  /**
   * Atualiza o nome de um tipo de usuário existente.
   *
   * @param userTypeId id do tipo de usuário a ser atualizado
   * @param userType   novo nome do tipo de usuário
   * @return tipo de usuário atualizado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o tipo de usuário não existir, o nome não for permitido
   *         ou já existir em outro registro
   */
  UserType updateUserType(Long userTypeId, UserType userType);

  /**
   * Busca um tipo de usuário pelo seu identificador.
   *
   * @param userTypeId id do tipo de usuário
   * @return tipo de usuário encontrado
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o tipo de usuário não existir
   */
  UserType getUserType(Long userTypeId);

  /**
   * Lista todos os tipos de usuário cadastrados.
   *
   * @return lista de tipos de usuário
   */
  List<UserType> getUserTypes();

  /**
   * Remove um tipo de usuário, desde que não haja usuários associados a ele.
   *
   * @param userTypeId id do tipo de usuário a ser removido
   * @throws com.techchallenge.restaurant.application.exception.DefaultException
   *         se o tipo de usuário não existir ou estiver em uso por algum usuário
   */
  void deleteUserType(Long userTypeId);
}
