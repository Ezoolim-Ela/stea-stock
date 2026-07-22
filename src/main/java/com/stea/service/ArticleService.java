package com.stea.service;

import com.stea.dto.*;
import com.stea.entity.Article;
import com.stea.entity.ServiceStea;
import com.stea.repository.ArticleRepository;
import com.stea.repository.ServiceSteaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ServiceSteaRepository serviceSteaRepository;

    public Page<ArticleResponse> search(String search, String categorie, String type, Long serviceId,
                                         BigDecimal prixMin, BigDecimal prixMax, int page, int size) {
        return articleRepository.search(search, categorie, type, serviceId, prixMin, prixMax,
                        PageRequest.of(page, size, Sort.by("reference").ascending()))
                .map(this::toResponse);
    }

    public ArticleResponse getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + id));
        return toResponse(article);
    }

    public ArticleResponse getByReference(String reference) {
        Article article = articleRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + reference));
        return toResponse(article);
    }

    public ArticleResponse create(ArticleRequest request) {
        if (request.getReference() != null && !request.getReference().isBlank() &&
                articleRepository.existsByReference(request.getReference())) {
            throw new IllegalArgumentException("La référence existe déjà : " + request.getReference());
        }

        Article article = Article.builder()
                .reference(request.getReference())
                .designation(request.getDesignation())
                .categorie(request.getCategorie())
                .type(request.getType() != null ? Article.TypeArticle.valueOf(request.getType()) : null)
                .prixUnitaire(request.getPrixUnitaire())
                .codeBarre(request.getCodeBarre())
                .quantiteStock(request.getQuantiteStock() != null ? request.getQuantiteStock() : 0)
                .seuilAlerte(request.getSeuilAlerte() != null ? request.getSeuilAlerte() : 10)
                .description(request.getDescription())
                .photo(request.getPhoto())
                .build();

        if (request.getServiceId() != null) {
            serviceSteaRepository.findById(request.getServiceId())
                    .ifPresent(article::setService);
        }

        return toResponse(articleRepository.save(article));
    }

    public ArticleResponse update(Long id, ArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + id));

        if (request.getReference() != null && !request.getReference().isBlank() &&
                !request.getReference().equals(article.getReference()) &&
                articleRepository.existsByReference(request.getReference())) {
            throw new IllegalArgumentException("La référence existe déjà : " + request.getReference());
        }

        article.setReference(request.getReference());
        article.setDesignation(request.getDesignation());
        article.setCategorie(request.getCategorie());
        if (request.getType() != null) {
            article.setType(Article.TypeArticle.valueOf(request.getType()));
        }
        article.setPrixUnitaire(request.getPrixUnitaire());
        article.setCodeBarre(request.getCodeBarre());
        if (request.getQuantiteStock() != null) article.setQuantiteStock(request.getQuantiteStock());
        if (request.getSeuilAlerte() != null) article.setSeuilAlerte(request.getSeuilAlerte());
        article.setDescription(request.getDescription());
        if (request.getPhoto() != null) article.setPhoto(request.getPhoto());
        if (request.getServiceId() != null) {
            serviceSteaRepository.findById(request.getServiceId())
                    .ifPresent(article::setService);
        }

        return toResponse(articleRepository.save(article));
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new IllegalArgumentException("Article introuvable : " + id);
        }
        articleRepository.clearLotsReferences(id);
        articleRepository.clearLignesCommandeReferences(id);
        articleRepository.clearMouvementsReferences(id);
        articleRepository.clearNumerosSerieReferences(id);
        articleRepository.clearReglesReferences(id);
        articleRepository.clearLignesInventaireReferences(id);
        articleRepository.deleteById(id);
    }

    public BigDecimal getValeurTotaleStock() {
        return articleRepository.calculerValeurTotaleStock();
    }

    public long countAlertesSeuilCritique() {
        return articleRepository.countAlertesSeuilCritique();
    }

    public List<ArticleResponse> findAll() {
        return articleRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ArticleResponse toResponse(Article article) {
        ArticleResponse resp = new ArticleResponse();
        resp.setId(article.getId());
        resp.setReference(article.getReference());
        resp.setDesignation(article.getDesignation());
        resp.setCategorie(article.getCategorie());
        resp.setType(article.getType() != null ? article.getType().name() : null);
        resp.setPrixUnitaire(article.getPrixUnitaire());
        resp.setCodeBarre(article.getCodeBarre());
        resp.setQuantiteStock(article.getQuantiteStock());
        resp.setSeuilAlerte(article.getSeuilAlerte());
        resp.setDescription(article.getDescription());
        resp.setPhoto(article.getPhoto());
        if (article.getService() != null) {
            resp.setServiceId(article.getService().getId());
            resp.setServiceNom(article.getService().getNom());
        }
        resp.setCreatedAt(article.getCreatedAt());
        resp.setUpdatedAt(article.getUpdatedAt());
        return resp;
    }
}
