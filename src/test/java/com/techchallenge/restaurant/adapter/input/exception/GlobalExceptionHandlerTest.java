package com.techchallenge.restaurant.adapter.input.exception;

import com.techchallenge.restaurant.application.exception.DefaultException;
import com.techchallenge.restaurant.application.exception.ErrorCode;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(new TestController())
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
    // Envia string vazia para um campo com @NotBlank + @Pattern: ambos falham
    // sobre o mesmo field "name", acionando o merge function (existing, duplicate) -> existing
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

  // ── handleBusiness — cada arm do switch ───────────────────────────────

  @ParameterizedTest
  @MethodSource("businessErrorCases")
  void handleBusiness_shouldMapEachErrorCodeToExpectedHttpStatus(ErrorCode code, int expectedStatus) throws Exception {
    mockMvc.perform(post("/test/business")
            .param("code", code.name()))
        .andExpect(status().is(expectedStatus))
        .andExpect(jsonPath("$.detail").value("error: " + code.name()))
        .andExpect(jsonPath("$.timestamp").value(notNullValue()));
  }

  static Stream<Arguments> businessErrorCases() {
    return Stream.of(
        Arguments.of(ErrorCode.EMAIL_ALREADY_EXISTS,           422),
        Arguments.of(ErrorCode.LOGIN_ALREADY_EXISTS,           422),
        Arguments.of(ErrorCode.USER_NOT_FOUND,                 404),
        Arguments.of(ErrorCode.INVALID_PASSWORD,               422),
        Arguments.of(ErrorCode.USER_TYPE_NOT_FOUND,            404),
        Arguments.of(ErrorCode.USER_TYPE_INVALID_NAME,         422),
        Arguments.of(ErrorCode.USER_TYPE_ALREADY_EXISTS,       422),
        Arguments.of(ErrorCode.RESTAURANT_ALREADY_EXISTS,      422),
        Arguments.of(ErrorCode.MENU_ITEM_ALREADY_EXISTS,       422),
        Arguments.of(ErrorCode.USER_TYPE_IN_USE,               409),
        Arguments.of(ErrorCode.RESTAURANT_NOT_FOUND,           404),
        Arguments.of(ErrorCode.RESTAURANT_OWNER_NOT_FOUND,     404),
        Arguments.of(ErrorCode.RESTAURANT_OWNER_UNAUTHORIZED,  403),
        Arguments.of(ErrorCode.MENU_ITEM_NOT_FOUND,            404),
        Arguments.of(ErrorCode.MENU_ITEM_RESTAURANT_NOT_FOUND, 404)
    );
  }

  // ── Controlador auxiliar usado apenas nestes testes ───────────────────

  @RestController
  static class TestController {

    @PostMapping("/test/validation")
    void validation(@Valid @RequestBody ValidBody body) {}

    @PostMapping("/test/business")
    void business(@RequestParam String code) {
      throw new DefaultException(ErrorCode.valueOf(code), "error: " + code);
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
