package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Commande;
import com.stea.entity.Livraison;
import com.stea.repository.CommandeRepository;
import com.stea.repository.LivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;

    public Page<LivraisonResponse> search(String search, String statut, int page, int size) {
        Livraison.StatutLivraison statutEnum = statut != null ? Livraison.StatutLivraison.valueOf(statut) : null;
        return livraisonRepository.search(search, statutEnum,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    public LivraisonResponse getById(Long id) {
        Livraison l = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison introuvable : " + id));
        return toResponse(l);
    }

    public LivraisonResponse create(LivraisonRequest request) {
        Commande commande = null;
        if (request.getCommandeId() != null) {
            commande = commandeRepository.findById(request.getCommandeId()).orElse(null);
        }

        Livraison l = Livraison.builder()
                .numeroBon(request.getNumeroBon())
                .datePrevue(request.getDatePrevue())
                .destinataire(request.getDestinataire())
                .contenu(request.getContenu())
                .vehicule(request.getVehicule())
                .nombreColis(request.getNombreColis() != null ? request.getNombreColis() : 0)
                .statut(request.getStatut() != null ?
                        Livraison.StatutLivraison.valueOf(request.getStatut()) :
                        Livraison.StatutLivraison.EN_ATTENTE)
                .commande(commande)
                .observation(request.getObservation())
                .bordereauTransmis(false)
                .factureEnvoyee(false)
                .build();

        return toResponse(livraisonRepository.save(l));
    }

    public LivraisonResponse updateStatut(Long id, String statut) {
        Livraison l = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison introuvable : " + id));

        Livraison.StatutLivraison nouveauStatut = Livraison.StatutLivraison.valueOf(statut);
        Livraison.StatutLivraison currentStatut = l.getStatut();

        if (!isValidTransition(currentStatut, nouveauStatut)) {
            throw new IllegalArgumentException(
                    "Transition invalide : " + currentStatut + " -> " + nouveauStatut);
        }

        l.setStatut(nouveauStatut);

        if (nouveauStatut == Livraison.StatutLivraison.LIVREE) {
            l.setDateReceptionEffective(LocalDate.now());
        }

        return toResponse(livraisonRepository.save(l));
    }

    @Transactional
    public LivraisonResponse transmettreBordereau(Long id) {
        Livraison l = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison introuvable : " + id));

        l.setBordereauTransmis(true);
        l.setStatut(Livraison.StatutLivraison.BORDEREAU_TRANSMIS);

        if (l.getCommande() != null) {
            BigDecimal totalCommande = l.getCommande().getTotal();
            BigDecimal montantPaye = l.getCommande().getMontantPaye();
            l.setMontantRestant(totalCommande.subtract(montantPaye != null ? montantPaye : BigDecimal.ZERO));
        }

        return toResponse(livraisonRepository.save(l));
    }

    @Transactional
    public LivraisonResponse envoyerFacture(Long id) {
        Livraison l = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison introuvable : " + id));

        l.setFactureEnvoyee(true);
        l.setStatut(Livraison.StatutLivraison.FACTURE_ENVOYEE);

        return toResponse(livraisonRepository.save(l));
    }

    private boolean isValidTransition(Livraison.StatutLivraison from, Livraison.StatutLivraison to) {
        return switch (from) {
            case EN_ATTENTE -> to == Livraison.StatutLivraison.EN_COURS || to == Livraison.StatutLivraison.ANNULEE;
            case EN_COURS -> to == Livraison.StatutLivraison.LIVREE || to == Livraison.StatutLivraison.RETOURNE || to == Livraison.StatutLivraison.ANNULEE;
            case LIVREE -> to == Livraison.StatutLivraison.BORDEREAU_TRANSMIS || to == Livraison.StatutLivraison.RETOURNE;
            case BORDEREAU_TRANSMIS -> to == Livraison.StatutLivraison.FACTURE_ENVOYEE;
            case FACTURE_ENVOYEE -> false;
            case RETOURNE -> false;
            case ANNULEE -> false;
        };
    }

    public List<LivraisonResponse> findAll() {
        return livraisonRepository.findAll().stream().map(this::toResponse).toList();
    }

    private LivraisonResponse toResponse(Livraison l) {
        LivraisonResponse resp = new LivraisonResponse();
        resp.setId(l.getId());
        resp.setNumeroBon(l.getNumeroBon());
        resp.setDatePrevue(l.getDatePrevue());
        resp.setDateReceptionEffective(l.getDateReceptionEffective());
        resp.setDestinataire(l.getDestinataire());
        resp.setContenu(l.getContenu());
        resp.setVehicule(l.getVehicule());
        resp.setNombreColis(l.getNombreColis());
        resp.setStatut(l.getStatut().name());
        resp.setBordereauTransmis(l.getBordereauTransmis());
        resp.setFactureEnvoyee(l.getFactureEnvoyee());
        resp.setMontantRestant(l.getMontantRestant());
        resp.setObservation(l.getObservation());
        resp.setCommandeId(l.getCommande() != null ? l.getCommande().getId() : null);
        resp.setCommandeNumero(l.getCommande() != null ? l.getCommande().getNumero() : null);
        resp.setCreatedAt(l.getCreatedAt());
        resp.setUpdatedAt(l.getUpdatedAt());
        return resp;
    }

    public void delete(Long id) {
        if (!livraisonRepository.existsById(id)) {
            throw new IllegalArgumentException("Livraison introuvable : " + id);
        }
        livraisonRepository.deleteById(id);
    }
}
