package com.stea.service;

import com.stea.dto.NotificationResponse;
import com.stea.entity.Notification;
import com.stea.entity.Utilisateur;
import com.stea.repository.NotificationRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Notification creerNotification(Long destinataireId, String canal, String message, Notification.TypeNotification type) {
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));

        Notification notification = Notification.builder()
                .destinataire(destinataire)
                .canal(Notification.Canal.valueOf(canal))
                .message(message)
                .type(type != null ? type : Notification.TypeNotification.SYSTEME)
                .build();

        return notificationRepository.save(notification);
    }

    public Notification creerNotification(Long destinataireId, String canal, String message) {
        return creerNotification(destinataireId, canal, message, Notification.TypeNotification.SYSTEME);
    }

    public List<NotificationResponse> getNotificationsNonLues(Long destinataireId) {
        return notificationRepository.findByDestinataireIdAndLuFalse(destinataireId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<NotificationResponse> getAllNotifications(Long destinataireId) {
        return notificationRepository.findByDestinataireIdOrderByCreatedAtDesc(destinataireId,
                org.springframework.data.domain.PageRequest.of(0, 100))
                .getContent()
                .stream().map(this::toResponse).collect(Collectors.toList());
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

    @Transactional
    public void lireToutes(Long destinataireId) {
        List<Notification> nonLues = notificationRepository.findByDestinataireIdAndLuFalse(destinataireId);
        for (Notification n : nonLues) {
            n.setStatutLecture(Notification.StatutLecture.LUE);
        }
        notificationRepository.saveAll(nonLues);
    }

    public List<Notification> listerToutes(Long destinataireId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getDestinataire() != null && n.getDestinataire().getId().equals(destinataireId))
                .toList();
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(n.getId());
        resp.setType(n.getType() != null ? n.getType().name() : Notification.TypeNotification.SYSTEME.name());
        resp.setMessage(n.getMessage());
        resp.setCanal(n.getCanal().name());
        resp.setLu(n.getStatutLecture() == Notification.StatutLecture.LUE);
        resp.setDate(n.getCreatedAt());
        return resp;
    }
}
