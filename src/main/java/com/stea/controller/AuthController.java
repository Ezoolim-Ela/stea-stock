package com.stea.controller;

import com.stea.dto.*;
import com.stea.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurResponse> inscription(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/inscriptions")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<UtilisateurResponse>> getPendingInscriptions() {
        return ResponseEntity.ok(authService.getPendingInscriptions());
    }

    @PatchMapping("/inscriptions/{id}/approuver")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UtilisateurResponse> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(authService.approuverInscription(id));
    }

    @PatchMapping("/inscriptions/{id}/rejeter")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UtilisateurResponse> rejeter(@PathVariable Long id) {
        return ResponseEntity.ok(authService.rejeterInscription(id));
    }
}
