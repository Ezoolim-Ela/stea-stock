package com.stea.repository;

import com.stea.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%',:email,'%'))) " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:entite IS NULL OR a.entite = :entite) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> search(@Param("email") String email, @Param("action") String action,
                          @Param("entite") String entite, Pageable pageable);

    Page<AuditLog> findByUtilisateurIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}