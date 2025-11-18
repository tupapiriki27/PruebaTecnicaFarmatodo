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
public class CustomerResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String address;
  private String city;
  private String state;
  private String zipCode;
  private String country;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean active;

}

