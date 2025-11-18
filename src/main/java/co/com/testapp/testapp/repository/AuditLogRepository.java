package co.com.testapp.testapp.repository;

import co.com.testapp.testapp.entity.AuditLog;
import co.com.testapp.testapp.entity.EventStatus;
import co.com.testapp.testapp.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

  List<AuditLog> findByEntityIdOrderByCreatedAtDesc(String entityId);

  Page<AuditLog> findByEventTypeOrderByCreatedAtDesc(EventType eventType, Pageable pageable);

  Page<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);

  Page<AuditLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

  Page<AuditLog> findByStatusOrderByCreatedAtDesc(EventStatus status, Pageable pageable);

  @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
  Page<AuditLog> findByDateRangeOrderByCreatedAtDesc(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);

  @Query("SELECT a FROM AuditLog a WHERE a.eventType = :eventType AND a.status = :status ORDER BY a.createdAt DESC")
  Page<AuditLog> findByEventTypeAndStatusOrderByCreatedAtDesc(@Param("eventType") EventType eventType,
                                                               @Param("status") EventStatus status,
                                                               Pageable pageable);

}

