package co.com.testapp.testapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) para retornar registros de auditoría en respuestas REST.
 * 
 * Contiene todos los detalles de un evento de auditoría en formato JSON,
 * incluyendo el UUID único, tipo de evento, entidad afectada y detalles técnicos.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

  /** Identificador único UUID del registro de auditoría */
  private UUID id;
  
  /** Tipo de evento que ocurrió */
  private String eventType;
  
  /** Tipo de entidad afectada */
  private String entityType;
  
  /** ID específico de la entidad */
  private String entityId;
  
  /** ID del usuario que realizó la acción */
  private String userId;
  
  /** Descripción legible del evento */
  private String description;
  
  /** Detalles técnicos en formato JSON */
  private String details;
  
  /** Estado del evento (SUCCESS, FAILURE, PENDING, RETRY) */
  private String status;
  
  /** Mensaje de error si el evento falló */
  private String errorMessage;
  
  /** Fecha y hora cuando ocurrió el evento */
  private LocalDateTime createdAt;
  
  /** Dirección IP de origen */
  private String sourceIp;

}

