package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.AddToCartRequest;
import co.com.testapp.testapp.dto.OrderResponse;
import co.com.testapp.testapp.entity.Customer;
import co.com.testapp.testapp.entity.Order;
import co.com.testapp.testapp.entity.Product;
import co.com.testapp.testapp.exception.InsufficientStockException;
import co.com.testapp.testapp.exception.ProductNotFoundException;
import co.com.testapp.testapp.repository.CustomerRepository;
import co.com.testapp.testapp.repository.OrderItemRepository;
import co.com.testapp.testapp.repository.OrderRepository;
import co.com.testapp.testapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private OrderItemRepository orderItemRepository;

  @InjectMocks
  private OrderService orderService;

  @Test
  void addToCart_WithValidData_ShouldCreateCart() {
    Long customerId = 1L;
    Long productId = 1L;

    Customer customer = Customer.builder()
        .id(customerId)
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .build();

    Product product = Product.builder()
        .id(productId)
        .name("Test Product")
        .price(new BigDecimal("99.99"))
        .stock(100)
        .active(true)
        .build();

    AddToCartRequest request = AddToCartRequest.builder()
        .productId(productId)
        .quantity(2)
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));
    when(orderRepository.findActiveCartByCustomerId(customerId)).thenReturn(Optional.empty());
    when(orderItemRepository.findByOrderIdAndProductId(any(), eq(productId))).thenReturn(Optional.empty());
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      order.setId(1L);
      return order;
    });

    OrderResponse response = orderService.addToCart(customerId, request);

    assertNotNull(response);
    assertEquals(1, response.getItems().size());
    assertEquals(new BigDecimal("199.98"), response.getTotalAmount());
  }

  @Test
  void addToCart_WithInsufficientStock_ShouldThrowException() {
    Long customerId = 1L;
    Long productId = 1L;

    Customer customer = Customer.builder().id(customerId).firstName("John").lastName("Doe").build();
    Product product = Product.builder()
        .id(productId)
        .name("Test Product")
        .price(new BigDecimal("99.99"))
        .stock(5)
        .active(true)
        .build();

    AddToCartRequest request = AddToCartRequest.builder()
        .productId(productId)
        .quantity(10)
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));

    assertThrows(InsufficientStockException.class, () -> orderService.addToCart(customerId, request));
  }

  @Test
  void addToCart_WithInvalidProduct_ShouldThrowException() {
    Long customerId = 1L;
    Long productId = 999L;

    Customer customer = Customer.builder().id(customerId).build();
    AddToCartRequest request = AddToCartRequest.builder()
        .productId(productId)
        .quantity(1)
        .build();

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> orderService.addToCart(customerId, request));
  }

}

