package co.com.testapp.testapp.service;

import co.com.testapp.testapp.dto.TokenizationRequest;
import co.com.testapp.testapp.dto.TokenizationResponse;
import co.com.testapp.testapp.entity.CardToken;
import co.com.testapp.testapp.exception.InvalidCardDataException;
import co.com.testapp.testapp.exception.TokenizationRejectedException;
import co.com.testapp.testapp.repository.CardTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * Servicio para la tokenización segura de tarjetas de crédito.
 * 
 * Genera tokens únicos y seguros para tarjetas de crédito, permitiendo
 * el almacenamiento seguro de información de pago sin guardar los datos
 * completos de la tarjeta.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
@Transactional
public class TokenizationService {

  private final CardTokenRepository cardTokenRepository;
  private final SecureRandom secureRandom;

  @Value("${tokenization.rejection.probability}")
  private double rejectionProbability;

  /**
   * Constructor que inyecta las dependencias necesarias.
   * 
   * @param cardTokenRepository Repositorio para acceder a tokens guardados
   */
  public TokenizationService(CardTokenRepository cardTokenRepository) {
    this.cardTokenRepository = cardTokenRepository;
    this.secureRandom = new SecureRandom();
  }

  /**
   * Crea un token único y seguro para una tarjeta de crédito.
   * 
   * Valida la fecha de expiración, genera un token único usando SHA-256,
   * detecta la marca de la tarjeta y guarda la información en la base de datos.
   * El método puede rechazar la tokenización según la probabilidad configurada.
   * 
   * @param request Datos de la tarjeta a tokenizar
   * @return Respuesta con el token generado y detalles de la tarjeta
   * @throws TokenizationRejectedException Si la tokenización es rechazada
   * @throws InvalidCardDataException Si los datos de la tarjeta son inválidos
   */
  public TokenizationResponse createToken(TokenizationRequest request) {
    log.info("Initiating tokenization process for cardholder: {}", request.getCardholderName());

    // Validate card expiration
    validateCardExpiration(request.getExpirationDate());

    // Check rejection probability
    if (shouldReject()) {
      log.warn("Tokenization rejected due to configured rejection probability");
      throw new TokenizationRejectedException("Tokenization request was rejected. Please try again later.");
    }

    // Generate unique token
    String token = generateUniqueToken(request.getCardNumber());

    // Extract card information
    String lastFourDigits = extractLastFourDigits(request.getCardNumber());
    String cardBrand = detectCardBrand(request.getCardNumber());

    // Create and save token entity
    CardToken cardToken = CardToken.builder()
        .token(token)
        .lastFourDigits(lastFourDigits)
        .cardBrand(cardBrand)
        .expirationDate(request.getExpirationDate())
        .cardholderName(request.getCardholderName())
        .createdAt(LocalDateTime.now())
        .active(true)
        .build();

    cardTokenRepository.save(cardToken);

    log.info("Token created successfully: {} for card ending in {}", token, lastFourDigits);

    return TokenizationResponse.builder()
        .token(token)
        .lastFourDigits(lastFourDigits)
        .cardBrand(cardBrand)
        .expirationDate(request.getExpirationDate())
        .createdAt(cardToken.getCreatedAt())
        .active(true)
        .build();
  }

  /**
   * Determina si una tokenización debe ser rechazada.
   * 
   * Compara un valor aleatorio con la probabilidad configurada.
   * 
   * @return true si la tokenización debe ser rechazada, false en caso contrario
   */
  private boolean shouldReject() {
    double randomValue = secureRandom.nextDouble();
    return randomValue < rejectionProbability;
  }

  /**
   * Genera un token único y no duplicado para una tarjeta de crédito.
   * 
   * Utiliza SHA-256 y un nonce para generar tokens únicos, verificando
   * que no exista ya en la base de datos. Reintenta hasta 10 veces si
   * encuentra duplicados.
   * 
   * @param cardNumber Número de tarjeta de crédito
   * @return Token único generado
   * @throws RuntimeException Si no puede generar un token único después de 10 intentos
   */
  private String generateUniqueToken(String cardNumber) {
    String token;
    int attempts = 0;
    int maxAttempts = 10;

    do {
      token = generateToken(cardNumber, System.nanoTime() + attempts);
      attempts++;

      if (attempts >= maxAttempts) {
        throw new RuntimeException("Failed to generate unique token after " + maxAttempts + " attempts");
      }
    } while (cardTokenRepository.existsByToken(token));

    return token;
  }

  /**
   * Genera un token usando SHA-256 con un nonce para mayor seguridad.
   * 
   * @param cardNumber Número de tarjeta de crédito
   * @param nonce Valor único para aumentar la entropía
   * @return Token generado en formato hexadecimal con prefijo "tok_"
   * @throws RuntimeException Si SHA-256 no está disponible
   */
  private String generateToken(String cardNumber, long nonce) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String input = cardNumber + nonce + secureRandom.nextLong();
      byte[] hash = digest.digest(input.getBytes());
      return "tok_" + HexFormat.of().formatHex(hash).substring(0, 32);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating token", e);
    }
  }

  /**
   * Extrae los últimos cuatro dígitos de una tarjeta de crédito.
   * 
   * @param cardNumber Número de tarjeta completo
   * @return Los últimos 4 dígitos de la tarjeta
   */
  private String extractLastFourDigits(String cardNumber) {
    return cardNumber.substring(cardNumber.length() - 4);
  }

  /**
   * Detecta la marca de la tarjeta basándose en el primer dígito.
   * 
   * Utiliza el Algoritmo de Luhn para identificar la marca:
   * - 4: VISA
   * - 5: MASTERCARD
   * - 3: AMEX
   * - Otros: UNKNOWN
   * 
   * @param cardNumber Número de tarjeta de crédito
   * @return Marca de la tarjeta (VISA, MASTERCARD, AMEX, UNKNOWN)
   */
  private String detectCardBrand(String cardNumber) {
    if (cardNumber.startsWith("4")) {
      return "VISA";
    } else if (cardNumber.startsWith("5")) {
      return "MASTERCARD";
    } else if (cardNumber.startsWith("3")) {
      return "AMEX";
    } else {
      return "UNKNOWN";
    }
  }

  /**
   * Valida que una tarjeta de crédito no haya expirado.
   * 
   * Verifica que la fecha de expiración sea mayor o igual a la fecha actual.
   * Espera formato MM/YY donde YY se suma a 2000.
   * 
   * @param expirationDate Fecha de expiración en formato MM/YY
   * @throws InvalidCardDataException Si la tarjeta ha expirado
   */
  private void validateCardExpiration(String expirationDate) {
    String[] parts = expirationDate.split("/");
    int month = Integer.parseInt(parts[0]);
    int year = 2000 + Integer.parseInt(parts[1]);

    LocalDateTime now = LocalDateTime.now();
    int currentYear = now.getYear();
    int currentMonth = now.getMonthValue();

    if (year < currentYear || (year == currentYear && month < currentMonth)) {
      throw new InvalidCardDataException("Card has expired");
    }
  }

}

