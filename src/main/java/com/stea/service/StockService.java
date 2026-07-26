package com.stea.service;

import com.stea.entity.*;
import com.stea.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ArticleRepository articleRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final LotRepository lotRepository;
    private final NumeroSerieRepository numeroSerieRepository;
    private final EmplacementRepository emplacementRepository;
    private final EntrepotRepository entrepotRepository;
    private final RegleReapprovisionnementRepository regleReapproRepository;

    @Transactional
    public MouvementStock enregistrerEntree(Long articleId, Integer quantite, String unite,
            Long entrepotDestId, Long emplacementDestId, Long lotId, String numeroSerie,
            Long operateurId, String observation) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouve"));
        Utilisateur operateur = null;

        MouvementStock mouvement = MouvementStock.builder()
                .type(MouvementStock.TypeMouvement.ENTREE)
                .article(article)
                .quantite(quantite)
                .unite(unite)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStock.StatutMouvement.TRAITE)
                .observation(observation)
                .build();

        if (entrepotDestId != null) {
            entrepotRepository.findById(entrepotDestId).ifPresent(mouvement::setEntrepotDestination);
        }
        if (emplacementDestId != null) {
            emplacementRepository.findById(emplacementDestId).ifPresent(mouvement::setEmplacementDestination);
        }
        if (lotId != null) {
            lotRepository.findById(lotId).ifPresent(lot -> {
                mouvement.setLot(lot);
                lot.setQuantiteRestante(lot.getQuantiteRestante() + quantite);
                lotRepository.save(lot);
            });
        }
        if (numeroSerie != null) {
            mouvement.setNumeroSerie(numeroSerie);
        }

        article.setQuantiteStock(article.getQuantiteStock() + quantite);
        articleRepository.save(article);

        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public MouvementStock enregistrerSortie(Long articleId, Integer quantite, String unite,
            Long entrepotSourceId, Long emplacementSourceId, Long lotId, String numeroSerie,
            Long operateurId, String observation) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouve"));

        if (article.getQuantiteStock() < quantite) {
            throw new RuntimeException("Stock insuffisant pour " + article.getDesignation()
                    + ". Stock actuel: " + article.getQuantiteStock() + ", Sortie demandee: " + quantite);
        }

        MouvementStock mouvement = MouvementStock.builder()
                .type(MouvementStock.TypeMouvement.SORTIE)
                .article(article)
                .quantite(quantite)
                .unite(unite)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStock.StatutMouvement.TRAITE)
                .observation(observation)
                .build();

        if (entrepotSourceId != null) {
            entrepotRepository.findById(entrepotSourceId).ifPresent(mouvement::setEntrepotSource);
        }
        if (emplacementSourceId != null) {
            emplacementRepository.findById(emplacementSourceId).ifPresent(mouvement::setEmplacementSource);
        }
        if (lotId != null) {
            lotRepository.findById(lotId).ifPresent(lot -> {
                mouvement.setLot(lot);
                lot.setQuantiteRestante(lot.getQuantiteRestante() - quantite);
                lotRepository.save(lot);
            });
        }
        if (numeroSerie != null) {
            mouvement.setNumeroSerie(numeroSerie);
        }

        article.setQuantiteStock(article.getQuantiteStock() - quantite);
        articleRepository.save(article);

        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public MouvementStock enregistrerTransfert(Long articleId, Integer quantite,
            Long entrepotSourceId, Long entrepotDestId,
            Long emplacementSourceId, Long emplacementDestId,
            Long operateurId, String observation) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouve"));

        if (article.getQuantiteStock() < quantite) {
            throw new RuntimeException("Stock insuffisant pour transfert");
        }

        MouvementStock mouvement = MouvementStock.builder()
                .type(MouvementStock.TypeMouvement.TRANSFERT)
                .article(article)
                .quantite(quantite)
                .unite("Unite")
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStock.StatutMouvement.TRAITE)
                .observation(observation)
                .build();

        if (entrepotSourceId != null) {
            entrepotRepository.findById(entrepotSourceId).ifPresent(mouvement::setEntrepotSource);
        }
        if (entrepotDestId != null) {
            entrepotRepository.findById(entrepotDestId).ifPresent(mouvement::setEntrepotDestination);
        }
        if (emplacementSourceId != null) {
            emplacementRepository.findById(emplacementSourceId).ifPresent(mouvement::setEmplacementSource);
        }
        if (emplacementDestId != null) {
            emplacementRepository.findById(emplacementDestId).ifPresent(mouvement::setEmplacementDestination);
        }

        article.setQuantiteStock(article.getQuantiteStock() - quantite);
        articleRepository.save(article);

        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public Lot creerLot(Long articleId, String numeroLot, Integer quantite,
            LocalDate dateProduction, LocalDate dateExpiration, BigDecimal coutUnitaire, String fournisseur) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouve"));

        Lot lot = Lot.builder()
                .numeroLot(numeroLot)
                .article(article)
                .quantiteInitiale(quantite)
                .quantiteRestante(quantite)
                .dateProduction(dateProduction)
                .dateExpiration(dateExpiration)
                .coutUnitaire(coutUnitaire)
                .fournisseur(fournisseur)
                .statut(Lot.StatutLot.ACTIF)
                .build();

        return lotRepository.save(lot);
    }

    public List<Lot> getLotsDisponibles(Long articleId) {
        return lotRepository.findLotsDisponiblesFIFO(articleId);
    }

    public List<Lot> getLotsExpires() {
        return lotRepository.findLotsExpires(LocalDate.now());
    }

    public List<Lot> getLotsExpirationProche(int jours) {
        return lotRepository.findLotsExpirationProche(LocalDate.now(), LocalDate.now().plusDays(jours));
    }

    public BigDecimal calculerValorisationFIFO(Long articleId) {
        List<Lot> lots = lotRepository.findLotsDisponiblesFIFO(articleId);
        return lots.stream()
                .map(l -> l.getCoutUnitaire() != null
                        ? l.getCoutUnitaire().multiply(BigDecimal.valueOf(l.getQuantiteRestante()))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculerValorisationAVCO(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouve"));
        return article.getCoutMoyenPondere().multiply(BigDecimal.valueOf(article.getQuantiteStock()));
    }

    public List<RegleReapprovisionnement> getArticlesASurveiller() {
        return regleReapproRepository.findArticlesASurveiller();
    }

    public List<MouvementStock> getHistoriqueArticle(Long articleId) {
        return mouvementStockRepository.findByArticleId(articleId);
    }

    public List<NumeroSerie> getNumerosSerieArticle(Long articleId) {
        return numeroSerieRepository.findByArticleId(articleId);
    }
}
