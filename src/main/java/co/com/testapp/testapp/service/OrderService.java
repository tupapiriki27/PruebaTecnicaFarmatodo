package co.com.testapp.testapp.service;

import co.com.testapp.testapp.dto.AddToCartRequest;
import co.com.testapp.testapp.dto.OrderItemResponse;
import co.com.testapp.testapp.dto.OrderResponse;
import co.com.testapp.testapp.entity.Customer;
import co.com.testapp.testapp.entity.Order;
import co.com.testapp.testapp.entity.OrderItem;
import co.com.testapp.testapp.entity.OrderStatus;
import co.com.testapp.testapp.entity.Product;
import co.com.testapp.testapp.exception.CustomerNotFoundException;
import co.com.testapp.testapp.exception.InsufficientStockException;
import co.com.testapp.testapp.exception.OrderNotFoundException;
import co.com.testapp.testapp.exception.ProductNotFoundException;
import co.com.testapp.testapp.repository.CustomerRepository;
import co.com.testapp.testapp.repository.OrderItemRepository;
import co.com.testapp.testapp.repository.OrderRepository;
import co.com.testapp.testapp.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final OrderItemRepository orderItemRepository;

  public OrderService(OrderRepository orderRepository,
                      ProductRepository productRepository,
                      CustomerRepository customerRepository,
                      OrderItemRepository orderItemRepository) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.customerRepository = customerRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public OrderResponse addToCart(Long customerId, AddToCartRequest request) {
    log.info("Adding product {} to cart for customer {}", request.getProductId(), customerId);

    // Validate customer
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(
            String.format("Customer with ID %d not found", customerId)
        ));

    // Validate product
    Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
        .orElseThrow(() -> new ProductNotFoundException(
            String.format("Product with ID %d not found or is inactive", request.getProductId())
        ));

    // Validate stock
    if (product.getStock() < request.getQuantity()) {
      throw new InsufficientStockException(
          String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
              product.getName(), product.getStock(), request.getQuantity())
      );
    }

    // Get or create active cart
    Order cart = orderRepository.findActiveCartByCustomerId(customerId)
        .orElseGet(() -> createNewCart(customer));

    // Check if product already exists in cart
    OrderItem existingItem = orderItemRepository.findByOrderIdAndProductId(cart.getId(), product.getId())
        .orElse(null);

    if (existingItem != null) {
      // Update existing item
      int newQuantity = existingItem.getQuantity() + request.getQuantity();

      if (product.getStock() < newQuantity) {
        throw new InsufficientStockException(
            String.format("Insufficient stock for product '%s'. Available: %d, Requested total: %d",
                product.getName(), product.getStock(), newQuantity)
        );
      }

      existingItem.setQuantity(newQuantity);
      existingItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
      log.info("Updated existing cart item. New quantity: {}", newQuantity);
    } else {
      // Add new item
      OrderItem newItem = OrderItem.builder()
          .order(cart)
          .product(product)
          .quantity(request.getQuantity())
          .unitPrice(product.getPrice())
          .subtotal(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
          .build();

      cart.addItem(newItem);
      log.info("Added new item to cart");
    }

    // Update cart total
    updateCartTotal(cart);
    cart.setUpdatedAt(LocalDateTime.now());

    Order savedCart = orderRepository.save(cart);

    log.info("Cart updated successfully for customer {}", customerId);

    return mapToResponse(savedCart);
  }

  @Transactional(readOnly = true)
  public OrderResponse getCart(Long customerId) {
    log.info("Fetching cart for customer {}", customerId);

    Order cart = orderRepository.findActiveCartByCustomerId(customerId)
        .orElseThrow(() -> new OrderNotFoundException(
            String.format("No active cart found for customer %d", customerId)
        ));

    return mapToResponse(cart);
  }

  private Order createNewCart(Customer customer) {
    log.info("Creating new cart for customer {}", customer.getId());

    return Order.builder()
        .customer(customer)
        .status(OrderStatus.CART)
        .totalAmount(BigDecimal.ZERO)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private void updateCartTotal(Order cart) {
    BigDecimal total = cart.getItems().stream()
        .map(OrderItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(total);
    log.debug("Cart total updated to: {}", total);
  }

  private OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomer().getId())
        .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
        .items(order.getItems().stream()
            .map(this::mapItemToResponse)
            .collect(Collectors.toList()))
        .totalAmount(order.getTotalAmount())
        .status(order.getStatus().name())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();
  }

  private OrderItemResponse mapItemToResponse(OrderItem item) {
    return OrderItemResponse.builder()
        .id(item.getId())
        .productId(item.getProduct().getId())
        .productName(item.getProduct().getName())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .subtotal(item.getSubtotal())
        .build();
  }

}

