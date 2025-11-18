package co.com.testapp.testapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status;

  // Información de entrega
  @Column(length = 255)
  private String shippingAddress;

  @Column(length = 100)
  private String shippingCity;

  @Column(length = 100)
  private String shippingState;

  @Column(length = 20)
  private String shippingZipCode;

  @Column(length = 100)
  private String shippingCountry;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void removeItem(OrderItem item) {
    items.remove(item);
    item.setOrder(null);
  }

}

