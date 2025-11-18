package co.com.testapp.testapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenizationResponse {

  private String token;
  private String lastFourDigits;
  private String cardBrand;
  private String expirationDate;
  private LocalDateTime createdAt;
  private Boolean active;

}

