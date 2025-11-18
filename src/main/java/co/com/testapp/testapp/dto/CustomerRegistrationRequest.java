package co.com.testapp.testapp.dto;

import jakarta.validation.constraints.Email;
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
public class CustomerRegistrationRequest {

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be between 10 and 20 digits and may include a leading +")
  private String phoneNumber;

  @NotBlank(message = "Address is required")
  @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
  private String address;

  @Size(max = 100, message = "City must not exceed 100 characters")
  private String city;

  @Size(max = 100, message = "State must not exceed 100 characters")
  private String state;

  @Pattern(regexp = "^[0-9]{5,20}$", message = "Zip code must be between 5 and 20 digits")
  private String zipCode;

  @Size(max = 100, message = "Country must not exceed 100 characters")
  private String country;

}

