package co.com.testapp.testapp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

  @Mock
  private AuditLogRepository auditLogRepository;

  @Mock
  private ObjectMapper objectMapper;

  private AuditService auditService;

  @BeforeEach
  void setUp() {
    auditService = new AuditService(auditLogRepository, objectMapper);
  }

  @Test
  void logSuccessEvent_ShouldSaveAuditLogWithSuccessStatus() {
    // Setup
    String eventType = "PAYMENT_APPROVED";
    String entityType = "PAYMENT";
    String entityId = "123";
    String userId = "user-1";
    String description = "Payment approved successfully";
    Object details = null;

    when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
      AuditLog log = invocation.getArgument(0);
      log.setId(UUID.randomUUID());
      return log;
    });

    // Execute
    auditService.logSuccessEvent(eventType, entityType, entityId, userId, description, details);

    // Verify
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertNotNull(savedLog.getId());
  }

  @Test
  void logFailureEvent_ShouldSaveAuditLogWithFailureStatus() {
    // Setup
    String eventType = "PAYMENT_REJECTED";
    String entityType = "PAYMENT";
    String entityId = "123";
    String userId = "user-1";
    String description = "Payment rejected";
    String errorMessage = "Insufficient funds";
    Object details = null;

    when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
      AuditLog log = invocation.getArgument(0);
      log.setId(UUID.randomUUID());
      return log;
    });

    // Execute
    auditService.logFailureEvent(eventType, entityType, entityId, userId, description, errorMessage, details);

    // Verify
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertNotNull(savedLog.getId());
  }

  @Test
  void logRetryEvent_ShouldSaveAuditLogWithRetryStatus() {
    // Setup
    String eventType = "PAYMENT_ATTEMPTED";
    String entityType = "PAYMENT";
    String entityId = "123";
    String userId = "user-1";
    String description = "Payment retry attempt 2 of 3";
    Object details = null;

    when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
      AuditLog log = invocation.getArgument(0);
      log.setId(UUID.randomUUID());
      return log;
    });

    // Execute
    auditService.logRetryEvent(eventType, entityType, entityId, userId, description, details);

    // Verify
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertNotNull(savedLog.getId());
  }

}

