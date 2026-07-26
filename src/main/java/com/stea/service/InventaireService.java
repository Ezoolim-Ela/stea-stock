package com.stea.service;

import com.stea.entity.*;
import com.stea.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventaireService {

    private final InventairePhysiqueRepository inventaireRepository;
    private final LigneInventaireRepository ligneInventaireRepository;
    private final ArticleRepository articleRepository;
    private final EntrepotRepository entrepotRepository;
    private final EmplacementRepository emplacementRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final LotRepository lotRepository;

    public List<InventairePhysique> lister() {
        return inventaireRepository.findAll();
    }

    public InventairePhysique get(Long id) {
        return inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire non trouve"));
    }

    @Transactional
    public InventairePhysique creer(Long entrepotId, InventairePhysique.TypeInventaire type, LocalDate datePrevue, String realisateur) {
        Entrepot entrepot = entrepotRepository.findById(entrepotId)
                .orElseThrow(() -> new RuntimeException("Entrepot non trouve"));

        String reference = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", inventaireRepository.count() + 1);

        InventairePhysique inventaire = InventairePhysique.builder()
                .reference(reference)
                .entrepot(entrepot)
                .type(type)
                .statut(InventairePhysique.StatutInventaire.BROUILLON)
                .datePrevue(datePrevue)
                .realisateur(realisateur)
                .build();

        return inventaireRepository.save(inventaire);
    }

    @Transactional
    public InventairePhysique demarrer(Long id) {
        InventairePhysique inv = get(id);
        inv.setStatut(InventairePhysique.StatutInventaire.EN_COURS);
        inv.setDateRealisation(LocalDate.now());

        List<Article> articles = articleRepository.findAll();
        List<LigneInventaire> lignes = new ArrayList<>();

        for (Article article : articles) {
            LigneInventaire ligne = LigneInventaire.builder()
                    .inventaire(inv)
                    .article(article)
                    .quantiteTheorique(article.getQuantiteStock())
                    .quantitePhysique(0)
                    .ecart(0)
                    .statut(LigneInventaire.StatutLigne.EN_ATTENTE)
                    .build();
            lignes.add(ligne);
        }

        ligneInventaireRepository.saveAll(lignes);
        inv.setNbArticlesComptes(0);
        return inventaireRepository.save(inv);
    }

    @Transactional
    public LigneInventaire compter(Long ligneId, Integer quantitePhysique, String observation) {
        LigneInventaire ligne = ligneInventaireRepository.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne d'inventaire non trouvee"));

        ligne.setQuantitePhysique(quantitePhysique);
        ligne.setEcart(quantitePhysique - ligne.getQuantiteTheorique());
        ligne.setStatut(ligne.getEcart() != 0
                ? LigneInventaire.StatutLigne.ECART
                : LigneInventaire.StatutLigne.COMPTE);
        ligne.setObservation(observation);

        LigneInventaire saved = ligneInventaireRepository.save(ligne);

        InventairePhysique inv = ligne.getInventaire();
        long nbComptes = ligneInventaireRepository.countByInventaireId(inv.getId());
        long nbEcarts = ligneInventaireRepository.countByInventaireIdAndEcartNot(inv.getId(), 0);
        inv.setNbArticlesComptes((int) nbComptes);
        inv.setNbEcarts((int) nbEcarts);
        inventaireRepository.save(inv);

        return saved;
    }

    @Transactional
    public InventairePhysique valider(Long id) {
        InventairePhysique inv = get(id);
        inv.setStatut(InventairePhysique.StatutInventaire.VALIDE);

        List<LigneInventaire> toutesLignes = ligneInventaireRepository.findByInventaireId(id);
        BigDecimal valeurEcarts = BigDecimal.ZERO;

        for (LigneInventaire ligne : toutesLignes) {
            if (ligne.getStatut() == LigneInventaire.StatutLigne.EN_ATTENTE) {
                continue;
            }

            Article article = ligne.getArticle();
            article.setQuantiteStock(ligne.getQuantitePhysique());
            articleRepository.save(article);

            if (ligne.getEcart() != 0) {
                valeurEcarts = valeurEcarts.add(
                        BigDecimal.valueOf(ligne.getEcart()).multiply(article.getPrixUnitaire() != null
                                ? article.getPrixUnitaire() : BigDecimal.ZERO));

                MouvementStock mouvement = MouvementStock.builder()
                        .type(MouvementStock.TypeMouvement.INVENTAIRE)
                        .article(article)
                        .quantite(Math.abs(ligne.getEcart()))
                        .unite("Unite")
                        .dateMouvement(LocalDateTime.now())
                        .statut(MouvementStock.StatutMouvement.TRAITE)
                        .inventaire(inv)
                        .observation("Ajustement inventaire " + inv.getReference()
                                + " (ecart: " + ligne.getEcart() + ")")
                        .build();
                mouvementStockRepository.save(mouvement);
            }
        }

        inv.setValeurEcarts(valeurEcarts);
        return inventaireRepository.save(inv);
    }

    public List<LigneInventaire> getLignesInventaire(Long inventaireId) {
        return ligneInventaireRepository.findByInventaireId(inventaireId);
    }

    public List<LigneInventaire> getEcarts(Long inventaireId) {
        return ligneInventaireRepository.findByInventaireIdAndEcartNot(inventaireId, 0);
    }

    public List<InventairePhysique> getInventairesEnCours() {
        return inventaireRepository.findByStatutIn(
                List.of(InventairePhysique.StatutInventaire.BROUILLON,
                        InventairePhysique.StatutInventaire.EN_COURS));
    }
}
