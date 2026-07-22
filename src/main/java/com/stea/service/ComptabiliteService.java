package com.stea.service;

import com.stea.entity.Commande;
import com.stea.entity.Livraison;
import com.stea.entity.Utilisateur;
import com.stea.repository.CommandeRepository;
import com.stea.repository.LivraisonRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComptabiliteService {

    private final CommandeRepository commandeRepository;
    private final LivraisonRepository livraisonRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Commande> getCommandesNonPayees() {
        return commandeRepository.findByStatutPaiement(Commande.StatutPaiement.NON_PAYE);
    }

    public List<Commande> getCommandesPayeesPartiellement() {
        return commandeRepository.findByStatutPaiement(Commande.StatutPaiement.PARTIEL);
    }

    public List<Commande> getCommandesPayees() {
        return commandeRepository.findByStatutPaiement(Commande.StatutPaiement.PAYE);
    }

    public List<Livraison> getLivraisonsEnAttentePaiement() {
        return livraisonRepository.findByStatutPaiement(Livraison.StatutPaiementLivraison.EN_ATTENTE_PAIEMENT);
    }

    @Transactional
    public Commande validerPaiementCommande(Long commandeId, Long comptableId, String observation) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvee"));
        Utilisateur comptable = utilisateurRepository.findById(comptableId)
                .orElseThrow(() -> new RuntimeException("Comptable non trouve"));

        commande.setStatutPaiement(Commande.StatutPaiement.PAYE);
        commande.setComptableValidateur(comptable);
        commande.setObservation(observation);
        commande.setDateModification(LocalDateTime.now());

        return commandeRepository.save(commande);
    }

    @Transactional
    public Livraison validerPaiementLivraison(Long livraisonId, Long comptableId, String observation) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvee"));
        Utilisateur comptable = utilisateurRepository.findById(comptableId)
                .orElseThrow(() -> new RuntimeException("Comptable non trouve"));

        livraison.setStatutPaiement(Livraison.StatutPaiementLivraison.PAIEMENT_VALIDE);
        livraison.setComptableValidateur(comptable);
        livraison.setDateValidationPaiement(LocalDateTime.now());
        livraison.setObservation(observation);

        return livraisonRepository.save(livraison);
    }

    @Transactional
    public Livraison refuserPaiementLivraison(Long livraisonId, Long comptableId, String observation) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvee"));
        Utilisateur comptable = utilisateurRepository.findById(comptableId)
                .orElseThrow(() -> new RuntimeException("Comptable non trouve"));

        livraison.setStatutPaiement(Livraison.StatutPaiementLivraison.PAIEMENT_REFUSE);
        livraison.setComptableValidateur(comptable);
        livraison.setDateValidationPaiement(LocalDateTime.now());
        livraison.setObservation(observation);

        return livraisonRepository.save(livraison);
    }
}
