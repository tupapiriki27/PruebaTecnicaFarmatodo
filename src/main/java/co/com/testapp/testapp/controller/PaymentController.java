package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.CheckoutRequest;
import co.com.testapp.testapp.dto.CheckoutResponse;
import co.com.testapp.testapp.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping("/checkout")
  public ResponseEntity<CheckoutResponse> processCheckout(
      @Valid @RequestBody CheckoutRequest request) {
    log.info("Received checkout request for customer: {}", request.getCustomerId());
    CheckoutResponse response = paymentService.processCheckout(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/checkout/{customerId}/{orderId}")
  public ResponseEntity<CheckoutResponse> getCheckoutStatus(
      @PathVariable Long customerId,
      @PathVariable Long orderId) {
    log.info("Received status check request for customer: {}, order: {}", customerId, orderId);
    CheckoutResponse response = paymentService.getCheckoutStatus(customerId, orderId);
    return ResponseEntity.ok(response);
  }

}

