package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Role;
import com.stea.entity.Utilisateur;
import com.stea.repository.RoleRepository;
import com.stea.repository.UtilisateurRepository;
import com.stea.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        if (user.getStatut() == Utilisateur.StatutEnum.EN_ATTENTE) {
            throw new IllegalArgumentException("Votre compte est en attente de validation par un administrateur");
        }
        if (user.getStatut() == Utilisateur.StatutEnum.SUSPENDU) {
            throw new IllegalArgumentException("Votre compte a ete suspendu. Contactez un administrateur");
        }
        if (user.getStatut() == Utilisateur.StatutEnum.INACTIF) {
            throw new IllegalArgumentException("Votre compte est desactive. Contactez un administrateur");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        String jwt = jwtProvider.generateToken(authentication);

        user.setDateDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(user);

        return new LoginResponse(jwt, "Bearer", mapToResponse(user));
    }

    public UtilisateurResponse register(RegisterRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe deja avec cet email");
        }

        Role role;
        if (request.getRole() != null) {
            role = roleRepository.findByLibelle(Role.RoleEnum.valueOf(request.getRole()))
                    .orElseThrow(() -> new IllegalArgumentException("Role invalide : " + request.getRole()));
        } else {
            role = roleRepository.findByLibelle(Role.RoleEnum.OPERATEUR_STOCK)
                    .orElseThrow(() -> new IllegalArgumentException("Role par defaut introuvable"));
        }

        Utilisateur user = Utilisateur.builder()
                .identite(request.getIdentite())
                .email(request.getEmail())
                .motDePasse(request.getMotDePasse())
                .role(role)
                .statut(Utilisateur.StatutEnum.EN_ATTENTE)
                .build();

        return mapToResponse(utilisateurRepository.save(user));
    }

    public List<UtilisateurResponse> getPendingInscriptions() {
        return utilisateurRepository.findByStatut(Utilisateur.StatutEnum.EN_ATTENTE)
                .stream().map(AuthService::mapToResponse).collect(Collectors.toList());
    }

    public UtilisateurResponse approuverInscription(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        if (user.getStatut() != Utilisateur.StatutEnum.EN_ATTENTE) {
            throw new IllegalArgumentException("Cet utilisateur n'est pas en attente de validation");
        }
        user.setStatut(Utilisateur.StatutEnum.ACTIF);
        return mapToResponse(utilisateurRepository.save(user));
    }

    public UtilisateurResponse rejeterInscription(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        if (user.getStatut() != Utilisateur.StatutEnum.EN_ATTENTE) {
            throw new IllegalArgumentException("Cet utilisateur n'est pas en attente de validation");
        }
        utilisateurRepository.delete(user);
        return mapToResponse(user);
    }

    public static UtilisateurResponse mapToResponse(Utilisateur user) {
        UtilisateurResponse resp = new UtilisateurResponse();
        resp.setId(user.getId());
        resp.setPhoto(user.getPhoto());
        resp.setIdentite(user.getIdentite());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole().getLibelle().name());
        resp.setStatut(user.getStatut().name());
        resp.setDateDerniereConnexion(user.getDateDerniereConnexion());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }
}
