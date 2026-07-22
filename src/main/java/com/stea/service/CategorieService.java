package com.stea.service;

import com.stea.dto.CategorieRequest;
import com.stea.entity.Categorie;
import com.stea.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public Categorie creerCategorie(CategorieRequest request) {
        if (categorieRepository.existsByNom(request.getNom())) {
            throw new RuntimeException("La categorie '" + request.getNom() + "' existe deja");
        }
        Categorie categorie = Categorie.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .dateCreation(LocalDateTime.now())
                .build();
        return categorieRepository.save(categorie);
    }

    public List<Categorie> listerCategories() {
        return categorieRepository.findAll();
    }

    public Categorie getCategorie(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorie non trouvee"));
    }

    public Categorie getCategorieParNom(String nom) {
        return categorieRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("Categorie non trouvee"));
    }

    public Categorie modifierCategorie(Long id, CategorieRequest request) {
        Categorie categorie = getCategorie(id);
        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());
        return categorieRepository.save(categorie);
    }

    public void supprimerCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
