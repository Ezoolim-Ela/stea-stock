package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Fournisseur;
import com.stea.repository.ArticleRepository;
import com.stea.repository.CommandeRepository;
import com.stea.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;
    private final CommandeRepository commandeRepository;

    public Page<FournisseurResponse> search(String search, String statut, int page, int size) {
        Fournisseur.StatutFournisseur statutEnum = statut != null ? Fournisseur.StatutFournisseur.valueOf(statut) : null;
        return fournisseurRepository.search(search, statutEnum,
                        PageRequest.of(page, size, Sort.by("designation").ascending()))
                .map(this::toResponse);
    }

    public FournisseurResponse getById(Long id) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable : " + id));
        return toResponse(f);
    }

    public FournisseurResponse create(FournisseurRequest request) {
        if (fournisseurRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Le code fournisseur existe déjà : " + request.getCode());
        }

        Fournisseur f = Fournisseur.builder()
                .code(request.getCode())
                .designation(request.getDesignation())
                .categorieProduits(request.getCategorieProduits())
                .contactPrincipal(request.getContactPrincipal())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .pays(request.getPays())
                .ville(request.getVille())
                .statut(request.getStatut() != null ?
                        Fournisseur.StatutFournisseur.valueOf(request.getStatut()) :
                        Fournisseur.StatutFournisseur.ACTIF)
                .build();

        return toResponse(fournisseurRepository.save(f));
    }

    public FournisseurResponse update(Long id, FournisseurRequest request) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable : " + id));

        f.setDesignation(request.getDesignation());
        f.setCategorieProduits(request.getCategorieProduits());
        f.setContactPrincipal(request.getContactPrincipal());
        f.setTelephone(request.getTelephone());
        f.setEmail(request.getEmail());
        f.setPays(request.getPays());
        f.setVille(request.getVille());
        if (request.getStatut() != null) {
            f.setStatut(Fournisseur.StatutFournisseur.valueOf(request.getStatut()));
        }

        return toResponse(fournisseurRepository.save(f));
    }

    public FournisseurResponse updateStatut(Long id, String statut) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable : " + id));
        f.setStatut(Fournisseur.StatutFournisseur.valueOf(statut));
        return toResponse(fournisseurRepository.save(f));
    }

    private FournisseurResponse toResponse(Fournisseur f) {
        FournisseurResponse resp = new FournisseurResponse();
        resp.setId(f.getId());
        resp.setCode(f.getCode());
        resp.setDesignation(f.getDesignation());
        resp.setCategorieProduits(f.getCategorieProduits());
        resp.setContactPrincipal(f.getContactPrincipal());
        resp.setTelephone(f.getTelephone());
        resp.setEmail(f.getEmail());
        resp.setPays(f.getPays());
        resp.setVille(f.getVille());
        resp.setDerniereCommande(f.getDerniereCommande());
        resp.setStatut(f.getStatut().name());
        resp.setCreatedAt(f.getCreatedAt());
        resp.setUpdatedAt(f.getUpdatedAt());
        return resp;
    }

    @Transactional
    public void delete(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new IllegalArgumentException("Fournisseur introuvable : " + id);
        }
        articleRepository.clearFournisseurReferences(id);
        commandeRepository.clearFournisseurReferences(id);
        fournisseurRepository.deleteById(id);
    }
}
