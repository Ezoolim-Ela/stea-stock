package com.stea.service;

import com.stea.dto.MessageRequest;
import com.stea.dto.MessageResponse;
import com.stea.entity.Message;
import com.stea.entity.Notification;
import com.stea.entity.Utilisateur;
import com.stea.repository.MessageRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    public Long getUserIdByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"))
                .getId();
    }

    public MessageResponse envoyer(Long expediteurId, MessageRequest request) {
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new IllegalArgumentException("Expediteur introuvable"));
        Utilisateur destinataire = utilisateurRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable"));

        Message msg = Message.builder()
                .expediteur(expediteur)
                .destinataire(destinataire)
                .contenu(request.getContenu())
                .sujet(request.getSujet())
                .build();
        messageRepository.save(msg);

        notificationService.creerNotification(request.getDestinataireId(), "IN_APP",
                "Nouveau message de " + expediteur.getIdentite() + ": " + truncate(request.getContenu(), 100),
                Notification.TypeNotification.UTILISATEUR);

        return toResponse(msg);
    }

    public Page<MessageResponse> inbox(Long userId, int page, int size) {
        return messageRepository.findByDestinataireIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public Page<MessageResponse> sent(Long userId, int page, int size) {
        return messageRepository.findByExpediteurIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public List<MessageResponse> conversation(Long userId, Long otherUserId) {
        return messageRepository.findConversation(userId, otherUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void marquerCommeLu(Long messageId, Long userId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable"));
        if (msg.getDestinataire().getId().equals(userId)) {
            msg.setLu(true);
            messageRepository.save(msg);
        }
    }

    public long countNonLus(Long userId) {
        return messageRepository.countByDestinataireIdAndLuFalse(userId);
    }

    private MessageResponse toResponse(Message m) {
        MessageResponse resp = new MessageResponse();
        resp.setId(m.getId());
        resp.setExpediteurId(m.getExpediteur().getId());
        resp.setExpediteurNom(m.getExpediteur().getIdentite());
        resp.setDestinataireId(m.getDestinataire().getId());
        resp.setDestinataireNom(m.getDestinataire().getIdentite());
        resp.setContenu(m.getContenu());
        resp.setSujet(m.getSujet());
        resp.setLu(m.getLu());
        resp.setDate(m.getCreatedAt());
        return resp;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
