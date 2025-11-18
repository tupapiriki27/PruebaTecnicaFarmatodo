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
@Table(name = "card_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 64)
  private String token;

  @Column(nullable = false, length = 4)
  private String lastFourDigits;

  @Column(nullable = false, length = 50)
  private String cardBrand;

  @Column(nullable = false)
  private String expirationDate;

  @Column(nullable = false, length = 100)
  private String cardholderName;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private Boolean active;

}

