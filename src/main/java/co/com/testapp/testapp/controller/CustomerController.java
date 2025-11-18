package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.CustomerRegistrationRequest;
import co.com.testapp.testapp.dto.CustomerResponse;
import co.com.testapp.testapp.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> registerCustomer(
      @Valid @RequestBody CustomerRegistrationRequest request) {
    log.info("Received customer registration request for email: {}", request.getEmail());
    CustomerResponse response = customerService.registerCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}

