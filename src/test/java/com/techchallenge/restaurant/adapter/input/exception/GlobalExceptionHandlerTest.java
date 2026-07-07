package com.techchallenge.restaurant.adapter.input.exception;

import com.techchallenge.restaurant.application.exception.BaseException;
import com.techchallenge.restaurant.application.exception.EmailAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.InvalidPasswordException;
import com.techchallenge.restaurant.application.exception.LoginAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.MenuItemAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.MenuItemNotFoundException;
import com.techchallenge.restaurant.application.exception.MenuItemOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.MenuItemRestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.RestaurantNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerNotFoundException;
import com.techchallenge.restaurant.application.exception.RestaurantOwnerUnauthorizedException;
import com.techchallenge.restaurant.application.exception.UserNotFoundException;
import com.techchallenge.restaurant.application.exception.UserTypeAlreadyExistsException;
import com.techchallenge.restaurant.application.exception.UserTypeInUseException;
import com.techchallenge.restaurant.application.exception.UserTypeInvalidNameException;
import com.techchallenge.restaurant.application.exception.UserTypeNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;
  private final AtomicReference<BaseException> pendingException = new AtomicReference<>();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(new TestController(pendingException))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  // ── handleValidation ──────────────────────────────────────────────────

  @Test
  void handleValidation_shouldReturn400WithFieldErrorsMap() throws Exception {
    mockMvc.perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(ErrorResponseConstants.ERROR_TITLE_VALIDATION))
        .andExpect(jsonPath("$.detail").value(ErrorResponseConstants.ERROR_DETAIL_VALIDATION))
        .andExpect(jsonPath("$.errors.name").exists())
        .andExpect(jsonPath("$.timestamp").value(notNullValue()));
  }

  @Test
  void handleValidation_withMultipleErrorsOnSameField_shouldMergeDuplicateKeys() throws Exception {
    mockMvc.perform(post("/test/validation-multi")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").exists());
  }

  // ── handleNotReadable ──────────────────────────────────────────────────

  @Test
  void handleNotReadable_withMalformedJson_shouldReturnGenericDetail() throws Exception {
    mockMvc.perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("not-json"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(ErrorResponseConstants.ERROR_TITLE_INVALID_REQUEST))
        .andExpect(jsonPath("$.detail").value("Invalid request body"))
        .andExpect(jsonPath("$.timestamp").value(notNullValue()));
  }

  @Test
  void handleNotReadable_withInvalidUserTypeIdValue_shouldReturnSpecificMessage() throws Exception {
    mockMvc.perform(post("/test/usertype-body")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"userTypeId\": \"abc\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(ErrorResponseConstants.ERROR_TITLE_INVALID_REQUEST))
        .andExpect(jsonPath("$.detail").value("Invalid userTypeId value"));
  }

  @Test
  void handleNotReadable_withInvalidOtherField_shouldReturnFieldNameInDetail() throws Exception {
    mockMvc.perform(post("/test/price-body")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"price\": \"abc\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(containsString("price")));
  }

  // ── handleGeneric ──────────────────────────────────────────────────────

  @Test
  void handleGeneric_shouldReturn500WithInternalServerDetail() throws Exception {
    mockMvc.perform(post("/test/generic"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.title").value(ErrorResponseConstants.ERROR_TITLE_INTERNAL_SERVER))
        .andExpect(jsonPath("$.detail").value(ErrorResponseConstants.ERROR_DETAIL_INTERNAL_SERVER))
        .andExpect(jsonPath("$.timestamp").value(notNullValue()));
  }

  // ── handleBusiness — cada exception tipada ────────────────────────────

  @ParameterizedTest
  @MethodSource("businessErrorCases")
  void handleBusiness_shouldMapEachExceptionToExpectedHttpStatus(
      BaseException ex, int expectedStatus) throws Exception {
    pendingException.set(ex);
    mockMvc.perform(post("/test/business"))
        .andExpect(status().is(expectedStatus))
        .andExpect(jsonPath("$.detail").value(ex.getMessage()))
        .andExpect(jsonPath("$.timestamp").value(notNullValue()));
  }

  static Stream<Arguments> businessErrorCases() {
    return Stream.of(
        Arguments.of(new EmailAlreadyExistsException(),           422),
        Arguments.of(new LoginAlreadyExistsException(),           422),
        Arguments.of(new UserNotFoundException(1L),               404),
        Arguments.of(new InvalidPasswordException(),              422),
        Arguments.of(new UserTypeNotFoundException(1L),           404),
        Arguments.of(new UserTypeInvalidNameException(),          422),
        Arguments.of(new UserTypeAlreadyExistsException(),        422),
        Arguments.of(new RestaurantAlreadyExistsException(),      422),
        Arguments.of(new MenuItemAlreadyExistsException(),        422),
        Arguments.of(new UserTypeInUseException(),                409),
        Arguments.of(new RestaurantNotFoundException(1L),         404),
        Arguments.of(new RestaurantOwnerNotFoundException(),      404),
        Arguments.of(new RestaurantOwnerUnauthorizedException(),  403),
        Arguments.of(new MenuItemNotFoundException(1L),           404),
        Arguments.of(new MenuItemRestaurantNotFoundException(),   404),
        Arguments.of(new MenuItemOwnerUnauthorizedException(),    403)
    );
  }

  // ── Controlador auxiliar usado apenas nestes testes ───────────────────

  @RestController
  static class TestController {

    private final AtomicReference<BaseException> pending;

    TestController(AtomicReference<BaseException> pending) {
      this.pending = pending;
    }

    @PostMapping("/test/validation")
    void validation(@Valid @RequestBody ValidBody body) {}

    @PostMapping("/test/business")
    void business() {
      BaseException ex = pending.getAndSet(null);
      if (ex != null) throw ex;
    }

    @PostMapping("/test/generic")
    void generic() {
      throw new RuntimeException("unexpected error");
    }

    @PostMapping("/test/validation-multi")
    void validationMulti(@Valid @RequestBody MultiConstraintBody body) {}

    @PostMapping("/test/usertype-body")
    void userTypeBody(@RequestBody UserTypeBody body) {}

    @PostMapping("/test/price-body")
    void priceBody(@RequestBody PriceBody body) {}

    record ValidBody(@NotBlank String name) {}
    record MultiConstraintBody(@NotBlank @Pattern(regexp = "[A-Z]+") String name) {}
    record UserTypeBody(Long userTypeId) {}
    record PriceBody(Long price) {}
  }
}
