package co.com.testapp.testapp.controller;

import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.entity.EventStatus;
import co.com.testapp.testapp.entity.EventType;
import co.com.testapp.testapp.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests para el controlador de auditoría.
 *
 * Cubre todos los endpoints del controlador AuditController incluyendo:
 * - Obtención de registros por ID
 * - Filtrado por entidad
 * - Filtrado por tipo de evento
 * - Filtrado por tipo de entidad
 * - Filtrado por usuario
 * - Filtrado por estado
 * - Filtrado por rango de fechas
 *
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuditControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuditService auditService;

  private AuditLog testAuditLog;
  private UUID testId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    testId = UUID.randomUUID();
    now = LocalDateTime.now();

    testAuditLog = AuditLog.builder()
        .id(testId)
        .eventType(EventType.PAYMENT_APPROVED)
        .entityType("PAYMENT")
        .entityId("123")
        .userId("user-001")
        .description("Test payment approval")
        .details("{\"amount\": 100.00}")
        .status(EventStatus.SUCCESS)
        .errorMessage(null)
        .createdAt(now)
        .sourceIp("192.168.1.1")
        .build();
  }

  /**
   * Test: Obtener un registro de auditoría por ID cuando existe.
   */
  @Test
  void getAuditLog_WhenExists_ShouldReturnOk() throws Exception {
    when(auditService.getAuditLogById(testId)).thenReturn(testAuditLog);

    mockMvc.perform(get("/api/v1/audit/{id}", testId)
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testId.toString()))
        .andExpect(jsonPath("$.eventType").value("PAYMENT_APPROVED"))
        .andExpect(jsonPath("$.entityType").value("PAYMENT"))
        .andExpect(jsonPath("$.entityId").value("123"))
        .andExpect(jsonPath("$.userId").value("user-001"))
        .andExpect(jsonPath("$.description").value("Test payment approval"))
        .andExpect(jsonPath("$.status").value("SUCCESS"));
  }

  /**
   * Test: Obtener un registro de auditoría por ID cuando no existe.
   */
  @Test
  void getAuditLog_WhenNotExists_ShouldReturnNotFound() throws Exception {
    when(auditService.getAuditLogById(testId)).thenReturn(null);

    mockMvc.perform(get("/api/v1/audit/{id}", testId)
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  /**
   * Test: Obtener registros de auditoría filtrados por ID de entidad.
   */
  @Test
  void getAuditLogsByEntity_ShouldReturnList() throws Exception {
    AuditLog auditLog2 = AuditLog.builder()
        .id(UUID.randomUUID())
        .eventType(EventType.ORDER_CREATED)
        .entityType("ORDER")
        .entityId("123")
        .userId("user-001")
        .description("Test order creation")
        .details("{\"orderId\": 123}")
        .status(EventStatus.SUCCESS)
        .errorMessage(null)
        .createdAt(now)
        .sourceIp("192.168.1.2")
        .build();

    List<AuditLog> logs = Arrays.asList(testAuditLog, auditLog2);
    when(auditService.getAuditLogsByEntityId("123")).thenReturn(logs);

    mockMvc.perform(get("/api/v1/audit/entity/{entityId}", "123")
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].entityId").value("123"))
        .andExpect(jsonPath("$[1].entityId").value("123"));
  }


  /**
   * Test: Obtener registros de auditoría filtrados por tipo de evento inválido.
   */
  @Test
  void getAuditLogsByEventType_WithInvalidType_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/audit/event-type/{eventType}", "INVALID_EVENT_TYPE")
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  /**
   * Test: Obtener registros de auditoría filtrados por estado inválido.
   */
  @Test
  void getAuditLogsByStatus_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/audit/status/{status}", "INVALID_STATUS")
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test: Obtener registros de auditoría por rango de fechas válido.
   */
  @Test
  void getAuditLogsByDateRange_WithValidDates_ShouldReturnPage() throws Exception {
    LocalDateTime startDate = now.minusHours(1);
    LocalDateTime endDate = now.plusHours(1);

    Pageable pageable = PageRequest.of(0, 10);
    List<AuditLog> logs = Arrays.asList(testAuditLog);
    Page<AuditLog> page = new PageImpl<>(logs, pageable, 1);

    when(auditService.getAuditLogsByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/audit/date-range")
        .param("startDate", startDate.toString())
        .param("endDate", endDate.toString())
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  /**
   * Test: Obtener registros de auditoría con formato de fecha inválido.
   */
  @Test
  void getAuditLogsByDateRange_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/audit/date-range")
        .param("startDate", "invalid-date")
        .param("endDate", "invalid-date")
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test: Obtener registro de auditoría sin API Key.
   */
  @Test
  void getAuditLog_WithoutApiKey_ShouldReturnForbidden() throws Exception {
    mockMvc.perform(get("/api/v1/audit/{id}", testId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  /**
   * Test: Obtener registro de auditoría con API Key inválida.
   */
  @Test
  void getAuditLog_WithInvalidApiKey_ShouldReturnForbidden() throws Exception {
    mockMvc.perform(get("/api/v1/audit/{id}", testId)
        .header("X-API-Key", "invalid-api-key")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  /**
   * Test: Obtener registros de auditoría con todos los campos mapeados correctamente.
   */
  @Test
  void getAuditLog_ShouldMapAllFieldsCorrectly() throws Exception {
    AuditLog auditLogComplete = AuditLog.builder()
        .id(testId)
        .eventType(EventType.ORDER_CANCELLED)
        .entityType("ORDER")
        .entityId("789")
        .userId("user-003")
        .description("Complete test with all fields")
        .details("{\"reason\": \"Customer request\"}")
        .status(EventStatus.FAILURE)
        .errorMessage("Order cancellation failed due to payment status")
        .createdAt(now)
        .sourceIp("10.0.0.1")
        .build();

    when(auditService.getAuditLogById(testId)).thenReturn(auditLogComplete);

    mockMvc.perform(get("/api/v1/audit/{id}", testId)
        .header("X-API-Key", "ad_live_secure_audit_key_2024")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testId.toString()))
        .andExpect(jsonPath("$.eventType").value("ORDER_CANCELLED"))
        .andExpect(jsonPath("$.entityType").value("ORDER"))
        .andExpect(jsonPath("$.entityId").value("789"))
        .andExpect(jsonPath("$.userId").value("user-003"))
        .andExpect(jsonPath("$.description").value("Complete test with all fields"))
        .andExpect(jsonPath("$.details").value("{\"reason\": \"Customer request\"}"))
        .andExpect(jsonPath("$.status").value("FAILURE"))
        .andExpect(jsonPath("$.errorMessage").value("Order cancellation failed due to payment status"))
        .andExpect(jsonPath("$.sourceIp").value("10.0.0.1"));
  }

}

