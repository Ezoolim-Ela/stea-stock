package com.stea.controller;

import com.stea.entity.Notification;
import com.stea.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{destinataireId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(@PathVariable Long destinataireId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(destinataireId).stream()
                .map(n -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", n.getId());
                    map.put("message", n.getMessage());
                    map.put("canal", n.getCanal().name());
                    map.put("date", n.getCreatedAt());
                    map.put("lu", n.getStatutLecture() == Notification.StatutLecture.LUE);
                    return map;
                })
                .toList());
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
}
