package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.dto.CustomerRegistrationRequest;
import co.com.testapp.testapp.dto.CustomerResponse;
import co.com.testapp.testapp.entity.Customer;
import co.com.testapp.testapp.exception.DuplicateCustomerException;
import co.com.testapp.testapp.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  private static final String VALID_FIRST_NAME = "John";
  private static final String VALID_LAST_NAME = "Doe";
  private static final String VALID_EMAIL = "john.doe@example.com";
  private static final String VALID_PHONE = "+1234567890";
  private static final String VALID_ADDRESS = "123 Main Street";

  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerService customerService;

  @Test
  void registerCustomer_WithValidData_ShouldReturnCustomerResponse() {
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

    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
      Customer customer = invocation.getArgument(0);
      customer.setId(1L);
      return customer;
    });

    CustomerResponse response = customerService.registerCustomer(request);

    assertNotNull(response);
    assertNotNull(response.getId());
    assertEquals(VALID_FIRST_NAME, response.getFirstName());
    assertEquals(VALID_LAST_NAME, response.getLastName());
    assertEquals(VALID_EMAIL.toLowerCase(), response.getEmail());
    assertEquals(VALID_PHONE, response.getPhoneNumber());
    assertTrue(response.getActive());

    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void registerCustomer_WithDuplicateEmail_ShouldThrowDuplicateCustomerException() {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    when(customerRepository.existsByEmail(anyString())).thenReturn(true);

    DuplicateCustomerException exception = assertThrows(
        DuplicateCustomerException.class,
        () -> customerService.registerCustomer(request)
    );

    assertTrue(exception.getMessage().contains("already registered"));
    verify(customerRepository, times(0)).save(any(Customer.class));
  }

  @Test
  void registerCustomer_WithDuplicatePhoneNumber_ShouldThrowDuplicateCustomerException() {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email(VALID_EMAIL)
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(true);

    DuplicateCustomerException exception = assertThrows(
        DuplicateCustomerException.class,
        () -> customerService.registerCustomer(request)
    );

    assertTrue(exception.getMessage().contains("already registered"));
    verify(customerRepository, times(0)).save(any(Customer.class));
  }

  @Test
  void registerCustomer_ShouldNormalizeEmailToLowercase() {
    CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
        .firstName(VALID_FIRST_NAME)
        .lastName(VALID_LAST_NAME)
        .email("John.Doe@EXAMPLE.COM")
        .phoneNumber(VALID_PHONE)
        .address(VALID_ADDRESS)
        .build();

    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
      Customer customer = invocation.getArgument(0);
      customer.setId(1L);
      return customer;
    });

    CustomerResponse response = customerService.registerCustomer(request);

    assertEquals("john.doe@example.com", response.getEmail());
  }

}

