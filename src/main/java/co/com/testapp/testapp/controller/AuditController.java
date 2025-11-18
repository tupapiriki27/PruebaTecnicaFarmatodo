package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.dto.AuditLogResponse;
import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.entity.EventStatus;
import co.com.testapp.testapp.entity.EventType;
import co.com.testapp.testapp.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador REST para acceder a los registros de auditoría del sistema.
 * 
 * Proporciona endpoints para consultar eventos de auditoría con diversos filtros
 * como tipo de evento, entidad, usuario, estado y rango de fechas. Todos los
 * endpoints retornan registros con UUID único para trazabilidad completa.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/audit")
@Slf4j
public class AuditController {

  private final AuditService auditService;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  /**
   * Constructor que inyecta el servicio de auditoría.
   * 
   * @param auditService Servicio para gestionar registros de auditoría
   */
  public AuditController(AuditService auditService) {
    this.auditService = auditService;
  }

  /**
   * Obtiene un registro de auditoría específico por su UUID.
   * 
   * @param id UUID del registro de auditoría
   * @return Respuesta con el registro encontrado (200) o no encontrado (404)
   */
  @GetMapping("/{id}")
  public ResponseEntity<AuditLogResponse> getAuditLog(@PathVariable UUID id) {
    log.info("Fetching audit log with ID: {}", id);
    AuditLog auditLog = auditService.getAuditLogById(id);
    if (auditLog == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(mapToResponse(auditLog));
  }

  /**
   * Obtiene todos los registros de auditoría asociados a una entidad específica.
   * 
   * @param entityId ID de la entidad
   * @return Lista de registros de auditoría para esa entidad
   */
  @GetMapping("/entity/{entityId}")
  public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntity(@PathVariable String entityId) {
    log.info("Obteniendo registros de auditoría para entidad: {}", entityId);
    List<AuditLog> logs = auditService.getAuditLogsByEntityId(entityId);
    List<AuditLogResponse> responses = logs.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  /**
   * Obtiene registros de auditoría filtrados por tipo de evento.
   * 
   * @param eventType Tipo de evento a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @GetMapping("/event-type/{eventType}")
  public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByEventType(
      @PathVariable String eventType,
      Pageable pageable) {
    log.info("Obteniendo registros de auditoría para tipo de evento: {}", eventType);
    try {
      EventType type = EventType.valueOf(eventType);
      Page<AuditLog> logs = auditService.getAuditLogsByEventType(type, pageable);
      Page<AuditLogResponse> responses = logs.map(this::mapToResponse);
      return ResponseEntity.ok(responses);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Obtiene registros de auditoría filtrados por tipo de entidad.
   * 
   * @param entityType Tipo de entidad a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @GetMapping("/entity-type/{entityType}")
  public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByEntityType(
      @PathVariable String entityType,
      Pageable pageable) {
    log.info("Obteniendo registros de auditoría para tipo de entidad: {}", entityType);
    Page<AuditLog> logs = auditService.getAuditLogsByEntityType(entityType, pageable);
    Page<AuditLogResponse> responses = logs.map(this::mapToResponse);
    return ResponseEntity.ok(responses);
  }

  /**
   * Obtiene registros de auditoría filtrados por ID de usuario.
   * 
   * @param userId ID del usuario a filtrar
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByUser(
      @PathVariable String userId,
      Pageable pageable) {
    log.info("Obteniendo registros de auditoría para usuario: {}", userId);
    Page<AuditLog> logs = auditService.getAuditLogsByUserId(userId, pageable);
    Page<AuditLogResponse> responses = logs.map(this::mapToResponse);
    return ResponseEntity.ok(responses);
  }

  /**
   * Obtiene registros de auditoría filtrados por estado del evento.
   * 
   * @param status Estado a filtrar (SUCCESS, FAILURE, PENDING, RETRY)
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @GetMapping("/status/{status}")
  public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByStatus(
      @PathVariable String status,
      Pageable pageable) {
    log.info("Obteniendo registros de auditoría para estado: {}", status);
    try {
      EventStatus eventStatus = EventStatus.valueOf(status);
      Page<AuditLog> logs = auditService.getAuditLogsByStatus(eventStatus, pageable);
      Page<AuditLogResponse> responses = logs.map(this::mapToResponse);
      return ResponseEntity.ok(responses);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Obtiene registros de auditoría dentro de un rango de fechas.
   * 
   * @param startDate Fecha de inicio en formato ISO 8601 (ej: 2024-11-16T00:00:00)
   * @param endDate Fecha de fin en formato ISO 8601
   * @param pageable Información de paginación
   * @return Página de registros encontrados
   */
  @GetMapping("/date-range")
  public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByDateRange(
      @RequestParam String startDate,
      @RequestParam String endDate,
      Pageable pageable) {
    log.info("Obteniendo registros de auditoría para rango de fechas: {} a {}", startDate, endDate);
    try {
      LocalDateTime start = LocalDateTime.parse(startDate, DATE_FORMATTER);
      LocalDateTime end = LocalDateTime.parse(endDate, DATE_FORMATTER);
      Page<AuditLog> logs = auditService.getAuditLogsByDateRange(start, end, pageable);
      Page<AuditLogResponse> responses = logs.map(this::mapToResponse);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Mapea una entidad AuditLog a su DTO de respuesta.
   * 
   * @param auditLog Entidad de auditoría a mapear
   * @return DTO con los datos de auditoría formateados
   */
  private AuditLogResponse mapToResponse(AuditLog auditLog) {
    return AuditLogResponse.builder()
        .id(auditLog.getId())
        .eventType(auditLog.getEventType().name())
        .entityType(auditLog.getEntityType())
        .entityId(auditLog.getEntityId())
        .userId(auditLog.getUserId())
        .description(auditLog.getDescription())
        .details(auditLog.getDetails())
        .status(auditLog.getStatus().name())
        .errorMessage(auditLog.getErrorMessage())
        .createdAt(auditLog.getCreatedAt())
        .sourceIp(auditLog.getSourceIp())
        .build();
  }

}

