package co.com.testapp.testapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private Long id;
  private Long orderId;
  private BigDecimal amount;
  private String status;
  private Integer attemptCount;
  private String failureReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

