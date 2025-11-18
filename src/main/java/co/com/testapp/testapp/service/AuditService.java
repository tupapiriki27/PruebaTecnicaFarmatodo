package co.com.testapp.testapp.service;

import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.entity.EventStatus;
import co.com.testapp.testapp.entity.EventType;
import co.com.testapp.testapp.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar el registro y consulta de eventos de auditoría del sistema.
 *
 * Proporciona métodos centralizados para registrar eventos en el sistema con diferentes
 * estados (éxito, fallo, pendiente, reintento). Los eventos se guardan en la base de datos
 * con UUID único para trazabilidad completa.
 *
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
@Transactional
public class AuditService {

  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  /**
   * Constructor que inyecta las dependencias necesarias.
   *
   * @param auditLogRepository Repositorio para acceder a los registros de auditoría
   * @param objectMapper Mapeador de objetos para serializar detalles a JSON
   */
  public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
    this.auditLogRepository = auditLogRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Registra un evento genérico en el sistema de auditoría.
   *
   * Método base que guarda todos los detalles de un evento, incluyendo tipo, estado,
   * descripción y detalles adicionales en formato JSON. Si ocurre un error durante
   * el registro, se registra en los logs pero no se interrumpe el flujo principal.
   *
   * @param eventTypeName Nombre del tipo de evento (ej: "PAYMENT_APPROVED")
   * @param entityType Tipo de entidad afectada (ej: "PAYMENT", "ORDER")
   * @param entityId ID de la entidad específica
   * @param userId ID del usuario que realizó la acción (puede ser null)
   * @param description Descripción legible del evento
   * @param details Objeto con detalles adicionales (se convierte a JSON)
   * @param status Estado del evento (SUCCESS, FAILURE, PENDING, RETRY)
   * @param errorMessage Mensaje de error si aplica (puede ser null)
   */

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void logEvent(String eventTypeName, String entityType, String entityId, String userId,
                       String description, Object details, EventStatus status, String errorMessage) {
    try {
      EventType eventType = EventType.valueOf(eventTypeName);

      String detailsJson = null;
      if (details != null) {
        detailsJson = objectMapper.writeValueAsString(details);
      }

      AuditLog auditLog = AuditLog.builder()
          .eventType(eventType)
          .entityType(entityType)
          .entityId(entityId)
          .userId(userId)
          .description(description)
          .details(detailsJson)
          .status(status)
          .errorMessage(errorMessage)
          .createdAt(LocalDateTime.now())
          .build();

      auditLogRepository.save(auditLog);
      log.debug("Audit event logged: {} for {} with id {}", eventType, entityType, entityId);
    } catch (Exception e) {
      log.error("Error logging audit event", e);
      // No lanzar excepción para no interrumpir el flujo principal
    }
  }

  /**
   * Registra un evento exitoso en el sistema de auditoría.
   *
   * Registra un evento que se completó correctamente. El estado se establece automáticamente
   * como SUCCESS.
   *
   * @param eventType Nombre del tipo de evento
   * @param entityType Tipo de entidad afectada
   * @param entityId ID de la entidad específica
   * @param userId ID del usuario que realizó la acción
   * @param description Descripción del evento
   * @param details Detalles adicionales del evento
   */
  public void logSuccessEvent(String eventType, String entityType, String entityId, String userId,
                              String description, Object details) {
    logEvent(eventType, entityType, entityId, userId, description, details, EventStatus.SUCCESS, null);
  }

  /**
   * Registra un evento fallido en el sistema de auditoría.
   *
   * Registra un evento que falló durante su ejecución. El estado se establece automáticamente
   * como FAILURE e incluye un mensaje de error.
   *
   * @param eventType Nombre del tipo de evento
   * @param entityType Tipo de entidad afectada
   * @param entityId ID de la entidad específica
   * @param userId ID del usuario que realizó la acción
   * @param description Descripción del evento
   * @param errorMessage Mensaje de error que describe el fallo
   * @param details Detalles adicionales del evento
   */
  public void logFailureEvent(String eventType, String entityType, String entityId, String userId,
                              String description, String errorMessage, Object details) {
    logEvent(eventType, entityType, entityId, userId, description, details, EventStatus.FAILURE, errorMessage);
  }

  /**
   * Registra un evento de reintento en el sistema de auditoría.
   *
   * Registra un evento que está siendo reintentado después de un fallo anterior.
   * El estado se establece automáticamente como RETRY.
   *
   * @param eventType Nombre del tipo de evento
   * @param entityType Tipo de entidad afectada
   * @param entityId ID de la entidad específica
   * @param userId ID del usuario que realizó la acción
   * @param description Descripción del evento
   * @param details Detalles adicionales del evento
   */
  public void logRetryEvent(String eventType, String entityType, String entityId, String userId,
                            String description, Object details) {
    logEvent(eventType, entityType, entityId, userId, description, details, EventStatus.RETRY, null);
  }

  /**
   * Obtiene un registro de auditoría específico por su UUID.
   *
   * @param id UUID del registro de auditoría a recuperar
   * @return El registro de auditoría encontrado, o null si no existe
   */
  @Transactional(readOnly = true)
  public AuditLog getAuditLogById(UUID id) {
    return auditLogRepository.findById(id).orElse(null);
  }

  /**
   * Obtiene todos los registros de auditoría asociados a una entidad específica.
   *
   * @param entityId ID de la entidad
   * @return Lista de registros ordenados por fecha descendente
   */
  @Transactional(readOnly = true)
  public List<AuditLog> getAuditLogsByEntityId(String entityId) {
    return auditLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId);
  }

  /**
   * Obtiene registros de auditoría filtrados por tipo de evento.
   *
   * @param eventType Tipo de evento a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByEventType(EventType eventType, Pageable pageable) {
    return auditLogRepository.findByEventTypeOrderByCreatedAtDesc(eventType, pageable);
  }

  /**
   * Obtiene registros de auditoría filtrados por tipo de entidad.
   *
   * @param entityType Tipo de entidad a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByEntityType(String entityType, Pageable pageable) {
    return auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType, pageable);
  }

  /**
   * Obtiene registros de auditoría filtrados por ID de usuario.
   *
   * @param userId ID del usuario a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByUserId(String userId, Pageable pageable) {
    return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
  }

  /**
   * Obtiene registros de auditoría filtrados por estado del evento.
   *
   * @param status Estado del evento a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByStatus(EventStatus status, Pageable pageable) {
    return auditLogRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
  }

  /**
   * Obtiene registros de auditoría dentro de un rango de fechas específico.
   *
   * @param startDate Fecha y hora de inicio del rango
   * @param endDate Fecha y hora de fin del rango
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    return auditLogRepository.findByDateRangeOrderByCreatedAtDesc(startDate, endDate, pageable);
  }

  /**
   * Obtiene registros de auditoría filtrados por tipo de evento y estado combinados.
   *
   * @param eventType Tipo de evento a filtrar
   * @param status Estado del evento a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @Transactional(readOnly = true)
  public Page<AuditLog> getAuditLogsByEventTypeAndStatus(EventType eventType,
                                                          EventStatus status,
                                                          Pageable pageable) {
    return auditLogRepository.findByEventTypeAndStatusOrderByCreatedAtDesc(eventType, status, pageable);
  }

}

