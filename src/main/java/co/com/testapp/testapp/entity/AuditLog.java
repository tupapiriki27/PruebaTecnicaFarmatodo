package co.com.testapp.testapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para registrar y auditar todos los eventos del sistema.
 * 
 * Cada transacción tiene un UUID único para identificación global a nivel mundial.
 * Proporciona trazabilidad completa de todas las operaciones realizadas en el sistema,
 * incluyendo información detallada sobre qué sucedió, cuándo ocurrió, quién lo realizó
 * y cuál fue el resultado.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

  /** Identificador único UUID del registro de auditoría */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "VARCHAR(36)")
  private UUID id;

  /** Tipo de evento que ocurrió en el sistema */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EventType eventType;

  /** Tipo de entidad sobre la que se realizó la acción */
  @Column(nullable = false, length = 100)
  private String entityType;

  /** ID específico de la entidad afectada */
  @Column(length = 36)
  private String entityId;

  /** ID del usuario que realizó la acción (opcional) */
  @Column(length = 100)
  private String userId;

  /** Descripción legible del evento */
  @Column(length = 500)
  private String description;

  /** Detalles técnicos del evento en formato JSON */
  @Column(columnDefinition = "TEXT")
  private String details;

  /** Estado del evento (éxito, fallo, pendiente, reintento) */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EventStatus status;

  /** Mensaje de error si el evento falló */
  @Column(length = 500)
  private String errorMessage;

  /** Fecha y hora exacta cuando ocurrió el evento */
  @Column(nullable = false)
  private LocalDateTime createdAt;

  /** Dirección IP de origen de la solicitud (opcional) */
  @Column(length = 100)
  private String sourceIp;

}

