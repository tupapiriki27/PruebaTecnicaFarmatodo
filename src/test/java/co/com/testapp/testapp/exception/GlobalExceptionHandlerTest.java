package co.com.testapp.testapp.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Tests para el GlobalExceptionHandler.
 *
 * Cubre todos los handlers de excepciones incluyendo:
 * - TokenizationRejectedException
 * - InvalidCardDataException
 * - DuplicateCustomerException
 * - CustomerNotFoundException
 * - ProductNotFoundException
 * - OrderNotFoundException
 * - InsufficientStockException
 * - PaymentFailedException
 * - AccessDeniedException
 * - MethodArgumentNotValidException
 * - Exception genérica
 *
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private HttpServletRequest mockRequest;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRequestURI()).thenReturn("/api/v1/test");
  }

  /**
   * Test: Manejar TokenizationRejectedException.
   */
  @Test
  void handleTokenizationRejected_ShouldReturnUnprocessableEntity() {
    TokenizationRejectedException ex = new TokenizationRejectedException("Tokenization rejected");

    ResponseEntity<ErrorResponse> response = handler.handleTokenizationRejected(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getBody().getStatus());
    assertEquals("Tokenization Rejected", response.getBody().getError());
    assertEquals("Tokenization rejected", response.getBody().getMessage());
    assertEquals("/api/v1/test", response.getBody().getPath());
  }

  /**
   * Test: Manejar InvalidCardDataException.
   */
  @Test
  void handleInvalidCardData_ShouldReturnBadRequest() {
    InvalidCardDataException ex = new InvalidCardDataException("Invalid card number");

    ResponseEntity<ErrorResponse> response = handler.handleInvalidCardData(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    assertEquals("Invalid Card Data", response.getBody().getError());
    assertEquals("Invalid card number", response.getBody().getMessage());
  }

  /**
   * Test: Manejar MethodArgumentNotValidException con múltiples errores.
   */
  @Test
  void handleValidationErrors_WithMultipleErrors_ShouldReturnBadRequest() {
    MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "test");
    bindingResult.addError(new FieldError("object", "email", "Email is required"));
    bindingResult.addError(new FieldError("object", "name", "Name must not be blank"));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Validation Failed", response.getBody().getError());
    assertEquals("Invalid request parameters", response.getBody().getMessage());
    assertNotNull(response.getBody().getDetails());
    assertEquals(2, response.getBody().getDetails().size());
    assertTrue(response.getBody().getDetails().contains("email: Email is required"));
    assertTrue(response.getBody().getDetails().contains("name: Name must not be blank"));
  }

  /**
   * Test: Manejar DuplicateCustomerException.
   */
  @Test
  void handleDuplicateCustomer_ShouldReturnConflict() {
    DuplicateCustomerException ex = new DuplicateCustomerException("Email already exists");

    ResponseEntity<ErrorResponse> response = handler.handleDuplicateCustomer(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
    assertEquals("Duplicate Customer", response.getBody().getError());
    assertEquals("Email already exists", response.getBody().getMessage());
  }

  /**
   * Test: Manejar CustomerNotFoundException.
   */
  @Test
  void handleCustomerNotFound_ShouldReturnNotFound() {
    CustomerNotFoundException ex = new CustomerNotFoundException("Customer with ID 1 not found");

    ResponseEntity<ErrorResponse> response = handler.handleCustomerNotFound(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    assertEquals("Customer Not Found", response.getBody().getError());
    assertEquals("Customer with ID 1 not found", response.getBody().getMessage());
  }

  /**
   * Test: Manejar ProductNotFoundException.
   */
  @Test
  void handleProductNotFound_ShouldReturnNotFound() {
    ProductNotFoundException ex = new ProductNotFoundException("Product with ID 5 not found");

    ResponseEntity<ErrorResponse> response = handler.handleProductNotFound(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    assertEquals("Product Not Found", response.getBody().getError());
    assertEquals("Product with ID 5 not found", response.getBody().getMessage());
  }

  /**
   * Test: Manejar OrderNotFoundException.
   */
  @Test
  void handleOrderNotFound_ShouldReturnNotFound() {
    OrderNotFoundException ex = new OrderNotFoundException("Order with ID 123 not found");

    ResponseEntity<ErrorResponse> response = handler.handleOrderNotFound(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    assertEquals("Order Not Found", response.getBody().getError());
    assertEquals("Order with ID 123 not found", response.getBody().getMessage());
  }

  /**
   * Test: Manejar InsufficientStockException.
   */
  @Test
  void handleInsufficientStock_ShouldReturnBadRequest() {
    InsufficientStockException ex = new InsufficientStockException("Not enough stock available");

    ResponseEntity<ErrorResponse> response = handler.handleInsufficientStock(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    assertEquals("Insufficient Stock", response.getBody().getError());
    assertEquals("Not enough stock available", response.getBody().getMessage());
  }

  /**
   * Test: Manejar PaymentFailedException.
   */
  @Test
  void handlePaymentFailed_ShouldReturnPaymentRequired() {
    PaymentFailedException ex = new PaymentFailedException("Payment was rejected");

    ResponseEntity<ErrorResponse> response = handler.handlePaymentFailed(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.PAYMENT_REQUIRED.value(), response.getBody().getStatus());
    assertEquals("Payment Failed", response.getBody().getError());
    assertEquals("Payment was rejected", response.getBody().getMessage());
  }

  /**
   * Test: Manejar AccessDeniedException.
   */
  @Test
  void handleAccessDenied_ShouldReturnForbidden() {
    AccessDeniedException ex = new AccessDeniedException("Access denied");

    ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
    assertEquals("Access Denied", response.getBody().getError());
    assertEquals("Invalid or missing API key", response.getBody().getMessage());
    assertEquals("/api/v1/test", response.getBody().getPath());
  }

  /**
   * Test: Manejar excepción genérica.
   */
  @Test
  void handleGenericException_ShouldReturnInternalServerError() {
    Exception ex = new RuntimeException("Unexpected error");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
    assertEquals("Internal Server Error", response.getBody().getError());
    assertEquals("An unexpected error occurred", response.getBody().getMessage());
    assertEquals("/api/v1/test", response.getBody().getPath());
  }

  /**
   * Test: Manejar InvalidCardDataException con diferentes mensajes.
   */
  @Test
  void handleInvalidCardData_WithCVVError_ShouldReturnBadRequest() {
    InvalidCardDataException ex = new InvalidCardDataException("Invalid CVV");

    ResponseEntity<ErrorResponse> response = handler.handleInvalidCardData(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid CVV", response.getBody().getMessage());
  }

  /**
   * Test: Manejar TokenizationRejectedException con timestamp válido.
   */
  @Test
  void handleTokenizationRejected_ShouldIncludeValidTimestamp() {
    TokenizationRejectedException ex = new TokenizationRejectedException("Rejection");
    LocalDateTime beforeCall = LocalDateTime.now();

    ResponseEntity<ErrorResponse> response = handler.handleTokenizationRejected(ex, mockRequest);

    LocalDateTime afterCall = LocalDateTime.now();
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getTimestamp());
    assertTrue(response.getBody().getTimestamp().isAfter(beforeCall.minusSeconds(1)));
    assertTrue(response.getBody().getTimestamp().isBefore(afterCall.plusSeconds(1)));
  }

  /**
   * Test: Manejar MethodArgumentNotValidException sin errores.
   */
  @Test
  void handleValidationErrors_WithNoErrors_ShouldReturnBadRequest() {
    MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "test");

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody().getDetails());
    assertEquals(0, response.getBody().getDetails().size());
  }

  /**
   * Test: Manejar CustomerNotFoundException verifica que la ruta es correcta.
   */
  @Test
  void handleCustomerNotFound_ShouldIncludeCorrectPath() {
    when(mockRequest.getRequestURI()).thenReturn("/api/v1/customers/1");
    CustomerNotFoundException ex = new CustomerNotFoundException("Not found");

    ResponseEntity<ErrorResponse> response = handler.handleCustomerNotFound(ex, mockRequest);

    assertNotNull(response.getBody());
    assertEquals("/api/v1/customers/1", response.getBody().getPath());
  }

  /**
   * Test: Manejar DuplicateCustomerException con mensaje de email.
   */
  @Test
  void handleDuplicateCustomer_WithEmailDuplication_ShouldReturnConflict() {
    DuplicateCustomerException ex = new DuplicateCustomerException("Email john@example.com already registered");

    ResponseEntity<ErrorResponse> response = handler.handleDuplicateCustomer(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Email john@example.com already registered", response.getBody().getMessage());
  }

  /**
   * Test: Manejar InsufficientStockException con cantidad detallada.
   */
  @Test
  void handleInsufficientStock_WithDetailedMessage_ShouldReturnBadRequest() {
    InsufficientStockException ex = new InsufficientStockException(
        "Product ID 5 requires 10 units but only 3 available");

    ResponseEntity<ErrorResponse> response = handler.handleInsufficientStock(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Product ID 5 requires 10 units but only 3 available", 
        response.getBody().getMessage());
  }

  /**
   * Test: Manejar PaymentFailedException verifica que el status sea correcto.
   */
  @Test
  void handlePaymentFailed_ShouldReturnCorrectHttpStatus() {
    PaymentFailedException ex = new PaymentFailedException("Insufficient funds");

    ResponseEntity<ErrorResponse> response = handler.handlePaymentFailed(ex, mockRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.PAYMENT_REQUIRED.value(), response.getBody().getStatus());
    assertEquals(402, response.getBody().getStatus());
  }

  /**
   * Test: Manejar excepción genérica verifica que no incluye detalles.
   */
  @Test
  void handleGenericException_ShouldNotIncludeDetails() {
    Exception ex = new IllegalArgumentException("Some error");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, mockRequest);

    assertNotNull(response.getBody());
    assertTrue(response.getBody().getDetails() == null || response.getBody().getDetails().isEmpty());
  }

}

