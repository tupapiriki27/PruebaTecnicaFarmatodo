package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.TokenizationRequest;
import co.com.testapp.testapp.dto.TokenizationResponse;
import co.com.testapp.testapp.service.TokenizationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tokenization")
@Slf4j
public class TokenizationController {

  private final TokenizationService tokenizationService;

  public TokenizationController(TokenizationService tokenizationService) {
    this.tokenizationService = tokenizationService;
  }

  @PostMapping("/tokens")
  public ResponseEntity<TokenizationResponse> createToken(@Valid @RequestBody TokenizationRequest request) {
    log.info("Received tokenization request for cardholder: {}", request.getCardholderName());
    TokenizationResponse response = tokenizationService.createToken(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}

