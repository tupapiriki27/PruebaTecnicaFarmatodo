package co.com.testapp.testapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(nullable = false, unique = true, length = 20)
  private String phoneNumber;

  @Column(nullable = false, length = 255)
  private String address;

  @Column(length = 100)
  private String city;

  @Column(length = 100)
  private String state;

  @Column(length = 20)
  private String zipCode;

  @Column(length = 100)
  private String country;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private Boolean active;

}

