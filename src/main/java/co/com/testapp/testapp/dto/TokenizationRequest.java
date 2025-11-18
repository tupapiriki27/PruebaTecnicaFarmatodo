package co.com.testapp.testapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenizationRequest {

  @NotBlank(message = "Card number is required")
  @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be between 13 and 19 digits")
  private String cardNumber;

  @NotBlank(message = "CVV is required")
  @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
  private String cvv;

  @NotBlank(message = "Expiration date is required")
  @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Expiration date must be in MM/YY format")
  private String expirationDate;

  @NotBlank(message = "Cardholder name is required")
  @Size(min = 3, max = 100, message = "Cardholder name must be between 3 and 100 characters")
  private String cardholderName;

}

