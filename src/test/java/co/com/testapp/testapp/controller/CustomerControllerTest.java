package co.com.testapp.testapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.com.testapp.testapp.dto.CustomerRegistrationRequest;
import co.com.testapp.testapp.dto.CustomerResponse;
import co.com.testapp.testapp.exception.DuplicateCustomerException;
import co.com.testapp.testapp.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

  private static final String CUSTOMERS_URL = "/api/v1/customers";
  private static final String API_KEY_HEADER = "X-API-Key";
  private static final String VALID_FIRST_NAME = "John";
  private static final String VALID_LAST_NAME = "Doe";
  private static final String VALID_EMAIL = "john.doe@example.com";
  private static final String VALID_PHONE = "+1234567890";
  private static final String VALID_ADDRESS = "123 Main Street";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CustomerService customerService;

  @Value("${customer.api.key}")
  private String customerApiKey;

  @Test
  void registerCustomer_WithValidRequest_ShouldReturnCreated() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .city("New York")
        .state("NY")
        .zipCode("10001")
        .country("USA")
        .build();

    CustomerResponse response = CustomerResponse.builder()
        .id(1L)
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .city("New York")
        .state("NY")
        .zipCode("10001")
        .country("USA")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .active(true)
        .build();

    when(customerService.registerCustomer(any(CustomerRegistrationRequest.class))).thenReturn(response);

    mockMvc.perform(post(CUSTOMERS_URL)
            .header(API_KEY_HEADER, customerApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value(VALID_FIRST_NAME))
        .andExpect(jsonPath("$.email").value(VALID_EMAIL));
  }

  @Test
  void registerCustomer_WithoutApiKey_ShouldReturnForbidden() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    mockMvc.perform(post(CUSTOMERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void registerCustomer_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email("invalid-email")
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    mockMvc.perform(post(CUSTOMERS_URL)
            .header(API_KEY_HEADER, customerApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void registerCustomer_WithInvalidPhoneNumber_ShouldReturnBadRequest() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber("123")
        .address(VALID_ADDRESS)
        .build();

    mockMvc.perform(post(CUSTOMERS_URL)
            .header(API_KEY_HEADER, customerApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void registerCustomer_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    when(customerService.registerCustomer(any(CustomerRegistrationRequest.class)))
        .thenThrow(new DuplicateCustomerException("Email 'john.doe@example.com' is already registered"));

    mockMvc.perform(post(CUSTOMERS_URL)
            .header(API_KEY_HEADER, customerApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Duplicate Customer"));
  }

  @Test
  void registerCustomer_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .build();

    mockMvc.perform(post(CUSTOMERS_URL)
            .header(API_KEY_HEADER, customerApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

}

