# Restaurant Service

Backend para gestão de restaurantes, cardápios, usuários e tipos de usuário, construído em **Spring Boot 3 / Java 21** seguindo os princípios de **Arquitetura Hexagonal (Ports & Adapters)**.

O sistema permite que **Donos de Restaurante** cadastrem seus estabelecimentos e itens de cardápio, e que **Clientes** consultem essas informações, com todo o domínio isolado de frameworks e persistência.

## Sumário

- [Arquitetura](#arquitetura)
- [Stack técnica](#stack-técnica)
- [Estrutura de pacotes](#estrutura-de-pacotes)
- [Regras de negócio](#regras-de-negócio)
- [Endpoints da API](#endpoints-da-api)
- [Tratamento de erros](#tratamento-de-erros)
- [Configuração e execução](#configuração-e-execução)
- [Testes](#testes)
- [Documentação interativa (Swagger)](#documentação-interativa-swagger)

## Arquitetura

O projeto segue **Clean/Hexagonal Architecture**, com o domínio isolado de frameworks e da infraestrutura. As regras de dependência entre camadas são **validadas automaticamente por testes ArchUnit** (`src/test/java/.../architecture/CleanArchitectureTest.java`):

```
┌──────────────────────────────────────────────────────────────────────┐
│                               Config                                  │
│  UserUseCaseConfig, UserTypeUseCaseConfig, RestaurantUseCaseConfig,   │
│  MenuItemUseCaseConfig, SecurityConfig  —  composição de dependências │
└────────────────────────────────┬─────────────────────────────────────┘
                                  │
┌────────────────────────────────▼─────────────────────────────────────┐
│                              Adapter                                   │
│  Input                                     Output                      │
│  ├─ controller  (REST — injeta port.input) ├─ postgres (JPA/Repos)    │
│  ├─ request/response (DTOs)                ├─ security (BCrypt)        │
│  ├─ mapper   (Web ↔ Domínio)               ├─ time (relógio UTC)       │
│  ├─ exception (GlobalExceptionHandler)     └─ transaction (Spring TX)  │
│  └─ validation                                                          │
└────────────────────────────────┬─────────────────────────────────────┘
                                  │
┌────────────────────────────────▼─────────────────────────────────────┐
│                            Application                                 │
│  ├─ port.input   (interfaces de entrada: CreateUserPort, etc.)        │
│  ├─ port.output  (portas de saída: persistência, transação, senha…)   │
│  ├─ usecase      (implementações — POJOs puros, sem Spring)           │
│  ├─ exception    (hierarquia BaseException → 16 exceções tipadas)     │
│  └─ domain       (entidades ricas: User, Restaurant, MenuItem…)       │
└──────────────────────────────────────────────────────────────────────┘
```

> **Regra de dependência:** as setas apontam sempre para dentro. `Config` conhece tudo; `Domain` não conhece ninguém.

Regras impostas pelos testes de arquitetura (11 verificações no build):

- O **domínio** não pode depender de `port`, `adapter` ou `config`, nem de anotações Spring/JPA.
- A **camada de aplicação** não pode depender de `adapter` nem de `config`.
- **Use cases** não podem depender de Spring, Jakarta Persistence ou qualquer framework.
- **Controllers** não podem referenciar `usecase` diretamente — apenas as interfaces `port.input`.
- Implementações de **output ports** devem residir em `adapter.output`.
- Entidades JPA (`@Entity`) só podem existir em `adapter.output.postgres.model`.
- As camadas não podem ter dependências cíclicas.

## Stack técnica

| Categoria         | Tecnologia                                              |
|-------------------|----------------------------------------------------------|
| Linguagem         | Java 21                                                  |
| Framework         | Spring Boot 3.5.16 (Web, Data JPA, Validation, Security) |
| Banco de dados    | PostgreSQL 16 (produção/local), H2 (testes)              |
| Mapeamento        | MapStruct 1.6.3 (DTO ↔ Domínio ↔ Entidade)               |
| Boilerplate       | Lombok                                                   |
| Documentação      | springdoc-openapi (Swagger UI) 2.8.4                     |
| Segurança         | Spring Security (sessão stateless) + BCrypt              |
| Testes            | JUnit 5, Spring Boot Test, ArchUnit 1.3.0, JaCoCo         |
| Empacotamento     | Docker multi-stage (Maven + Eclipse Temurin JRE 21)      |

## Estrutura de pacotes

```
com.techchallenge.restaurant
├── adapter
│   ├── input
│   │   ├── controller        # REST controllers — dependem de port.input
│   │   ├── request/response  # DTOs de entrada e saída
│   │   ├── mapper             # MapStruct: request/response ↔ domínio
│   │   ├── exception          # GlobalExceptionHandler (RFC 7807 / ProblemDetail)
│   │   └── validation
│   └── output
│       ├── postgres           # @Entity, Spring Data Repositories, mappers JPA ↔ domínio
│       ├── security           # BCryptPasswordEncryptionAdapter
│       ├── time               # UtcDateTimeProvider
│       └── transaction        # SpringTransactionAdapter
├── application
│   ├── domain                 # Entidades ricas (User, Restaurant, MenuItem, UserType)
│   ├── port
│   │   ├── input              # Interfaces de entrada por agregado
│   │   │   ├── user           # CreateUserPort, UpdateUserPort, GetUserPort…
│   │   │   ├── usertype       # CreateUserTypePort, DeleteUserTypePort…
│   │   │   ├── restaurant     # CreateRestaurantPort, GetRestaurantsPort…
│   │   │   └── menuitem       # CreateMenuItemPort, UpdateMenuItemPort…
│   │   └── output             # UserPersistencePort, TransactionPort, DateTimeProviderPort…
│   ├── usecase                # Implementações — POJOs sem anotações de framework
│   │   ├── user               # CreateUserUseCase, UpdateUserUseCase…
│   │   ├── usertype
│   │   ├── restaurant
│   │   └── menuitem
│   └── exception              # BaseException + 16 exceções tipadas
├── config                     # UserUseCaseConfig, UserTypeUseCaseConfig,
│                              # RestaurantUseCaseConfig, MenuItemUseCaseConfig, SecurityConfig
└── RestaurantApplication.java
```

## Regras de negócio

- **Usuários** possuem `login` e `email` únicos; senha exigida no cadastro e validada por padrão definido em `InputValidationConstants`.
- **Tipos de usuário** (`UserType`) aceitam apenas os valores `"Dono"` e `"Cliente"`; nomes duplicados (case-insensitive) são rejeitados.
- Um tipo de usuário **não pode ser excluído** enquanto houver usuários associados a ele (`USER_TYPE_IN_USE`).
- Um **restaurante** só pode ser criado se o `ownerUserId` informado existir e possuir o tipo `"Dono"` (caso contrário, `RESTAURANT_OWNER_UNAUTHORIZED`).
- Nomes de restaurante são únicos (case-insensitive).
- **Itens de cardápio** pertencem sempre a um restaurante (`/restaurants/{restaurantId}/menu-items`) e exigem preço mínimo de `0.01`.

## Endpoints da API

Prefixo base: `/api/v1` (mais o `context-path` configurado — ver [Configuração](#configuração-e-execução)).

### Usuários — `/api/v1/user`

| Método | Caminho                              | Descrição                                  | Corpo da requisição                                          |
|--------|---------------------------------------|---------------------------------------------|---------------------------------------------------------------|
| POST   | `/api/v1/user`                        | Cria um novo usuário                        | `name, email, login, password, userTypeId`                    |
| GET    | `/api/v1/user/{id}`                   | Busca usuário por id                        | —                                                               |
| PATCH  | `/api/v1/user/{id}`                   | Atualiza dados cadastrais                   | `name?, email?, login?, userTypeId?`                           |
| PATCH  | `/api/v1/user/{id}/password`          | Altera a senha (valida a senha atual)       | `currentPassword, newPassword`                                 |
| PATCH  | `/api/v1/user/{id}/user-type/{userTypeId}` | Atribui/altera o tipo do usuário       | —                                                               |
| DELETE | `/api/v1/user/{id}`                   | Remove um usuário                           | —                                                               |

### Tipos de usuário — `/api/v1/user-types`

| Método | Caminho                        | Descrição                                         |
|--------|----------------------------------|---------------------------------------------------|
| POST   | `/api/v1/user-types`             | Cria um tipo de usuário (`"Dono"` ou `"Cliente"`) |
| GET    | `/api/v1/user-types/{id}`        | Busca por id                                      |
| GET    | `/api/v1/user-types`             | Lista todos                                       |
| PATCH  | `/api/v1/user-types/{id}`        | Atualiza o nome                                    |
| DELETE | `/api/v1/user-types/{id}`        | Remove (bloqueado se houver usuários vinculados)   |

### Restaurantes — `/api/v1/restaurants`

| Método | Caminho                          | Descrição                          | Corpo da requisição                                                     |
|--------|-----------------------------------|---------------------------------------|---------------------------------------------------------------------------|
| POST   | `/api/v1/restaurants`             | Cadastra um restaurante              | `name, address, cuisineType, openingHours, ownerUserId`                   |
| GET    | `/api/v1/restaurants/{id}`        | Busca por id                          | —                                                                          |
| GET    | `/api/v1/restaurants`             | Lista todos                           | —                                                                          |
| PATCH  | `/api/v1/restaurants/{id}`        | Atualiza dados do restaurante         | `name?, address?, cuisineType?, openingHours?, ownerUserId?`              |
| DELETE | `/api/v1/restaurants/{id}`        | Remove um restaurante                 | —                                                                          |

### Itens de cardápio — `/api/v1/restaurants/{restaurantId}/menu-items`

| Método | Caminho                                                  | Descrição                        | Corpo / parâmetros                                                             |
|--------|-------------------------------------------------------------|--------------------------------------|--------------------------------------------------------------------------------|
| POST   | `/api/v1/restaurants/{restaurantId}/menu-items`              | Cria um item de cardápio             | `name, description, price, onlyAtRestaurant, photoPath, ownerId`               |
| GET    | `/api/v1/restaurants/{restaurantId}/menu-items/{id}`         | Busca item por id                    | —                                                                               |
| GET    | `/api/v1/restaurants/{restaurantId}/menu-items`              | Lista itens do restaurante           | —                                                                               |
| PATCH  | `/api/v1/restaurants/{restaurantId}/menu-items/{id}`         | Atualiza um item                     | `name?, description?, price?, onlyAtRestaurant?, photoPath?, ownerId`          |
| DELETE | `/api/v1/restaurants/{restaurantId}/menu-items/{id}`         | Remove um item                       | query param: `ownerId`                                                          |

> `ownerId` identifica o dono do restaurante. Operações de escrita em itens de cardápio são rejeitadas com `403` se o `ownerId` não corresponder ao dono cadastrado do restaurante.

> Uma coleção Postman pronta para uso está disponível na raiz do repositório: `Restaurant.postman_collection.json` (e o ambiente `dev.postman_environment.json`).

## Tratamento de erros

Todas as respostas de erro seguem o padrão **RFC 7807 (`application/problem+json`)**, gerado pelo `GlobalExceptionHandler`, contendo `type`, `title`, `detail`, `instance` e `timestamp` (UTC). Erros de validação (`400`) incluem ainda um mapa `errors` com o campo e a mensagem correspondente.

Principais códigos de negócio e seus status HTTP:

| Status | Situações                                                                              |
|--------|-------------------------------------------------------------------------------------------|
| 400    | Corpo de requisição inválido ou falha de validação de campos (Bean Validation)             |
| 403    | `RESTAURANT_OWNER_UNAUTHORIZED` — usuário informado não é `"Dono"`               |
| 404    | Usuário, tipo de usuário, restaurante, item de cardápio ou dono não encontrados            |
| 409    | `USER_TYPE_IN_USE` — tentativa de excluir um tipo de usuário em uso                         |
| 422    | E-mail/login/nome duplicado, senha inválida, nome de tipo de usuário não permitido         |
| 500    | Erro interno inesperado                                                                      |

## Configuração e execução

### Pré-requisitos

- Java 21
- Maven 3.9+ (não há wrapper `mvnw` no projeto — use o Maven instalado na máquina)
- Docker e Docker Compose (opcional, para subir a aplicação + PostgreSQL)
- PostgreSQL 16 (caso não utilize o Docker Compose)

### Variáveis de ambiente

O arquivo `.env` na raiz do projeto define as credenciais do banco (usadas pelo `docker-compose.yml`):

```env
POSTGRES_DB=restaurant_db
POSTGRES_USER=restaurant_user
POSTGRES_PASSWORD=restaurant_password
```

### Opção 1 — Executando com Docker Compose

```bash
docker compose up --build
```

Isso sobe dois serviços:

- **postgres**: PostgreSQL 16, exposto em `localhost:5432`, com volume persistente.
- **app**: build multi-stage da aplicação (`Dockerfile`), com o perfil `local` ativo e `ddl-auto=update`, exposta em `http://localhost:8080/restaurant`.

### Opção 2 — Executando localmente com Maven

1. Suba apenas o banco de dados (via Docker ou uma instância local de PostgreSQL) com as credenciais do `.env`.
2. Rode a aplicação com o profile `local`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

3. A aplicação ficará disponível em `http://localhost:8080/restaurant`, com o datasource apontando por padrão para `jdbc:postgresql://localhost:5432/restaurant_db` (usuário/senha `restaurant_user` / `restaurant_password`, conforme `application-local.yaml`).

### Build do artefato

```bash
mvn clean package
```

Gera o jar executável em `target/restaurant-*.jar`.

## Testes

```bash
mvn test
```

- Testes unitários e de integração de controllers usam **H2** em memória; os controllers são testados via `@WebMvcTest` com `@MockBean` das interfaces `port.input`.
- `CleanArchitectureTest` valida **11 regras** de dependência entre camadas com **ArchUnit**, incluindo:
  - use cases não podem depender de Spring/JPA
  - controllers não podem referenciar `usecase` diretamente (apenas `port.input`)
- O projeto possui atualmente **166 testes**, todos passando (`BUILD SUCCESS`).
- O plugin **JaCoCo** gera relatório de cobertura em `target/site/jacoco/index.html` após a execução dos testes.

## Documentação interativa (Swagger)

Com a aplicação em execução (perfil `local`), a documentação OpenAPI fica disponível em:

- Swagger UI: `http://localhost:8080/restaurant/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/restaurant/v3/api-docs`

> A autorização HTTP (`SecurityConfig`) atualmente permite todas as requisições (`permitAll`) em modo *stateless*, sem `CSRF`/`login` habilitados. As senhas de usuário são armazenadas com hash **BCrypt**.
