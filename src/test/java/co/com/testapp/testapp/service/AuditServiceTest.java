package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.entity.EventStatus;
import co.com.testapp.testapp.entity.EventType;
import co.com.testapp.testapp.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tests para el servicio de auditoría.
 *
 * Cubre todos los métodos públicos de AuditService incluyendo:
 * - logEvent: Método base para registrar eventos
 * - logSuccessEvent: Registrar eventos exitosos
 * - logFailureEvent: Registrar eventos fallidos
 * - logRetryEvent: Registrar reintentos
 * - getAuditLogById: Obtener por UUID
 * - getAuditLogsByEntityId: Filtrar por entidad
 * - getAuditLogsByEventType: Filtrar por tipo de evento
 * - getAuditLogsByEntityType: Filtrar por tipo de entidad
 * - getAuditLogsByUserId: Filtrar por usuario
 * - getAuditLogsByStatus: Filtrar por estado
 * - getAuditLogsByDateRange: Filtrar por rango de fechas
 * - getAuditLogsByEventTypeAndStatus: Filtrar por combinación evento-estado
 *
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

  @Mock
  private AuditLogRepository auditLogRepository;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private AuditService auditService;

  private UUID testId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    testId = UUID.randomUUID();
    now = LocalDateTime.now();
  }

  // ==================== logEvent Tests ====================

  /**
   * Test: Registrar un evento genérico con todos los parámetros.
   */
  @Test
  void logEvent_WithAllParameters_ShouldSaveAuditLog() throws Exception {
    String eventType = "PAYMENT_APPROVED";
    String entityType = "PAYMENT";
    String entityId = "pay_123";
    String userId = "user_456";
    String description = "Payment processed successfully";
    Object details = new Object();
    EventStatus status = EventStatus.SUCCESS;

    when(objectMapper.writeValueAsString(details)).thenReturn("{\"amount\": 100.0}");
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logEvent(eventType, entityType, entityId, userId, description, details, status, null);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventType.PAYMENT_APPROVED, savedLog.getEventType());
    assertEquals(entityType, savedLog.getEntityType());
    assertEquals(entityId, savedLog.getEntityId());
    assertEquals(status, savedLog.getStatus());
  }

  /**
   * Test: Registrar un evento con detalles nulos.
   */
  @Test
  void logEvent_WithNullDetails_ShouldSaveAuditLogWithoutDetails() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logEvent("ORDER_CREATED", "ORDER", "order_123", "user_456", 
        "Order created successfully", null, EventStatus.SUCCESS, null);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertNull(savedLog.getDetails());
  }

  /**
   * Test: Registrar un evento exitoso.
   */
  @Test
  void logSuccessEvent_ShouldSaveAuditLogWithSuccessStatus() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logSuccessEvent("PAYMENT_APPROVED", "PAYMENT", "pay_123",
        "user_456", "Payment approved successfully", null);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventStatus.SUCCESS, savedLog.getStatus());
    assertEquals(EventType.PAYMENT_APPROVED, savedLog.getEventType());
  }

  /**
   * Test: Registrar un evento exitoso con detalles.
   */
  @Test
  void logSuccessEvent_WithDetails_ShouldIncludeDetailsInAuditLog() throws Exception {
    Object details = new Object();
    when(objectMapper.writeValueAsString(details)).thenReturn("{\"transactionId\": \"txn_123\"}");
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logSuccessEvent("ORDER_CREATED", "ORDER", "order_123",
        "customer_789", "Order created", details);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventStatus.SUCCESS, savedLog.getStatus());
  }

  /**
   * Test: Registrar un evento fallido.
   */
  @Test
  void logFailureEvent_ShouldSaveAuditLogWithFailureStatus() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logFailureEvent("PAYMENT_REJECTED", "PAYMENT", "pay_123",
        "user_456", "Payment rejected", "Insufficient funds", null);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventStatus.FAILURE, savedLog.getStatus());
    assertEquals("Insufficient funds", savedLog.getErrorMessage());
  }

  /**
   * Test: Registrar un evento fallido con detalles.
   */
  @Test
  void logFailureEvent_WithDetails_ShouldIncludeErrorAndDetails() throws Exception {
    Object details = new Object();
    when(objectMapper.writeValueAsString(details)).thenReturn("{\"attemptCount\": 3}");
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logFailureEvent("ORDER_CANCELLED", "ORDER", "order_123",
        "admin_user", "Order cancelled due to payment failure", "Payment declined after 3 attempts", details);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventStatus.FAILURE, savedLog.getStatus());
  }

  // ==================== logRetryEvent Tests ====================

  /**
   * Test: Registrar un evento de reintento.
   */
  @Test
  void logRetryEvent_ShouldSaveAuditLogWithRetryStatus() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    auditService.logRetryEvent("PAYMENT_ATTEMPTED", "PAYMENT", "pay_123",
        "user_456", "Payment retry attempt 2 of 3", null);

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(EventStatus.RETRY, savedLog.getStatus());
  }

  /**
   * Test: Registrar múltiples reintentos.
   */
  @Test
  void logRetryEvent_MultipleRetries_ShouldSaveMultipleAuditLogs() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(createTestAuditLog());

    for (int i = 1; i <= 3; i++) {
      auditService.logRetryEvent("PAYMENT_ATTEMPTED", "PAYMENT", "pay_123",
          "user_456", "Attempt " + i + " of 3", null);
    }

    verify(auditLogRepository, times(3)).save(any(AuditLog.class));
  }

  /**
   * Test: Obtener un registro de auditoría existente por UUID.
   */
  @Test
  void getAuditLogById_WhenExists_ShouldReturnAuditLog() {
    AuditLog auditLog = createTestAuditLog();
    when(auditLogRepository.findById(testId)).thenReturn(Optional.of(auditLog));

    AuditLog result = auditService.getAuditLogById(testId);

    assertNotNull(result);
    assertEquals(testId, result.getId());
    assertEquals(EventType.PAYMENT_APPROVED, result.getEventType());
    verify(auditLogRepository, times(1)).findById(testId);
  }

  /**
   * Test: Obtener un registro de auditoría que no existe por UUID.
   */
  @Test
  void getAuditLogById_WhenNotExists_ShouldReturnNull() {
    when(auditLogRepository.findById(testId)).thenReturn(Optional.empty());

    AuditLog result = auditService.getAuditLogById(testId);

    assertNull(result);
    verify(auditLogRepository, times(1)).findById(testId);
  }

  /**
   * Test: Obtener registros por ID de entidad.
   */
  @Test
  void getAuditLogsByEntityId_ShouldReturnListOfAuditLogs() {
    String entityId = "pay_123";
    List<AuditLog> auditLogs = Arrays.asList(
        createTestAuditLog(),
        createTestAuditLog()
    );
    when(auditLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId))
        .thenReturn(auditLogs);

    List<AuditLog> result = auditService.getAuditLogsByEntityId(entityId);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(auditLogRepository, times(1)).findByEntityIdOrderByCreatedAtDesc(entityId);
  }

  /**
   * Test: Obtener registros por ID de entidad cuando no hay resultados.
   */
  @Test
  void getAuditLogsByEntityId_WhenNoResults_ShouldReturnEmptyList() {
    when(auditLogRepository.findByEntityIdOrderByCreatedAtDesc("nonexistent"))
        .thenReturn(Arrays.asList());

    List<AuditLog> result = auditService.getAuditLogsByEntityId("nonexistent");

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  /**
   * Test: Obtener registros filtrados por tipo de evento.
   */
  @Test
  void getAuditLogsByEventType_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog(), createTestAuditLog()),
        pageable,
        2
    );
    when(auditLogRepository.findByEventTypeOrderByCreatedAtDesc(EventType.PAYMENT_APPROVED, pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByEventType(EventType.PAYMENT_APPROVED, pageable);

    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByEventTypeOrderByCreatedAtDesc(EventType.PAYMENT_APPROVED, pageable);
  }

  /**
   * Test: Obtener registros filtrados por tipo de entidad.
   */
  @Test
  void getAuditLogsByEntityType_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog()),
        pageable,
        1
    );
    when(auditLogRepository.findByEntityTypeOrderByCreatedAtDesc("PAYMENT", pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByEntityType("PAYMENT", pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByEntityTypeOrderByCreatedAtDesc("PAYMENT", pageable);
  }

  /**
   * Test: Obtener registros filtrados por ID de usuario.
   */
  @Test
  void getAuditLogsByUserId_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog(), createTestAuditLog()),
        pageable,
        2
    );
    when(auditLogRepository.findByUserIdOrderByCreatedAtDesc("user_456", pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByUserId("user_456", pageable);

    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByUserIdOrderByCreatedAtDesc("user_456", pageable);
  }

  /**
   * Test: Obtener registros filtrados por estado del evento.
   */
  @Test
  void getAuditLogsByStatus_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog()),
        pageable,
        1
    );
    when(auditLogRepository.findByStatusOrderByCreatedAtDesc(EventStatus.SUCCESS, pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByStatus(EventStatus.SUCCESS, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByStatusOrderByCreatedAtDesc(EventStatus.SUCCESS, pageable);
  }

  /**
   * Test: Obtener registros dentro de un rango de fechas.
   */
  @Test
  void getAuditLogsByDateRange_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    LocalDateTime startDate = now.minusHours(1);
    LocalDateTime endDate = now.plusHours(1);

    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog()),
        pageable,
        1
    );
    when(auditLogRepository.findByDateRangeOrderByCreatedAtDesc(startDate, endDate, pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByDateRange(startDate, endDate, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByDateRangeOrderByCreatedAtDesc(startDate, endDate, pageable);
  }

  /**
   * Test: Obtener registros con filtros combinados (EventType + Status).
   */
  @Test
  void getAuditLogsByEventTypeAndStatus_ShouldReturnPageOfAuditLogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> page = new PageImpl<>(
        Arrays.asList(createTestAuditLog()),
        pageable,
        1
    );
    when(auditLogRepository.findByEventTypeAndStatusOrderByCreatedAtDesc(
        EventType.PAYMENT_APPROVED, EventStatus.SUCCESS, pageable))
        .thenReturn(page);

    Page<AuditLog> result = auditService.getAuditLogsByEventTypeAndStatus(
        EventType.PAYMENT_APPROVED, EventStatus.SUCCESS, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(auditLogRepository, times(1)).findByEventTypeAndStatusOrderByCreatedAtDesc(
        EventType.PAYMENT_APPROVED, EventStatus.SUCCESS, pageable);
  }

  /**
   * Test: Obtener registros con filtros combinados cuando no hay resultados.
   */
  @Test
  void getAuditLogsByEventTypeAndStatus_WhenNoResults_ShouldReturnEmptyPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AuditLog> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

    when(auditLogRepository.findByEventTypeAndStatusOrderByCreatedAtDesc(
        EventType.PAYMENT_REJECTED, EventStatus.FAILURE, pageable))
        .thenReturn(emptyPage);

    Page<AuditLog> result = auditService.getAuditLogsByEventTypeAndStatus(
        EventType.PAYMENT_REJECTED, EventStatus.FAILURE, pageable);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
  }

  /**
   * Crea un AuditLog de prueba con valores predefinidos.
   */
  private AuditLog createTestAuditLog() {
    return AuditLog.builder()
        .id(testId)
        .eventType(EventType.PAYMENT_APPROVED)
        .entityType("PAYMENT")
        .entityId("pay_123")
        .userId("user_456")
        .description("Payment approved successfully")
        .details("{\"amount\": 100.0}")
        .status(EventStatus.SUCCESS)
        .errorMessage(null)
        .createdAt(now)
        .sourceIp("192.168.1.1")
        .build();
  }

}

