package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import java.util.Arrays;
import java.util.List;
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

  /**
   * Test: Obtener todos los productos cuando existen productos activos.
   */
  @Test
  void getAllProducts_WhenProductsExist_ShouldReturnProductList() {
    // Setup
    LocalDateTime now = LocalDateTime.now();
    Product product1 = Product.builder()
        .id(1L)
        .name("Product 1")
        .description("Description 1")
        .price(new BigDecimal("99.99"))
        .stock(100)
        .category("Electronics")
        .sku("SKU-001")
        .active(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    Product product2 = Product.builder()
        .id(2L)
        .name("Product 2")
        .description("Description 2")
        .price(new BigDecimal("149.99"))
        .stock(50)
        .category("Books")
        .sku("SKU-002")
        .active(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product1, product2));

    // Execute
    List<ProductResponse> responses = productService.getAllProducts();

    // Verify
    assertNotNull(responses);
    assertEquals(2, responses.size());
    assertEquals("Product 1", responses.get(0).getName());
    assertEquals("Product 2", responses.get(1).getName());
    verify(productRepository, times(1)).findByActiveTrue();
  }

  /**
   * Test: Obtener todos los productos cuando no hay productos.
   */
  @Test
  void getAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() {
    when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList());

    List<ProductResponse> responses = productService.getAllProducts();

    assertNotNull(responses);
    assertEquals(0, responses.size());
    verify(productRepository, times(1)).findByActiveTrue();
  }

  /**
   * Test: Obtener todos los productos con un solo producto.
   */
  @Test
  void getAllProducts_WithSingleProduct_ShouldReturnListWithOneProduct() {
    LocalDateTime now = LocalDateTime.now();
    Product product = Product.builder()
        .id(1L)
        .name("Single Product")
        .description("Only product")
        .price(new BigDecimal("79.99"))
        .stock(10)
        .category("Clothing")
        .sku("SKU-003")
        .active(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product));

    List<ProductResponse> responses = productService.getAllProducts();

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals("Single Product", responses.get(0).getName());
    assertEquals(new BigDecimal("79.99"), responses.get(0).getPrice());
  }

  /**
   * Test: Obtener todos los productos verifica que todos tengan ID.
   */
  @Test
  void getAllProducts_ShouldMapAllFieldsCorrectly() {
    LocalDateTime now = LocalDateTime.now();
    Product product = Product.builder()
        .id(5L)
        .name("Complete Product")
        .description("Full description")
        .price(new BigDecimal("199.99"))
        .stock(25)
        .category("Premium")
        .sku("SKU-PREM-001")
        .active(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product));

    List<ProductResponse> responses = productService.getAllProducts();

    assertNotNull(responses);
    assertEquals(1, responses.size());
    ProductResponse response = responses.get(0);
    assertEquals(5L, response.getId());
    assertEquals("Complete Product", response.getName());
    assertEquals("Full description", response.getDescription());
    assertEquals(new BigDecimal("199.99"), response.getPrice());
    assertEquals(25, response.getStock());
    assertEquals("Premium", response.getCategory());
    assertEquals("SKU-PREM-001", response.getSku());
  }

  /**
   * Test: Actualizar un producto existente con datos válidos.
   */
  @Test
  void updateProduct_WithValidData_ShouldReturnUpdatedProduct() {
    Long productId = 1L;
    LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    LocalDateTime now = LocalDateTime.now();

    Product existingProduct = Product.builder()
        .id(productId)
        .name("Old Product Name")
        .description("Old Description")
        .price(new BigDecimal("50.00"))
        .stock(100)
        .category("Old Category")
        .sku("OLD-SKU")
        .active(true)
        .createdAt(createdAt)
        .updatedAt(createdAt)
        .build();

    ProductRequest request = ProductRequest.builder()
        .name("New Product Name")
        .description("New Description")
        .price(new BigDecimal("99.99"))
        .stock(50)
        .category("New Category")
        .sku("NEW-SKU")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
      Product product = invocation.getArgument(0);
      product.setUpdatedAt(now);
      return product;
    });

    ProductResponse response = productService.updateProduct(productId, request);

    assertNotNull(response);
    assertEquals(productId, response.getId());
    assertEquals("New Product Name", response.getName());
    assertEquals("New Description", response.getDescription());
    assertEquals(new BigDecimal("99.99"), response.getPrice());
    assertEquals(50, response.getStock());
    assertEquals("New Category", response.getCategory());
    assertEquals("NEW-SKU", response.getSku());
    verify(productRepository, times(1)).findById(productId);
    verify(productRepository, times(1)).save(any(Product.class));
  }

  /**
   * Test: Actualizar un producto que no existe.
   */
  @Test
  void updateProduct_WhenProductNotFound_ShouldThrowException() {
    Long productId = 999L;
    ProductRequest request = ProductRequest.builder()
        .name("Test Name")
        .description("Test Description")
        .price(new BigDecimal("99.99"))
        .stock(100)
        .category("Test Category")
        .sku("TEST-SKU")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, 
        () -> productService.updateProduct(productId, request));

    verify(productRepository, times(1)).findById(productId);
    verify(productRepository, times(0)).save(any(Product.class));
  }

  /**
   * Test: Actualizar solo el nombre del producto.
   */
  @Test
  void updateProduct_WithPartialUpdate_ShouldUpdateAllFields() {
    Long productId = 1L;
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Old Name")
        .description("Old Description")
        .price(new BigDecimal("50.00"))
        .stock(100)
        .category("Old Category")
        .sku("OLD-SKU")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    ProductRequest request = ProductRequest.builder()
        .name("Updated Name Only")
        .description("Old Description")
        .price(new BigDecimal("50.00"))
        .stock(100)
        .category("Old Category")
        .sku("OLD-SKU")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
      Product product = invocation.getArgument(0);
      product.setUpdatedAt(LocalDateTime.now());
      return product;
    });

    ProductResponse response = productService.updateProduct(productId, request);

    assertNotNull(response);
    assertEquals("Updated Name Only", response.getName());
    verify(productRepository, times(1)).save(any(Product.class));
  }

  /**
   * Test: Actualizar precio a un valor mayor.
   */
  @Test
  void updateProduct_WithHigherPrice_ShouldUpdateSuccessfully() {
    Long productId = 1L;
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Product")
        .description("Description")
        .price(new BigDecimal("50.00"))
        .stock(100)
        .category("Category")
        .sku("SKU")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    ProductRequest request = ProductRequest.builder()
        .name("Product")
        .description("Description")
        .price(new BigDecimal("250.00"))
        .stock(100)
        .category("Category")
        .sku("SKU")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> 
        invocation.getArgument(0));

    ProductResponse response = productService.updateProduct(productId, request);

    assertNotNull(response);
    assertEquals(new BigDecimal("250.00"), response.getPrice());
  }

  /**
   * Test: Actualizar stock a cero.
   */
  @Test
  void updateProduct_WithZeroStock_ShouldUpdateSuccessfully() {
    Long productId = 1L;
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Product")
        .description("Description")
        .price(new BigDecimal("99.99"))
        .stock(100)
        .category("Category")
        .sku("SKU")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    ProductRequest request = ProductRequest.builder()
        .name("Product")
        .description("Description")
        .price(new BigDecimal("99.99"))
        .stock(0)
        .category("Category")
        .sku("SKU")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> 
        invocation.getArgument(0));

    ProductResponse response = productService.updateProduct(productId, request);

    assertNotNull(response);
    assertEquals(0, response.getStock());
  }

  /**
   * Test: Actualizar todos los campos a la vez.
   */
  @Test
  void updateProduct_WithAllFieldsChanged_ShouldUpdateAllCorrectly() {
    Long productId = 1L;
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Original")
        .description("Original Desc")
        .price(new BigDecimal("10.00"))
        .stock(1)
        .category("Original Cat")
        .sku("ORIG")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    ProductRequest request = ProductRequest.builder()
        .name("Brand New Name")
        .description("Completely New Description")
        .price(new BigDecimal("500.00"))
        .stock(1000)
        .category("Premium Electronics")
        .sku("PREMIUM-001")
        .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> 
        invocation.getArgument(0));

    ProductResponse response = productService.updateProduct(productId, request);

    assertNotNull(response);
    assertEquals("Brand New Name", response.getName());
    assertEquals("Completely New Description", response.getDescription());
    assertEquals(new BigDecimal("500.00"), response.getPrice());
    assertEquals(1000, response.getStock());
    assertEquals("Premium Electronics", response.getCategory());
    assertEquals("PREMIUM-001", response.getSku());
  }

}

