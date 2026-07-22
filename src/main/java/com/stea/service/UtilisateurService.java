package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Role;
import com.stea.entity.Utilisateur;
import com.stea.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommandeRepository commandeRepository;
    private final LivraisonRepository livraisonRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final NotificationRepository notificationRepository;

    public Page<UtilisateurResponse> search(String search, String statut, String role, int page, int size) {
        Utilisateur.StatutEnum statutEnum = statut != null ? Utilisateur.StatutEnum.valueOf(statut) : null;
        return utilisateurRepository.search(search, statutEnum, role,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(AuthService::mapToResponse);
    }

    public UtilisateurResponse getById(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        return AuthService.mapToResponse(user);
    }

    public UtilisateurResponse create(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        Role role = roleRepository.findByLibelle(Role.RoleEnum.valueOf(request.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Rôle invalide : " + request.getRole()));

        Utilisateur user = Utilisateur.builder()
                .identite(request.getIdentite())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(role)
                .statut(Utilisateur.StatutEnum.ACTIF)
                .build();

        return AuthService.mapToResponse(utilisateurRepository.save(user));
    }

    public UtilisateurResponse update(Long id, RegisterRequest request) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));

        if (!user.getEmail().equals(request.getEmail()) && utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        Role role = roleRepository.findByLibelle(Role.RoleEnum.valueOf(request.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Rôle invalide : " + request.getRole()));

        user.setIdentite(request.getIdentite());
        user.setEmail(request.getEmail());
        user.setRole(role);
        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        return AuthService.mapToResponse(utilisateurRepository.save(user));
    }

    public UtilisateurResponse updateStatut(Long id, String statut) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));

        user.setStatut(Utilisateur.StatutEnum.valueOf(statut));
        return AuthService.mapToResponse(utilisateurRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur introuvable : " + id);
        }
        commandeRepository.clearUtilisateurReferences(id);
        livraisonRepository.clearUtilisateurReferences(id);
        mouvementStockRepository.clearUtilisateurReferences(id);
        notificationRepository.clearDestinataireReferences(id);
        utilisateurRepository.deleteById(id);
    }
}
