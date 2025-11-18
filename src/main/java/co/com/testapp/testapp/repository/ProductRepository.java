package co.com.testapp.testapp.repository;

import co.com.testapp.testapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByActiveTrue();

  Optional<Product> findByIdAndActiveTrue(Long id);

  Optional<Product> findBySku(String sku);

  List<Product> findByCategory(String category);

}

