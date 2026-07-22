package com.stea.controller;

import com.stea.entity.ServiceStea;
import com.stea.repository.ServiceSteaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceSteaController {

    private final ServiceSteaRepository serviceSteaRepository;

    @GetMapping
    public List<ServiceStea> list() {
        return serviceSteaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceStea> getById(@PathVariable Long id) {
        return serviceSteaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
