package com.stea.service;

import com.stea.entity.AuditLog;
import com.stea.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog log(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public Page<AuditLog> search(String email, String action, String entite, int page, int size) {
        return auditLogRepository.search(
            email == null || email.isBlank() ? null : email,
            action == null || action.isBlank() ? null : action,
            entite == null || entite.isBlank() ? null : entite,
            PageRequest.of(page, size)
        );
    }

    public Page<AuditLog> getByUserId(Long userId, int page, int size) {
        return auditLogRepository.findByUtilisateurIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public Page<AuditLog> getAll(int page, int size) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }
}