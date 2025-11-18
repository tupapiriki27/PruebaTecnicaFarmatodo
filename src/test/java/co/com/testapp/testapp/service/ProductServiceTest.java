package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.ProductRequest;
import co.com.testapp.testapp.dto.ProductResponse;
import co.com.testapp.testapp.entity.Product;
import co.com.testapp.testapp.exception.ProductNotFoundException;
import co.com.testapp.testapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  private static final String PRODUCT_NAME = "Test Product";
  private static final BigDecimal PRODUCT_PRICE = new BigDecimal("99.99");
  private static final Integer PRODUCT_STOCK = 100;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  void createProduct_WithValidData_ShouldReturnProductResponse() {
    ProductRequest request = ProductRequest.builder()
        .name(PRODUCT_NAME)
        .description("Test Description")
        .price(PRODUCT_PRICE)
        .stock(PRODUCT_STOCK)
        .category("Electronics")
        .sku("TEST-001")
        .build();

    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
      Product product = invocation.getArgument(0);
      product.setId(1L);
      return product;
    });

    ProductResponse response = productService.createProduct(request);

    assertNotNull(response);
    assertNotNull(response.getId());
    assertEquals(PRODUCT_NAME, response.getName());
    assertEquals(PRODUCT_PRICE, response.getPrice());
    assertEquals(PRODUCT_STOCK, response.getStock());
  }

  @Test
  void getProductById_WhenProductExists_ShouldReturnProduct() {
    Long productId = 1L;
    Product product = Product.builder()
        .id(productId)
        .name(PRODUCT_NAME)
        .price(PRODUCT_PRICE)
        .stock(PRODUCT_STOCK)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));

    ProductResponse response = productService.getProductById(productId);

    assertNotNull(response);
    assertEquals(productId, response.getId());
    assertEquals(PRODUCT_NAME, response.getName());
  }

  @Test
  void getProductById_WhenProductNotFound_ShouldThrowException() {
    Long productId = 999L;

    when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
  }

}

