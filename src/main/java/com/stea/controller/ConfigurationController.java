package com.stea.controller;

import com.stea.dto.*;
import com.stea.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuration")
@CrossOrigin
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ConfigurationSystemeResponse> get() {
        return ResponseEntity.ok(configurationService.get());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ConfigurationSystemeResponse> update(@RequestBody ConfigurationSystemeRequest request) {
        return ResponseEntity.ok(configurationService.update(request));
    }
}
