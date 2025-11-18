package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.ProductRequest;
import co.com.testapp.testapp.dto.ProductResponse;
import co.com.testapp.testapp.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
    log.info("Received request to create product: {}", request.getName());
    ProductResponse response = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    log.info("Received request to get all products");
    List<ProductResponse> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
    log.info("Received request to get product with ID: {}", id);
    ProductResponse product = productService.getProductById(id);
    return ResponseEntity.ok(product);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id,
      @Valid @RequestBody ProductRequest request) {
    log.info("Received request to update product with ID: {}", id);
    ProductResponse response = productService.updateProduct(id, request);
    return ResponseEntity.ok(response);
  }

}

