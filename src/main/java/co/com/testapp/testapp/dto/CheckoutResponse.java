package co.com.testapp.testapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

  private Long orderId;
  private Long customerId;
  private String customerName;
  private List<OrderItemResponse> items;
  private BigDecimal totalAmount;
  private String orderStatus;
  private PaymentResponse payment;
  private String shippingAddress;
  private String shippingCity;
  private String shippingState;
  private String shippingZipCode;
  private String shippingCountry;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

