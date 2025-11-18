package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.TokenizationRequest;
import co.com.testapp.testapp.dto.TokenizationResponse;
import co.com.testapp.testapp.entity.CardToken;
import co.com.testapp.testapp.exception.InvalidCardDataException;
import co.com.testapp.testapp.exception.TokenizationRejectedException;
import co.com.testapp.testapp.repository.CardTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenizationServiceTest {

  private static final String VALID_CARD_NUMBER = "4111111111111111";
  private static final String VALID_CVV = "123";
  private static final String VALID_EXPIRATION = "12/25";
  private static final String VALID_CARDHOLDER = "John Doe";

  @Mock
  private CardTokenRepository cardTokenRepository;

  @InjectMocks
  private TokenizationService tokenizationService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(tokenizationService, "rejectionProbability", 0.0);
  }

  @Test
  void createToken_WithValidData_ShouldReturnTokenizationResponse() {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    when(cardTokenRepository.existsByToken(anyString())).thenReturn(false);
    when(cardTokenRepository.save(any(CardToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TokenizationResponse response = tokenizationService.createToken(request);

    assertNotNull(response);
    assertNotNull(response.getToken());
    assertTrue(response.getToken().startsWith("tok_"));
    assertEquals("1111", response.getLastFourDigits());
    assertEquals("VISA", response.getCardBrand());
    assertEquals(VALID_EXPIRATION, response.getExpirationDate());
    assertTrue(response.getActive());

    verify(cardTokenRepository, times(1)).save(any(CardToken.class));
  }

  @Test
  void createToken_WithExpiredCard_ShouldThrowInvalidCardDataException() {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate("01/20")
        .cardholderName(VALID_CARDHOLDER)
        .build();

    assertThrows(InvalidCardDataException.class, () -> tokenizationService.createToken(request));
  }

  @Test
  void createToken_WithRejectionProbability_ShouldThrowTokenizationRejectedException() {
    ReflectionTestUtils.setField(tokenizationService, "rejectionProbability", 1.0);

    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber(VALID_CARD_NUMBER)
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    assertThrows(TokenizationRejectedException.class, () -> tokenizationService.createToken(request));
  }

  @Test
  void createToken_WithMastercardNumber_ShouldDetectMastercard() {
    TokenizationRequest request = TokenizationRequest.builder()
        .cardNumber("5500000000000004")
        .cvv(VALID_CVV)
        .expirationDate(VALID_EXPIRATION)
        .cardholderName(VALID_CARDHOLDER)
        .build();

    when(cardTokenRepository.existsByToken(anyString())).thenReturn(false);
    when(cardTokenRepository.save(any(CardToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TokenizationResponse response = tokenizationService.createToken(request);

    assertEquals("MASTERCARD", response.getCardBrand());
    assertEquals("0004", response.getLastFourDigits());
  }

}

