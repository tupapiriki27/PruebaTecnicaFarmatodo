package co.com.testapp.testapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

  @NotNull(message = "Customer ID is required")
  private Long customerId;

  @NotBlank(message = "Tokenized card is required")
  private String tokenizedCard;

  @NotBlank(message = "Shipping address is required")
  @Size(max = 255, message = "Shipping address must not exceed 255 characters")
  private String shippingAddress;

  @NotBlank(message = "City is required")
  @Size(max = 100, message = "City must not exceed 100 characters")
  private String shippingCity;

  @NotBlank(message = "State/Province is required")
  @Size(max = 100, message = "State/Province must not exceed 100 characters")
  private String shippingState;

  @NotBlank(message = "Zip code is required")
  @Size(max = 20, message = "Zip code must not exceed 20 characters")
  private String shippingZipCode;

  @NotBlank(message = "Country is required")
  @Size(max = 100, message = "Country must not exceed 100 characters")
  private String shippingCountry;

}

