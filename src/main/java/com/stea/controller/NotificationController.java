package com.stea.controller;

import com.stea.dto.NotificationResponse;
import com.stea.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{destinataireId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long destinataireId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(destinataireId));
    }

    @GetMapping("/all/{destinataireId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@PathVariable Long destinataireId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(destinataireId));
    }

    @GetMapping("/{destinataireId}/compter")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<Long> compterNonLues(@PathVariable Long destinataireId) {
        return ResponseEntity.ok(notificationService.getNombreNonLues(destinataireId));
    }

    @PatchMapping("/{id}/lire")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/tout-lire/{destinataireId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<Void> toutLire(@PathVariable Long destinataireId) {
        notificationService.lireToutes(destinataireId);
        return ResponseEntity.ok().build();
    }
}
