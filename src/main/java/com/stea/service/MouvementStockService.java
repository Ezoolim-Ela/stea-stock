package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Article;
import com.stea.entity.MouvementStock;
import com.stea.entity.Utilisateur;
import com.stea.repository.ArticleRepository;
import com.stea.repository.MouvementStockRepository;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Page<MouvementStockResponse> search(String type, Long articleId,
                                                 LocalDateTime dateDebut, LocalDateTime dateFin,
                                                 int page, int size) {
        MouvementStock.TypeMouvement typeEnum = type != null ? MouvementStock.TypeMouvement.valueOf(type) : null;
        return mouvementRepository.search(typeEnum, articleId, dateDebut, dateFin,
                        PageRequest.of(page, size, Sort.by("dateMouvement").descending()))
                .map(this::toResponse);
    }

    public MouvementStockResponse getById(Long id) {
        MouvementStock m = mouvementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement introuvable : " + id));
        return toResponse(m);
    }

    @Transactional
    public MouvementStockResponse create(MouvementStockRequest request, Long operateurId) {
        Article article = null;

        if (request.getArticleId() != null) {
            article = articleRepository.findById(request.getArticleId()).orElse(null);
        } else if (request.getArticleReference() != null && !request.getArticleReference().isBlank()) {
            article = articleRepository.findByReference(request.getArticleReference()).orElse(null);
        }

        MouvementStock.TypeMouvement type = MouvementStock.TypeMouvement.valueOf(request.getType());

        if (article != null) {
            if (type == MouvementStock.TypeMouvement.SORTIE && request.getQuantite() > article.getQuantiteStock()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant. Disponible : " + article.getQuantiteStock() + ", Demandé : " + request.getQuantite());
            }

            switch (type) {
                case ENTREE -> article.setQuantiteStock(article.getQuantiteStock() + request.getQuantite());
                case SORTIE -> article.setQuantiteStock(article.getQuantiteStock() - request.getQuantite());
                case TRANSFERT -> article.setQuantiteStock(article.getQuantiteStock() - request.getQuantite());
                case AJUSTEMENT -> article.setQuantiteStock(request.getQuantite());
            }
            articleRepository.save(article);
        }

        Utilisateur operateur = operateurId != null ?
                utilisateurRepository.findById(operateurId).orElse(null) : null;

        String ref = "MOV" + System.currentTimeMillis();

        String refText = request.getArticleReference() != null ? request.getArticleReference().trim() : null;
        String desigText = request.getArticleDesignation() != null ? request.getArticleDesignation().trim() : null;
        if (article != null) {
            refText = article.getReference();
            desigText = article.getDesignation();
        }

        MouvementStock mouvement = MouvementStock.builder()
                .type(type)
                .article(article)
                .articleReferenceText(refText)
                .articleDesignationText(desigText)
                .quantite(request.getQuantite())
                .unite(request.getUnite() != null ? request.getUnite() : "Un")
                .emplacementSourceText(request.getEmplacementSource() != null ? request.getEmplacementSource().trim() : null)
                .emplacementDestinationText(request.getEmplacementDestination() != null ? request.getEmplacementDestination().trim() : null)
                .operateur(operateur)
                .reference(ref)
                .statut(MouvementStock.StatutMouvement.VALIDE)
                .build();

        return toResponse(mouvementRepository.save(mouvement));
    }

    @Transactional
    public MouvementStockResponse update(Long id, MouvementStockRequest request) {
        MouvementStock mouvement = mouvementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement introuvable : " + id));

        Article article = null;
        if (request.getArticleId() != null) {
            article = articleRepository.findById(request.getArticleId()).orElse(null);
        } else if (request.getArticleReference() != null && !request.getArticleReference().isBlank()) {
            article = articleRepository.findByReference(request.getArticleReference()).orElse(null);
        }

        mouvement.setType(MouvementStock.TypeMouvement.valueOf(request.getType()));
        mouvement.setArticle(article);
        mouvement.setQuantite(request.getQuantite());

        String refText = request.getArticleReference() != null ? request.getArticleReference().trim() : null;
        String desigText = request.getArticleDesignation() != null ? request.getArticleDesignation().trim() : null;
        if (article != null) {
            refText = article.getReference();
            desigText = article.getDesignation();
        }
        mouvement.setArticleReferenceText(refText);
        mouvement.setArticleDesignationText(desigText);

        mouvement.setEmplacementSourceText(request.getEmplacementSource() != null ? request.getEmplacementSource().trim() : null);
        mouvement.setEmplacementDestinationText(request.getEmplacementDestination() != null ? request.getEmplacementDestination().trim() : null);

        if (request.getUnite() != null) {
            mouvement.setUnite(request.getUnite());
        }

        return toResponse(mouvementRepository.save(mouvement));
    }

    public List<MouvementStockResponse> findAll() {
        return mouvementRepository.findAll().stream().map(this::toResponse).toList();
    }

    private MouvementStockResponse toResponse(MouvementStock m) {
        MouvementStockResponse resp = new MouvementStockResponse();
        resp.setId(m.getId());
        resp.setType(m.getType().name());
        resp.setArticleReference(m.getArticle() != null ? m.getArticle().getReference() : m.getArticleReferenceText());
        resp.setArticleDesignation(m.getArticle() != null ? m.getArticle().getDesignation() : m.getArticleDesignationText());
        resp.setQuantite(m.getQuantite());
        resp.setUnite(m.getUnite());
        resp.setEmplacementSource(m.getEmplacementSource() != null ? m.getEmplacementSource().getNom() : m.getEmplacementSourceText());
        resp.setEmplacementDestination(m.getEmplacementDestination() != null ? m.getEmplacementDestination().getNom() : m.getEmplacementDestinationText());
        resp.setOperateur(m.getOperateur() != null ? m.getOperateur().getIdentite() : null);
        resp.setReference(m.getReference());
        resp.setStatut(m.getStatut().name());
        resp.setDateMouvement(m.getDateMouvement());
        return resp;
    }

    public void delete(Long id) {
        if (!mouvementRepository.existsById(id)) {
            throw new IllegalArgumentException("Mouvement introuvable : " + id);
        }
        mouvementRepository.deleteById(id);
    }
}
