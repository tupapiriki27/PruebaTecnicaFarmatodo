package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.AddToCartRequest;
import co.com.testapp.testapp.dto.OrderResponse;
import co.com.testapp.testapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/cart/{customerId}")
  public ResponseEntity<OrderResponse> addToCart(
      @PathVariable Long customerId,
      @Valid @RequestBody AddToCartRequest request) {
    log.info("Received request to add product to cart for customer: {}", customerId);
    OrderResponse response = orderService.addToCart(customerId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/cart/{customerId}")
  public ResponseEntity<OrderResponse> getCart(@PathVariable Long customerId) {
    log.info("Received request to get cart for customer: {}", customerId);
    OrderResponse response = orderService.getCart(customerId);
    return ResponseEntity.ok(response);
  }

}

