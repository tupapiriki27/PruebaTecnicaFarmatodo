package co.com.testapp.testapp.service;

import co.com.testapp.testapp.dto.CustomerRegistrationRequest;
import co.com.testapp.testapp.dto.CustomerResponse;
import co.com.testapp.testapp.entity.Customer;
import co.com.testapp.testapp.exception.DuplicateCustomerException;
import co.com.testapp.testapp.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio para la gestión de clientes en el sistema.
 * 
 * Proporciona operaciones para el registro de nuevos clientes,
 * validando la unicidad de email y número de teléfono.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
@Transactional
public class CustomerService {

  private final CustomerRepository customerRepository;

  /**
   * Constructor que inyecta el repositorio de clientes.
   * 
   * @param customerRepository Repositorio para acceder a los clientes
   */
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  /**
   * Registra un nuevo cliente en el sistema.
   * 
   * Valida que el email y número de teléfono sean únicos antes de crear
   * el cliente. Normaliza el email a minúsculas.
   * 
   * @param request Datos del cliente a registrar
   * @return Respuesta con los detalles del cliente registrado
   * @throws DuplicateCustomerException Si el email o teléfono ya están registrados
   */
  public CustomerResponse registerCustomer(CustomerRegistrationRequest request) {
    log.info("Initiating customer registration for email: {}", request.getEmail());

    // Validate email uniqueness
    validateEmailUniqueness(request.getEmail());

    // Validate phone number uniqueness
    validatePhoneNumberUniqueness(request.getPhoneNumber());

    // Create customer entity
    Customer customer = Customer.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail().toLowerCase())
        .phoneNumber(request.getPhoneNumber())
        .address(request.getAddress())
        .city(request.getCity())
        .state(request.getState())
        .zipCode(request.getZipCode())
        .country(request.getCountry())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .active(true)
        .build();

    // Save customer
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Customer registered successfully with ID: {} and email: {}", 
        savedCustomer.getId(), savedCustomer.getEmail());

    return mapToResponse(savedCustomer);
  }

  /**
   * Valida que un email no esté ya registrado en el sistema.
   * 
   * @param email Email a validar
   * @throws DuplicateCustomerException Si el email ya está registrado
   */
  private void validateEmailUniqueness(String email) {
    String normalizedEmail = email.toLowerCase();
    if (customerRepository.existsByEmail(normalizedEmail)) {
      log.warn("Attempted to register with existing email: {}", normalizedEmail);
      throw new DuplicateCustomerException(
          String.format("Email '%s' is already registered", email)
      );
    }
  }

  /**
   * Valida que un número de teléfono no esté ya registrado en el sistema.
   * 
   * @param phoneNumber Número de teléfono a validar
   * @throws DuplicateCustomerException Si el número de teléfono ya está registrado
   */
  private void validatePhoneNumberUniqueness(String phoneNumber) {
    if (customerRepository.existsByPhoneNumber(phoneNumber)) {
      log.warn("Attempted to register with existing phone number: {}", phoneNumber);
      throw new DuplicateCustomerException(
          String.format("Phone number '%s' is already registered", phoneNumber)
      );
    }
  }

  /**
   * Convierte una entidad Customer a su DTO de respuesta.
   * 
   * @param customer Entidad de cliente a convertir
   * @return DTO con los datos del cliente formateados
   */
  private CustomerResponse mapToResponse(Customer customer) {
    return CustomerResponse.builder()
        .id(customer.getId())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .email(customer.getEmail())
        .phoneNumber(customer.getPhoneNumber())
        .address(customer.getAddress())
        .city(customer.getCity())
        .state(customer.getState())
        .zipCode(customer.getZipCode())
        .country(customer.getCountry())
        .createdAt(customer.getCreatedAt())
        .updatedAt(customer.getUpdatedAt())
        .active(customer.getActive())
        .build();
  }

}

