package com.stea.service;

import com.stea.entity.Entrepot;
import com.stea.entity.Emplacement;
import com.stea.repository.EntrepotRepository;
import com.stea.repository.EmplacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntrepotService {

    private final EntrepotRepository entrepotRepository;
    private final EmplacementRepository emplacementRepository;

    // === ENTREPOTS ===

    public List<Entrepot> lister() {
        return entrepotRepository.findAll();
    }

    public List<Entrepot> listerActifs() {
        return entrepotRepository.findByActifTrue();
    }

    public Entrepot get(Long id) {
        return entrepotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepot non trouve"));
    }

    public Entrepot getByCode(String code) {
        return entrepotRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Entrepot non trouve"));
    }

    @Transactional
    public Entrepot creer(Entrepot entrepot) {
        if (entrepotRepository.existsByCode(entrepot.getCode())) {
            throw new RuntimeException("Code entrepot deja utilise: " + entrepot.getCode());
        }
        return entrepotRepository.save(entrepot);
    }

    @Transactional
    public Entrepot modifier(Long id, Entrepot updates) {
        Entrepot e = get(id);
        e.setNom(updates.getNom());
        e.setAdresse(updates.getAdresse());
        e.setVille(updates.getVille());
        e.setResponsable(updates.getResponsable());
        e.setTelephone(updates.getTelephone());
        e.setDescription(updates.getDescription());
        e.setActif(updates.isActif());
        return entrepotRepository.save(e);
    }

    @Transactional
    public void supprimer(Long id) {
        entrepotRepository.deleteById(id);
    }

    // === EMPLACEMENTS ===

    public List<Emplacement> listerEmplacements(Long entrepotId) {
        return emplacementRepository.findByEntrepotId(entrepotId);
    }

    public List<Emplacement> listerEmplacementsActifs(Long entrepotId) {
        return emplacementRepository.findByEntrepotIdAndActifTrue(entrepotId);
    }

    public List<Emplacement> listerEmplacementsDisponibles(Long entrepotId) {
        return emplacementRepository.findDisponibles(entrepotId);
    }

    public Emplacement getEmplacement(Long id) {
        return emplacementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement non trouve"));
    }

    @Transactional
    public Emplacement creerEmplacement(Emplacement emplacement) {
        Long entrepotId = emplacement.getEntrepot().getId();
        if (emplacementRepository.existsByCodeAndEntrepotId(emplacement.getCode(), entrepotId)) {
            throw new RuntimeException("Code emplacement deja utilise dans cet entrepot");
        }
        emplacement.setEntrepot(entrepotRepository.findById(entrepotId).orElseThrow());
        return emplacementRepository.save(emplacement);
    }

    @Transactional
    public Emplacement modifierEmplacement(Long id, Emplacement updates) {
        Emplacement e = getEmplacement(id);
        e.setNom(updates.getNom());
        e.setZone(updates.getZone());
        e.setRayon(updates.getRayon());
        e.setNiveau(updates.getNiveau());
        e.setCapaciteMax(updates.getCapaciteMax());
        e.setActif(updates.isActif());
        return emplacementRepository.save(e);
    }

    @Transactional
    public void supprimerEmplacement(Long id) {
        emplacementRepository.deleteById(id);
    }

    public long compterEmplacements(Long entrepotId) {
        return emplacementRepository.findByEntrepotId(entrepotId).size();
    }
}
