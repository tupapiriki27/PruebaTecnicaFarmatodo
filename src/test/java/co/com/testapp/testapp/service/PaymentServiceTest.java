package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.CheckoutRequest;
import co.com.testapp.testapp.dto.CheckoutResponse;
import co.com.testapp.testapp.entity.Customer;
import co.com.testapp.testapp.entity.Order;
import co.com.testapp.testapp.entity.OrderItem;
import co.com.testapp.testapp.entity.OrderStatus;
import co.com.testapp.testapp.entity.Payment;
import co.com.testapp.testapp.entity.Product;
import co.com.testapp.testapp.exception.OrderNotFoundException;
import co.com.testapp.testapp.exception.PaymentFailedException;
import co.com.testapp.testapp.repository.CustomerRepository;
import co.com.testapp.testapp.repository.OrderRepository;
import co.com.testapp.testapp.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    // Set approval probability to 100% for successful payment tests
    ReflectionTestUtils.setField(paymentService, "approvalProbability", 1.0);
    ReflectionTestUtils.setField(paymentService, "maxRetryAttempts", 3);
    ReflectionTestUtils.setField(paymentService, "retryDelayMillis", 0L);
  }

  @Test
  void processCheckout_WithValidData_ShouldApprovePayment() {
    Long customerId = 1L;
    Long orderId = 1L;

    Customer customer = Customer.builder()
        .id(customerId)
        .firstName("John")
        .lastName("Doe")
        .email("john@example.com")
        .build();

    Product product = Product.builder()
        .id(1L)
        .name("Test Product")
        .price(new BigDecimal("99.99"))
        .build();

    OrderItem item = OrderItem.builder()
        .id(1L)
        .product(product)
        .quantity(1)
        .unitPrice(new BigDecimal("99.99"))
        .subtotal(new BigDecimal("99.99"))
        .build();

    Order cart = Order.builder()
        .id(orderId)
        .customer(customer)
        .items(new ArrayList<>(List.of(item)))
        .totalAmount(new BigDecimal("99.99"))
        .status(OrderStatus.CART)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    CheckoutRequest request = CheckoutRequest.builder()
        .customerId(customerId)
        .tokenizedCard("tok_test")
        .shippingAddress("123 Main St")
        .shippingCity("NYC")
        .shippingState("NY")
        .shippingZipCode("10001")
        .shippingCountry("USA")
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(orderRepository.findActiveCartByCustomerId(customerId)).thenReturn(Optional.of(cart));
    when(orderRepository.save(any(Order.class))).thenReturn(cart);
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
      Payment payment = invocation.getArgument(0);
      payment.setId(1L);
      return payment;
    });
    when(paymentRepository.findById(any())).thenReturn(Optional.empty());

    CheckoutResponse response = paymentService.processCheckout(request);

    assertNotNull(response);
    assertEquals(customerId, response.getCustomerId());
    assertEquals(new BigDecimal("99.99"), response.getTotalAmount());

    verify(emailService, times(1)).sendPaymentApprovedNotification(anyString(), anyString(), anyString(), anyString());
  }

  @Test
  void processCheckout_WithEmptyCart_ShouldThrowException() {
    Long customerId = 1L;

    Customer customer = Customer.builder()
        .id(customerId)
        .firstName("John")
        .lastName("Doe")
        .build();

    Order emptyCart = Order.builder()
        .id(1L)
        .customer(customer)
        .items(new ArrayList<>())
        .status(OrderStatus.CART)
        .build();

    CheckoutRequest request = CheckoutRequest.builder()
        .customerId(customerId)
        .tokenizedCard("tok_test")
        .shippingAddress("123 Main St")
        .shippingCity("NYC")
        .shippingState("NY")
        .shippingZipCode("10001")
        .shippingCountry("USA")
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(orderRepository.findActiveCartByCustomerId(customerId)).thenReturn(Optional.of(emptyCart));

    assertThrows(OrderNotFoundException.class, () -> paymentService.processCheckout(request));
  }

  @Test
  void processCheckout_WithPaymentRejection_ShouldNotifyCustomer() {
    // Set approval probability to 0% for all rejections
    ReflectionTestUtils.setField(paymentService, "approvalProbability", 0.0);

    Long customerId = 1L;
    Long orderId = 1L;

    Customer customer = Customer.builder()
        .id(customerId)
        .firstName("John")
        .lastName("Doe")
        .email("john@example.com")
        .build();

    Product product = Product.builder()
        .id(1L)
        .name("Test Product")
        .price(new BigDecimal("99.99"))
        .build();

    OrderItem item = OrderItem.builder()
        .product(product)
        .quantity(1)
        .unitPrice(new BigDecimal("99.99"))
        .subtotal(new BigDecimal("99.99"))
        .build();

    Order cart = Order.builder()
        .id(orderId)
        .customer(customer)
        .items(new ArrayList<>(List.of(item)))
        .totalAmount(new BigDecimal("99.99"))
        .status(OrderStatus.CART)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    CheckoutRequest request = CheckoutRequest.builder()
        .customerId(customerId)
        .tokenizedCard("tok_test")
        .shippingAddress("123 Main St")
        .shippingCity("NYC")
        .shippingState("NY")
        .shippingZipCode("10001")
        .shippingCountry("USA")
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(orderRepository.findActiveCartByCustomerId(customerId)).thenReturn(Optional.of(cart));
    when(orderRepository.save(any(Order.class))).thenReturn(cart);
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
      Payment payment = invocation.getArgument(0);
      payment.setId(1L);
      return payment;
    });

    assertThrows(PaymentFailedException.class, () -> paymentService.processCheckout(request));

    verify(emailService, times(1)).sendPaymentFailureNotification(anyString(), anyString(), anyString(), anyString());
  }

}

