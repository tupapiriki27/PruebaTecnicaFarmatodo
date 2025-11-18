package co.com.testapp.testapp.repository;

import co.com.testapp.testapp.entity.Order;
import co.com.testapp.testapp.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByCustomerId(Long customerId);

  @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = 'CART'")
  Optional<Order> findActiveCartByCustomerId(@Param("customerId") Long customerId);

  List<Order> findByStatus(OrderStatus status);

}

