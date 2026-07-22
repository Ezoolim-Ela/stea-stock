package com.stea.service;

import com.stea.dto.ArticleResponse;
import com.stea.dto.DashboardResponse;
import com.stea.dto.MouvementStockResponse;
import com.stea.entity.MouvementStock;
import com.stea.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ArticleRepository articleRepository;
    private final CommandeRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final MouvementStockRepository mouvementStockRepository;

    public DashboardResponse getDashboard() {
        DashboardResponse dash = new DashboardResponse();

        dash.setValeurStock(articleRepository.calculerValeurTotaleStock());
        dash.setAlertesSeuilCritique(articleRepository.countAlertesSeuilCritique());
        dash.setFournisseursActifs(fournisseurRepository.countByStatut(
                com.stea.entity.Fournisseur.StatutFournisseur.ACTIF));

        long totalConfirmees = commandeRepository.countConfirmeesDepuis(LocalDateTime.now().minusMonths(12));
        long totalLivrees = commandeRepository.countLivreesDepuis(LocalDateTime.now().minusMonths(12));
        dash.setTauxLivraisonPonctuelle(totalConfirmees > 0 ?
                (double) totalLivrees / totalConfirmees * 100 : 0);

        dash.setCommandesEnRetard(commandeRepository.countByStatut(
                com.stea.entity.Commande.StatutCommande.CONFIRMEE));

        dash.setRepartitionMouvements(mouvementStockRepository.repartitionMouvementsParType(
                LocalDateTime.now().minusMonths(12)));

        dash.setRepartitionStatutsCommandes(commandeRepository.repartitionStatuts());

        List<MouvementStock> derniersMouvements = mouvementStockRepository.findTop20ByOrderByDateMouvementDesc();
        dash.setDerniersMouvements(derniersMouvements.stream().map(m -> {
            MouvementStockResponse resp = new MouvementStockResponse();
            resp.setId(m.getId());
            resp.setType(m.getType().name());
            resp.setArticleReference(m.getArticle() != null ? m.getArticle().getReference() : m.getArticleReferenceText());
            resp.setArticleDesignation(m.getArticle() != null ? m.getArticle().getDesignation() : m.getArticleDesignationText());
            resp.setQuantite(m.getQuantite());
            resp.setUnite(m.getUnite());
            resp.setOperateur(m.getOperateur() != null ? m.getOperateur().getIdentite() : null);
            resp.setReference(m.getReference());
            resp.setStatut(m.getStatut().name());
            resp.setDateMouvement(m.getDateMouvement());
            return resp;
        }).toList());

        dash.setArticlesEnAlerte(articleRepository.findArticlesEnAlerte().stream().map(a -> {
            ArticleResponse resp = new ArticleResponse();
            resp.setId(a.getId());
            resp.setReference(a.getReference());
            resp.setDesignation(a.getDesignation());
            resp.setCategorie(a.getCategorie());
            resp.setPrixUnitaire(a.getPrixUnitaire());
            resp.setQuantiteStock(a.getQuantiteStock());
            resp.setSeuilAlerte(a.getSeuilAlerte());
            if (a.getService() != null) {
                resp.setServiceId(a.getService().getId());
                resp.setServiceNom(a.getService().getNom());
            }
            return resp;
        }).toList());

        return dash;
    }
}
