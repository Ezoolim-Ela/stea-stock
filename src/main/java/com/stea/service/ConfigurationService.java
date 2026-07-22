package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.ConfigurationSysteme;
import com.stea.repository.ConfigurationSystemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationSystemeRepository configRepository;

    public ConfigurationSystemeResponse get() {
        ConfigurationSysteme config = configRepository.findFirstBy()
                .orElseGet(() -> configRepository.save(new ConfigurationSysteme()));
        return toResponse(config);
    }

    public ConfigurationSystemeResponse update(ConfigurationSystemeRequest request) {
        ConfigurationSysteme config = configRepository.findFirstBy()
                .orElseGet(() -> configRepository.save(new ConfigurationSysteme()));

        if (request.getLangue() != null) config.setLangue(request.getLangue());
        if (request.getFuseauHoraire() != null) config.setFuseauHoraire(request.getFuseauHoraire());
        if (request.getUnitePoids() != null) config.setUnitePoids(request.getUnitePoids());
        if (request.getUniteVolume() != null) config.setUniteVolume(request.getUniteVolume());
        if (request.getSeuilAlerteGlobal() != null) config.setSeuilAlerteGlobal(request.getSeuilAlerteGlobal());
        if (request.getFrequenceAnalyse() != null) config.setFrequenceAnalyse(request.getFrequenceAnalyse());
        if (request.getNotifEmail() != null) config.setNotifEmail(request.getNotifEmail());
        if (request.getNotifInApp() != null) config.setNotifInApp(request.getNotifInApp());
        if (request.getNotifSms() != null) config.setNotifSms(request.getNotifSms());
        if (request.getEntrepriseNom() != null) config.setEntrepriseNom(request.getEntrepriseNom());
        if (request.getEntrepriseAdresse() != null) config.setEntrepriseAdresse(request.getEntrepriseAdresse());
        if (request.getEntrepriseEmail() != null) config.setEntrepriseEmail(request.getEntrepriseEmail());
        if (request.getEntrepriseTelephone() != null) config.setEntrepriseTelephone(request.getEntrepriseTelephone());
        if (request.getEntrepriseLogo() != null) config.setEntrepriseLogo(request.getEntrepriseLogo());

        return toResponse(configRepository.save(config));
    }

    private ConfigurationSystemeResponse toResponse(ConfigurationSysteme c) {
        ConfigurationSystemeResponse resp = new ConfigurationSystemeResponse();
        resp.setId(c.getId());
        resp.setLangue(c.getLangue());
        resp.setFuseauHoraire(c.getFuseauHoraire());
        resp.setUnitePoids(c.getUnitePoids());
        resp.setUniteVolume(c.getUniteVolume());
        resp.setSeuilAlerteGlobal(c.getSeuilAlerteGlobal());
        resp.setFrequenceAnalyse(c.getFrequenceAnalyse());
        resp.setNotifEmail(c.getNotifEmail());
        resp.setNotifInApp(c.getNotifInApp());
        resp.setNotifSms(c.getNotifSms());
        resp.setEntrepriseNom(c.getEntrepriseNom());
        resp.setEntrepriseAdresse(c.getEntrepriseAdresse());
        resp.setEntrepriseEmail(c.getEntrepriseEmail());
        resp.setEntrepriseTelephone(c.getEntrepriseTelephone());
        resp.setEntrepriseLogo(c.getEntrepriseLogo());
        return resp;
    }
}
