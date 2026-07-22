package com.stea.service;

import com.stea.entity.Notification;
import com.stea.entity.Utilisateur;
import com.stea.repository.NotificationRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Notification creerNotification(Long destinataireId, String canal, String message) {
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));

        Notification notification = Notification.builder()
                .destinataire(destinataire)
                .canal(Notification.Canal.valueOf(canal))
                .message(message)
                .build();

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsNonLues(Long destinataireId) {
        return notificationRepository.findByDestinataireIdAndLuFalse(destinataireId);
    }

    public long getNombreNonLues(Long destinataireId) {
        return notificationRepository.countByDestinataireIdAndLuFalse(destinataireId);
    }

    public Notification marquerCommeLue(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvee"));
        notification.setStatutLecture(Notification.StatutLecture.LUE);
        return notificationRepository.save(notification);
    }

    public List<Notification> listerToutes(Long destinataireId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getDestinataire() != null && n.getDestinataire().getId().equals(destinataireId))
                .toList();
    }
}
