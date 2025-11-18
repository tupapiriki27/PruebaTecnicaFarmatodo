package co.com.testapp.testapp.service;

import co.com.testapp.testapp.dto.ProductRequest;
import co.com.testapp.testapp.dto.ProductResponse;
import co.com.testapp.testapp.entity.Product;
import co.com.testapp.testapp.exception.ProductNotFoundException;
import co.com.testapp.testapp.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de productos en el catálogo de e-commerce.
 * 
 * Proporciona operaciones CRUD para productos, incluyendo creación, lectura,
 * actualización y filtrado de productos activos.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
@Transactional
public class ProductService {

  private final ProductRepository productRepository;

  /**
   * Constructor que inyecta el repositorio de productos.
   * 
   * @param productRepository Repositorio para acceder a los productos
   */
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Crea un nuevo producto en el catálogo.
   * 
   * @param request Datos del producto a crear (nombre, descripción, precio, stock, etc.)
   * @return Respuesta con los detalles del producto creado
   */
  public ProductResponse createProduct(ProductRequest request) {
    log.info("Creating product: {}", request.getName());

    Product product = Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .stock(request.getStock())
        .category(request.getCategory())
        .sku(request.getSku())
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    Product savedProduct = productRepository.save(product);

    log.info("Product created successfully with ID: {}", savedProduct.getId());

    return mapToResponse(savedProduct);
  }

  /**
   * Obtiene la lista de todos los productos activos.
   * 
   * @return Lista de respuestas con detalles de productos activos
   */
  @Transactional(readOnly = true)
  public List<ProductResponse> getAllProducts() {
    log.info("Fetching all active products");
    return productRepository.findByActiveTrue().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene un producto específico por su ID.
   * 
   * @param id ID del producto a recuperar
   * @return Respuesta con los detalles del producto
   * @throws ProductNotFoundException Si el producto no existe o está inactivo
   */
  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    log.info("Fetching product with ID: {}", id);
    Product product = productRepository.findByIdAndActiveTrue(id)
        .orElseThrow(() -> new ProductNotFoundException(
            String.format("Product with ID %d not found or is inactive", id)
        ));
    return mapToResponse(product);
  }

  /**
   * Actualiza la información de un producto existente.
   * 
   * @param id ID del producto a actualizar
   * @param request Nuevos datos del producto
   * @return Respuesta con los detalles del producto actualizado
   * @throws ProductNotFoundException Si el producto no existe
   */
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    log.info("Updating product with ID: {}", id);

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(
            String.format("Product with ID %d not found", id)
        ));

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setCategory(request.getCategory());
    product.setSku(request.getSku());
    product.setUpdatedAt(LocalDateTime.now());

    Product updatedProduct = productRepository.save(product);

    log.info("Product updated successfully with ID: {}", updatedProduct.getId());

    return mapToResponse(updatedProduct);
  }

  /**
   * Convierte una entidad Product a su DTO de respuesta.
   * 
   * @param product Entidad de producto a convertir
   * @return DTO con los datos del producto formateados
   */
  private ProductResponse mapToResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .category(product.getCategory())
        .sku(product.getSku())
        .active(product.getActive())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }

}

