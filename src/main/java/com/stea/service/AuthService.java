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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        String jwt = jwtProvider.generateToken(authentication);

        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        user.setDateDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(user);

        return new LoginResponse(jwt, "Bearer", mapToResponse(user));
    }

    public UtilisateurResponse register(RegisterRequest request) {
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

        return mapToResponse(utilisateurRepository.save(user));
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
