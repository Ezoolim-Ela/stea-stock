package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Article;
import com.stea.entity.Commande;
import com.stea.entity.LigneCommande;
import com.stea.entity.Notification;
import com.stea.entity.Role;
import com.stea.entity.Utilisateur;
import com.stea.repository.ArticleRepository;
import com.stea.repository.CommandeRepository;
import com.stea.repository.LivraisonRepository;
import com.stea.repository.NotificationRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final LivraisonRepository livraisonRepository;
    private final NotificationRepository notificationRepository;

    public Page<CommandeResponse> search(String search, String typeFlux, String statut, int page, int size) {
        Commande.TypeFlux typeEnum = typeFlux != null ? Commande.TypeFlux.valueOf(typeFlux) : null;
        Commande.StatutCommande statutEnum = statut != null ? Commande.StatutCommande.valueOf(statut) : null;
        return commandeRepository.search(search, typeEnum, statutEnum,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    public CommandeResponse getById(Long id) {
        Commande c = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id));
        return toResponse(c);
    }

    @Transactional
    public CommandeResponse create(CommandeRequest request, Long utilisateurId) {
        String numero = "CMD" + System.currentTimeMillis();

        Utilisateur utilisateur = utilisateurId != null ?
                utilisateurRepository.findById(utilisateurId).orElse(null) : null;

        Commande commande = Commande.builder()
                .numero(numero)
                .client(request.getClient())
                .typeFlux(Commande.TypeFlux.valueOf(request.getTypeFlux()))
                .total(BigDecimal.ZERO)
                .statut(Commande.StatutCommande.BROUILLON)
                .statutPaiement(Commande.StatutPaiement.NON_PAYE)
                .utilisateur(utilisateur)
                .lignes(new ArrayList<>())
                .build();

        if (request.getLignes() != null && !request.getLignes().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (LigneCommandeRequest lr : request.getLignes()) {
                Article article = articleRepository.findById(lr.getArticleId())
                        .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + lr.getArticleId()));

                LigneCommande ligne = LigneCommande.builder()
                        .commande(commande)
                        .article(article)
                        .quantite(lr.getQuantite())
                        .prixUnitaire(lr.getPrixUnitaire())
                        .sousTotal(lr.getPrixUnitaire().multiply(BigDecimal.valueOf(lr.getQuantite())))
                        .build();
                commande.getLignes().add(ligne);
                total = total.add(ligne.getSousTotal());
            }
            commande.setTotal(total);
        }

        return toResponse(commandeRepository.save(commande));
    }

    @Transactional
    public CommandeResponse addLigne(Long commandeId, LigneCommandeRequest request) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + commandeId));

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + request.getArticleId()));

        LigneCommande ligne = LigneCommande.builder()
                .commande(commande)
                .article(article)
                .quantite(request.getQuantite())
                .prixUnitaire(request.getPrixUnitaire())
                .sousTotal(request.getPrixUnitaire().multiply(BigDecimal.valueOf(request.getQuantite())))
                .build();

        commande.getLignes().add(ligne);
        commande.setTotal(commande.getTotal().add(ligne.getSousTotal()));

        return toResponse(commandeRepository.save(commande));
    }

    public CommandeResponse updateStatut(Long id, String statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id));
        commande.setStatut(Commande.StatutCommande.valueOf(statut));
        return toResponse(commandeRepository.save(commande));
    }

    @Transactional
    public CommandeResponse enregistrerPaiement(Long id, PaiementRequest request) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id));

        BigDecimal pourcentage = request.getPourcentagePaiement();
        if (pourcentage == null || pourcentage.compareTo(BigDecimal.ZERO) < 0 || pourcentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Le pourcentage doit etre entre 0 et 100");
        }

        BigDecimal montantPaye = commande.getTotal()
                .multiply(pourcentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        commande.setPourcentagePaiement(pourcentage);
        commande.setMontantPaye(montantPaye);
        commande.setDatePaiement(LocalDateTime.now());
        commande.setObservation(request.getObservation());

        if (request.getDelaiLivraison() != null) {
            commande.setDelaiLivraison(request.getDelaiLivraison());
        }

        if (pourcentage.compareTo(BigDecimal.ZERO) == 0) {
            commande.setStatutPaiement(Commande.StatutPaiement.NON_PAYE);
        } else if (pourcentage.compareTo(new BigDecimal("100")) < 0) {
            commande.setStatutPaiement(Commande.StatutPaiement.PARTIEL);
        } else {
            commande.setStatutPaiement(Commande.StatutPaiement.PAYE);
        }

        CommandeResponse resp = toResponse(commandeRepository.save(commande));

        List<Utilisateur> logisticiens = utilisateurRepository.findByRoleLibelleAndStatut(
                Role.RoleEnum.OPERATEUR_STOCK, Utilisateur.StatutEnum.ACTIF);
        String message = "Paiement enregistre sur la commande " + commande.getNumero()
                + " - " + pourcentage + "% (" + montantPaye + " CFA)";
        for (Utilisateur logisticien : logisticiens) {
            notificationRepository.save(Notification.builder()
                    .destinataire(logisticien)
                    .canal(Notification.Canal.IN_APP)
                    .message(message)
                    .build());
        }

        return resp;
    }

    public List<Object[]> getRepartitionStatuts() {
        return commandeRepository.repartitionStatuts();
    }

    public List<CommandeResponse> findAll() {
        return commandeRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CommandeResponse toResponse(Commande c) {
        CommandeResponse resp = new CommandeResponse();
        resp.setId(c.getId());
        resp.setNumero(c.getNumero());
        resp.setClient(c.getClient());
        resp.setTypeFlux(c.getTypeFlux().name());
        resp.setTotal(c.getTotal());
        resp.setStatut(c.getStatut().name());
        resp.setStatutPaiement(c.getStatutPaiement().name());
        resp.setPourcentagePaiement(c.getPourcentagePaiement());
        resp.setMontantPaye(c.getMontantPaye());
        resp.setDatePaiement(c.getDatePaiement());
        resp.setDelaiLivraison(c.getDelaiLivraison());
        resp.setFournisseurNom(c.getFournisseur() != null ? c.getFournisseur().getDesignation() : null);
        resp.setUtilisateur(c.getUtilisateur() != null ? c.getUtilisateur().getIdentite() : null);
        resp.setCreatedAt(c.getCreatedAt());
        resp.setUpdatedAt(c.getUpdatedAt());

        if (c.getLignes() != null) {
            resp.setLignes(c.getLignes().stream().map(l -> {
                LigneCommandeResponse lr = new LigneCommandeResponse();
                lr.setId(l.getId());
                lr.setArticleReference(l.getArticle().getReference());
                lr.setArticleDesignation(l.getArticle().getDesignation());
                lr.setQuantite(l.getQuantite());
                lr.setPrixUnitaire(l.getPrixUnitaire());
                lr.setSousTotal(l.getSousTotal());
                return lr;
            }).toList());
        }

        return resp;
    }

    @Transactional
    public void delete(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new IllegalArgumentException("Commande introuvable : " + id);
        }
        livraisonRepository.clearCommandeReferences(id);
        commandeRepository.deleteById(id);
    }
}
