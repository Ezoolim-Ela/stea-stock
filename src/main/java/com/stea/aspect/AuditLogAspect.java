package com.stea.aspect;

import com.stea.entity.AuditLog;
import com.stea.repository.UtilisateurRepository;
import com.stea.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final UtilisateurRepository utilisateurRepository;

    private static final Map<String, String> ENTITY_MAP = new HashMap<>();
    static {
        ENTITY_MAP.put("Article", "ARTICLE");
        ENTITY_MAP.put("Commande", "COMMANDE");
        ENTITY_MAP.put("Livraison", "LIVRAISON");
        ENTITY_MAP.put("Fournisseur", "FOURNISSEUR");
        ENTITY_MAP.put("MouvementStock", "MOUVEMENT_STOCK");
        ENTITY_MAP.put("Utilisateur", "UTILISATEUR");
        ENTITY_MAP.put("Notification", "NOTIFICATION");
        ENTITY_MAP.put("Message", "MESSAGE");
        ENTITY_MAP.put("ServiceStea", "SERVICE");
        ENTITY_MAP.put("Categorie", "CATEGORIE");
        ENTITY_MAP.put("Entrepot", "ENTREPOT");
        ENTITY_MAP.put("InventairePhysique", "INVENTAIRE");
        ENTITY_MAP.put("Lot", "LOT");
        ENTITY_MAP.put("ConfigurationSysteme", "CONFIGURATION");
        ENTITY_MAP.put("Comptabilite", "COMPTABILITE");
    }

    @Pointcut("execution(* com.stea.controller.*Controller.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        String action = determineAction(methodName);
        if (action == null) return joinPoint.proceed();

        String entity = ENTITY_MAP.getOrDefault(className.replace("Controller", ""), "UNKNOWN");

        Object result = null;
        boolean success = true;
        String error = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
            throw e;
        } finally {
            try {
                AuditLog auditLog = AuditLog.builder()
                        .action(action)
                        .entite(entity)
                        .succes(success)
                        .erreur(error != null && error.length() > 500 ? error.substring(0, 500) : error)
                        .build();

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getName() != null) {
                    String email = auth.getName();
                    auditLog.setEmail(email);
                    utilisateurRepository.findByEmail(email).ifPresent(u -> {
                        auditLog.setUtilisateur(u);
                        auditLog.setRole(u.getRole().getLibelle().name());
                    });
                }

                try {
                    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpServletRequest request = attrs.getRequest();
                        auditLog.setEndpoint(request.getRequestURI());
                        auditLog.setMethode(request.getMethod());
                        auditLog.setIpAddress(request.getRemoteAddr());
                    }
                } catch (Exception ignored) {}

                String details = buildDetails(joinPoint);
                auditLog.setDetails(details);

                auditLogService.log(auditLog);
            } catch (Exception e) {
                log.error("Failed to save audit log", e);
            }
        }
        return result;
    }

    private String determineAction(String methodName) {
        if (methodName.equals("create") || methodName.equals("save") || methodName.equals("register")
                || methodName.equals("inscription") || methodName.equals("envoyer")
                || methodName.equals("addLigne") || methodName.equals("add")
                || methodName.equals("createCommande") || methodName.equals("createLivraison")
                || methodName.equals("createArticle") || methodName.equals("createMouvement")
                || methodName.equals("saveInventaire") || methodName.equals("saveEntrepot")
                || methodName.equals("creerNotification") || methodName.equals("saveFournisseur")
                || methodName.equals("saveCategorie") || methodName.equals("saveService")
                || methodName.equals("saveConfiguration")) return "CREATION";
        if (methodName.equals("update") || methodName.equals("modifier") || methodName.equals("approuver")
                || methodName.equals("updateStatut") || methodName.equals("marquerCommeLu")
                || methodName.equals("lire") || methodName.equals("marquerLu")
                || methodName.equals("lireToutes") || methodName.equals("enregistrerPaiement")
                || methodName.equals("updateQuantity") || methodName.equals("updateStatutCommande")
                || methodName.equals("confirmer") || methodName.equals("valider")
                || methodName.equals("updateEntrepot") || methodName.equals("updateConfiguration")) return "MODIFICATION";
        if (methodName.equals("delete") || methodName.equals("supprimer") || methodName.equals("rejeter")
                || methodName.equals("removeLigne") || methodName.equals("deleteLigne")) return "SUPPRESSION";
        if (methodName.equals("login")) return "CONNEXION";
        if (methodName.equals("export") || methodName.equals("exportExcel") || methodName.equals("exportPdf")
                || methodName.equals("exportMouvements") || methodName.equals("exportCommandes")
                || methodName.equals("exportLivraisons")) return "EXPORT";
        if (methodName.equals("envoyerMessage")) return "ENVOI_MESSAGE";
        return null;
    }

    private String buildDetails(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null) {
                String argStr = args[0].toString();
                return argStr.length() > 500 ? argStr.substring(0, 500) : argStr;
            }
        } catch (Exception ignored) {}
        return "";
    }
}