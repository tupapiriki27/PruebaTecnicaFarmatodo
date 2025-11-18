package co.com.testapp.testapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.com.testapp.testapp.dto.TokenizationRequest;
import co.com.testapp.testapp.dto.TokenizationResponse;
import co.com.testapp.testapp.exception.TokenizationRejectedException;
import co.com.testapp.testapp.service.TokenizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
class TokenizationControllerTest {

  private static final String TOKENIZATION_URL = "/api/v1/tokenization/tokens";
  private static final String API_KEY_HEADER = "X-API-Key";
  private static final String VALID_CARD_NUMBER = "4111111111111111";
  private static final String VALID_CVV = "123";
  private static final String VALID_EXPIRATION = "12/25";
  private static final String VALID_CARDHOLDER = "John Doe";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TokenizationService tokenizationService;

  @Value("${tokenization.api.key}")
  private String tokenizationApiKey;

  @Test
  void createToken_WithValidRequest_ShouldReturnCreated() throws Exception {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    TokenizationResponse response = TokenizationResponse.builder()
        .token("tok_abc123")
        .lastFourDigits("1111")
        .cardBrand("VISA")
        .expirationDate(VALID_EXPIRATION)
        .createdAt(LocalDateTime.now())
        .active(true)
        .build();

    when(tokenizationService.createToken(any(TokenizationRequest.class))).thenReturn(response);

    mockMvc.perform(post(TOKENIZATION_URL)
            .header(API_KEY_HEADER, tokenizationApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("tok_abc123"))
        .andExpect(jsonPath("$.lastFourDigits").value("1111"))
        .andExpect(jsonPath("$.cardBrand").value("VISA"));
  }

  @Test
  void createToken_WithoutApiKey_ShouldReturnForbidden() throws Exception {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    mockMvc.perform(post(TOKENIZATION_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void createToken_WithInvalidApiKey_ShouldReturnForbidden() throws Exception {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    mockMvc.perform(post(TOKENIZATION_URL)
            .header(API_KEY_HEADER, "invalid-key")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void createToken_WithInvalidCardNumber_ShouldReturnBadRequest() throws Exception {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber("123")
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    mockMvc.perform(post(TOKENIZATION_URL)
            .header(API_KEY_HEADER, tokenizationApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void createToken_WhenRejected_ShouldReturnUnprocessableEntity() throws Exception {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    when(tokenizationService.createToken(any(TokenizationRequest.class)))
        .thenThrow(new TokenizationRejectedException("Tokenization request was rejected"));

    mockMvc.perform(post(TOKENIZATION_URL)
            .header(API_KEY_HEADER, tokenizationApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("Tokenization Rejected"));
  }

}

