package com.stea.config;

import com.stea.entity.*;
import com.stea.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategorieRepository categorieRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final LivraisonRepository livraisonRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final EntrepotRepository entrepotRepository;
    private final EmplacementRepository emplacementRepository;
    private final UniteMesureRepository uniteMesureRepository;
    private final LotRepository lotRepository;
    private final ServiceSteaRepository serviceSteaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        creerRoles();
        Utilisateur admin = creerAdmin();
        creerComptable();
        Utilisateur logisticien = creerLogisticien();
        List<ServiceStea> services = creerServices();
        List<Fournisseur> fournisseurs = creerFournisseurs();
        creerCategories();
        List<Article> articles = creerArticles(fournisseurs, services);
        creerEntrepots();
        creerUnitesMesure();
        creerLots(articles);
        List<Commande> commandes = creerCommandes(fournisseurs, articles);
        creerLivraisons(commandes);
        creerMouvementsStock(articles, logisticien);
        log.info("=== Donnees initiales STEA chargees avec succes ===");
    }

    private void creerRoles() {
        for (Role.RoleEnum roleEnum : Role.RoleEnum.values()) {
            if (roleRepository.findByLibelle(roleEnum).isEmpty()) {
                roleRepository.save(Role.builder().libelle(roleEnum).build());
                log.info("Role cree: {}", roleEnum);
            }
        }
    }

    private Utilisateur creerAdmin() {
        if (!utilisateurRepository.existsByEmail("admin@stea.tg")) {
            Role adminRole = roleRepository.findByLibelle(Role.RoleEnum.ADMINISTRATEUR).orElseThrow();
            Utilisateur admin = Utilisateur.builder()
                    .identite("Administrateur STEA")
                    .email("admin@stea.tg")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .statut(Utilisateur.StatutEnum.ACTIF)
                    .build();
            return utilisateurRepository.save(admin);
        }
        return utilisateurRepository.findByEmail("admin@stea.tg").orElseThrow();
    }

    private void creerComptable() {
        if (!utilisateurRepository.existsByEmail("comptable@stea.tg")) {
            Role role = roleRepository.findByLibelle(Role.RoleEnum.COMPTABLE).orElseThrow();
            utilisateurRepository.save(Utilisateur.builder()
                    .identite("Comptable STEA")
                    .email("comptable@stea.tg")
                    .motDePasse(passwordEncoder.encode("comptable123"))
                    .role(role)
                    .statut(Utilisateur.StatutEnum.ACTIF)
                    .build());
            log.info("Utilisateur comptable cree: comptable@stea.tg / comptable123");
        }
    }

    private Utilisateur creerLogisticien() {
        if (!utilisateurRepository.existsByEmail("logisticien@stea.tg")) {
            Role role = roleRepository.findByLibelle(Role.RoleEnum.OPERATEUR_STOCK).orElseThrow();
            return utilisateurRepository.save(Utilisateur.builder()
                    .identite("Logisticien STEA")
                    .email("logisticien@stea.tg")
                    .motDePasse(passwordEncoder.encode("logisticien123"))
                    .role(role)
                    .statut(Utilisateur.StatutEnum.ACTIF)
                    .build());
        }
        return utilisateurRepository.findByEmail("logisticien@stea.tg").orElseThrow();
    }

    private List<ServiceStea> creerServices() {
        if (serviceSteaRepository.count() > 0) return serviceSteaRepository.findAll();
        List<ServiceStea> list = List.of(
            ServiceStea.builder().code("SCE-INF").nom("SCE Informatique").description("Materiels informatique : ordinateurs, imprimantes, ecrans, serveurs").responsable("Koffi Agbekple").telephone("+22890111111").email("sce-informatique@stea.tg").actif(true).build(),
            ServiceStea.builder().code("SCE-BIO").nom("SCE Biomedicale").description("Materiels de laboratoire et equipements hospitaliers, produits chimiques et reactifs").responsable("Dr. Ama Kokoroko").telephone("+22890222222").email("sce-biomedicale@stea.tg").actif(true).build(),
            ServiceStea.builder().code("SCE-COM").nom("SCE Commercial").description("Equipements industriels, controle d'acces et securite, materiels roulants, mobiliers de bureau").responsable("Kwame Asante").telephone("+22890333333").email("sce-commercial@stea.tg").actif(true).build()
        );
        List<ServiceStea> saved = serviceSteaRepository.saveAll(list);
        log.info("{} services STEA crees", saved.size());
        return saved;
    }

    private List<Fournisseur> creerFournisseurs() {
        if (fournisseurRepository.count() > 0) return fournisseurRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<Fournisseur> list = List.of(
            Fournisseur.builder().code("FRN-001").designation("Schneider Electric").categorieProduits("Equipements industriels").equipementsFournis("Disjoncteurs, coffrets, regulateurs").paysOrigine("France").contactPrincipal("Jean Dupont").telephone("+33123456789").email("contact@schneider.com").pays("France").ville("Paris").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(10)).build(),
            Fournisseur.builder().code("FRN-002").designation("ZKTeco").categorieProduits("Controle d'acces et securite").equipementsFournis("Biometrie, controles d'acces, cameras").paysOrigine("Chine").contactPrincipal("Li Wei").telephone("+861234567890").email("sales@zkteco.com").pays("Chine").ville("Shenzhen").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(5)).build(),
            Fournisseur.builder().code("FRN-003").designation("HANNA Instruments").categorieProduits("Materiels de laboratoire").equipementsFournis("pHmetres, conductimetres, spectrometres").paysOrigine("Etats-Unis").contactPrincipal("Mark Smith").telephone("+1987654321").email("info@hannainst.com").pays("Etats-Unis").ville("Rhode Island").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(15)).build(),
            Fournisseur.builder().code("FRN-004").designation("KOHLER SDMO").categorieProduits("Equipements industriels").equipementsFournis("Groupes electrogenes, onduleurs").paysOrigine("France").contactPrincipal("Pierre Martin").telephone("+33987654321").email("vente@kohler-sdmo.com").pays("France").ville("Brest").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(3)).build(),
            Fournisseur.builder().code("FRN-005").designation("Toyota Tsusho").categorieProduits("Materiels roulants").equipementsFournis("Vehicules, pick-up, SUV").paysOrigine("Japon").contactPrincipal("Takeshi Yamamoto").telephone("+81123456789").email("export@toyota-tsusho.jp").pays("Japon").ville("Nagoya").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(20)).build(),
            Fournisseur.builder().code("FRN-006").designation("Steelcase").categorieProduits("Mobiliers de bureau").equipementsFournis("Tables, fauteuils, armoires, salons").paysOrigine("Etats-Unis").contactPrincipal("Sarah Johnson").telephone("+1122334455").email("export@steelcase.com").pays("Etats-Unis").ville("Grand Rapids").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(7)).build(),
            Fournisseur.builder().code("FRN-007").designation("Fisher Scientific").categorieProduits("Produits chimiques et reactifs").equipementsFournis("Reactifs, produits chimiques, traitement eau").paysOrigine("Etats-Unis").contactPrincipal("Robert Brown").telephone("+1555666777").email("orders@fishersci.com").pays("Etats-Unis").ville("Pittsburgh").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(12)).build(),
            Fournisseur.builder().code("FRN-008").designation("HP Inc.").categorieProduits("Materiels informatique").equipementsFournis("Ordinateurs, imprimantes, ecrans").paysOrigine("Etats-Unis").contactPrincipal("Tom Wilson").telephone("+1444555666").email("sales@hp.com").pays("Etats-Unis").ville("Palo Alto").statut(Fournisseur.StatutFournisseur.ACTIF).derniereCommande(now.minusDays(2)).build()
        );
        List<Fournisseur> saved = fournisseurRepository.saveAll(list);
        log.info("{} fournisseurs STEA crees", saved.size());
        return saved;
    }

    private void creerCategories() {
        if (categorieRepository.count() > 0) return;
        List<Categorie> cats = List.of(
            Categorie.builder().nom("Equipements industriels").description("Groupe electrogene, outillages, equipements de manutention").build(),
            Categorie.builder().nom("Materiels de laboratoire et hospitalier").description("Materiels de laboratoire et equipements hospitaliers").build(),
            Categorie.builder().nom("Produits chimiques et reactifs").description("Produits de traitement de l'eau, reactifs").build(),
            Categorie.builder().nom("Controle d'acces et systeme de securite").description("Controle d'acces, equipements de securite").build(),
            Categorie.builder().nom("Materiels roulants").description("Vehicules, accessoires").build(),
            Categorie.builder().nom("Mobiliers de bureau").description("Tables, fauteuils, armoires, salons").build(),
            Categorie.builder().nom("Materiels informatique").description("Ordinateurs, imprimantes, ecrans interactifs").build()
        );
        categorieRepository.saveAll(cats);
        log.info("{} categories STEA creees", cats.size());
    }

    private List<Article> creerArticles(List<Fournisseur> fournisseurs, List<ServiceStea> services) {
        if (articleRepository.count() > 0) return articleRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        ServiceStea sceInformatique = services.get(0);
        ServiceStea sceBiomedicale = services.get(1);
        ServiceStea sceCommercial = services.get(2);
        List<Article> list = List.of(
            Article.builder().reference("STE-EQ-001").designation("Groupe electrogene diesel KOHLER SDMO 150KVA").categorie("Equipements industriels").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("18500000")).quantiteStock(3).dernierFournisseur(fournisseurs.get(3)).service(sceCommercial).description("Groupe electrogene insonorise 150KVA").build(),
            Article.builder().reference("STE-EQ-002").designation("Transpalette electrique 2,5 tonnes").categorie("Equipements industriels").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("3200000")).quantiteStock(5).dernierFournisseur(fournisseurs.get(0)).service(sceCommercial).description("Transpalette elec. charge utile 2500kg").build(),
            Article.builder().reference("STE-EQ-003").designation("Panneau solaire flexible monocristallin 100W").categorie("Equipements industriels").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("285000")).quantiteStock(20).dernierFournisseur(fournisseurs.get(0)).service(sceCommercial).description("Panneau solaire ETFE/EVA 100W").build(),
            Article.builder().reference("STE-EQ-004").designation("Convertisseur 12V/230V + USB 300W").categorie("Equipements industriels").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("175000")).quantiteStock(15).dernierFournisseur(fournisseurs.get(0)).service(sceCommercial).description("Convertisseur tension pure sinusoide").build(),
            Article.builder().reference("STE-EQ-005").designation("Poste de soudure MIG 160A").categorie("Equipements industriels").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("890000")).quantiteStock(4).dernierFournisseur(fournisseurs.get(0)).service(sceCommercial).description("Poste soudure MIG/MAG 160A").build(),
            Article.builder().reference("STE-LAB-001").designation("pH-metre Multiparametre HANNA HI 98194").categorie("Materiels de laboratoire et hospitalier").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("1250000")).quantiteStock(6).dernierFournisseur(fournisseurs.get(2)).service(sceBiomedicale).description("pH-metre de terrain multiparametre").build(),
            Article.builder().reference("STE-LAB-002").designation("Conductimetre HANNA HI 9835").categorie("Materiels de laboratoire et hospitalier").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("980000")).quantiteStock(4).dernierFournisseur(fournisseurs.get(2)).service(sceBiomedicale).description("Conductimetre portable haute precision").build(),
            Article.builder().reference("STE-LAB-003").designation("Analyseur d'electrolytes EA-2000B").categorie("Materiels de laboratoire et hospitalier").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("8500000")).quantiteStock(2).dernierFournisseur(fournisseurs.get(2)).service(sceBiomedicale).description("Analyseur semi-automatique electrolytes").build(),
            Article.builder().reference("STE-LAB-004").designation("Microscope biologique Celestron").categorie("Materiels de laboratoire et hospitalier").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("2100000")).quantiteStock(3).dernierFournisseur(fournisseurs.get(2)).service(sceBiomedicale).description("Microscope numerique camera integree").build(),
            Article.builder().reference("STE-CH-001").designation("Reactif de test pH 4.0 et 7.0").categorie("Produits chimiques et reactifs").type(Article.TypeArticle.CONSOMMABLE).prixUnitaire(new BigDecimal("45000")).quantiteStock(50).dernierFournisseur(fournisseurs.get(6)).service(sceBiomedicale).description("Kit calibration pH").build(),
            Article.builder().reference("STE-CH-002").designation("Produit traitement eau potabilisation").categorie("Produits chimiques et reactifs").type(Article.TypeArticle.CONSOMMABLE).prixUnitaire(new BigDecimal("125000")).quantiteStock(30).dernierFournisseur(fournisseurs.get(6)).service(sceBiomedicale).description("Chlorine dosage pour traitement eau").build(),
            Article.builder().reference("STE-CH-003").designation("Acide chlorhydrique 37% 2,5L").categorie("Produits chimiques et reactifs").type(Article.TypeArticle.CONSOMMABLE).prixUnitaire(new BigDecimal("35000")).quantiteStock(25).dernierFournisseur(fournisseurs.get(6)).service(sceBiomedicale).description("Acide HCl analytique").build(),
            Article.builder().reference("STE-SEC-001").designation("Presence empreintes ZKTeco K40").categorie("Controle d'acces et systeme de securite").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("450000")).quantiteStock(10).dernierFournisseur(fournisseurs.get(1)).service(sceCommercial).description("Terminal biometrique empreintes + RFID").build(),
            Article.builder().reference("STE-SEC-002").designation("Centrale controle d'acces inBIO 260").categorie("Controle d'acces et systeme de securite").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("1850000")).quantiteStock(4).dernierFournisseur(fournisseurs.get(1)).service(sceCommercial).description("Controleur 4 portes TCP/IP").build(),
            Article.builder().reference("STE-SEC-003").designation("Gache electrique securite integree").categorie("Controle d'acces et systeme de securite").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("180000")).quantiteStock(12).dernierFournisseur(fournisseurs.get(1)).service(sceCommercial).description("Gache 12V/24V fail-safe").build(),
            Article.builder().reference("STE-VH-001").designation("Toyota Hilux Double Cabine 2.4L GD-6").categorie("Materiels roulants").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("35000000")).quantiteStock(2).dernierFournisseur(fournisseurs.get(4)).service(sceCommercial).description("Pick-up 4x4 diesel").build(),
            Article.builder().reference("STE-VH-002").designation("Toyota Land Cruiser Prado 2.8L").categorie("Materiels roulants").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("52000000")).quantiteStock(1).dernierFournisseur(fournisseurs.get(4)).service(sceCommercial).description("SUV 4x4 full options").build(),
            Article.builder().reference("STE-MOB-001").designation("Bureau Presidentiel 160x80cm").categorie("Mobiliers de bureau").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("850000")).quantiteStock(8).dernierFournisseur(fournisseurs.get(5)).service(sceCommercial).description("Bureau en bois massif avec caisson").build(),
            Article.builder().reference("STE-MOB-002").designation("Fauteuil Ergonomique Pro").categorie("Mobiliers de bureau").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("650000")).quantiteStock(15).dernierFournisseur(fournisseurs.get(5)).service(sceCommercial).description("Fauteuil cuir synthetique").build(),
            Article.builder().reference("STE-MOB-003").designation("Salon 3 places + 2 Fauteuils").categorie("Mobiliers de bureau").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("2500000")).quantiteStock(3).dernierFournisseur(fournisseurs.get(5)).service(sceCommercial).description("Salon cuir veritable noir").build(),
            Article.builder().reference("STE-INF-001").designation("HP ProBook 450 G8 core i7 8Go 512Go SSD").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("1259000")).quantiteStock(10).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Ordinateur portable professionnel").build(),
            Article.builder().reference("STE-INF-002").designation("HP LaserJet Pro MFP M479fdw").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("980000")).quantiteStock(5).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Imprimante multifonction couleur").build(),
            Article.builder().reference("STE-PC-001").designation("Photocopieur Canon imageRUNNER 2525i").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("3200000")).quantiteStock(4).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Photocopieur noir et blanc 25 ppm, scan, impression reseau").build(),
            Article.builder().reference("STE-PC-002").designation("Photocopieur Ricoh MP 301SPF").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("4500000")).quantiteStock(3).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Photocopieur multifonction A3, 30 ppm, recto-verso auto").build(),
            Article.builder().reference("STE-PC-003").designation("Photocopieur Konica Minolta bizhub 227").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("2800000")).quantiteStock(5).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Photocopieur multifonction 22 ppm, scan couleur, fax").build(),
            Article.builder().reference("STE-PC-004").designation("Photocopieur HP LaserJet Managed MFP E72525dn").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("5500000")).quantiteStock(2).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Photocopieur couleur A3, 25 ppm, gestion securisee").build(),
            Article.builder().reference("STE-PC-005").designation("Photocopieur Epson WorkForce Pro WF-C879R").categorie("Materiels informatique").type(Article.TypeArticle.MATERIEL).prixUnitaire(new BigDecimal("3800000")).quantiteStock(3).dernierFournisseur(fournisseurs.get(7)).service(sceInformatique).description("Photocopieur jet d'encre A3, 24 ppm, EcoTank").build()
        );
        List<Article> saved = articleRepository.saveAll(list);
        log.info("{} articles STEA crees", saved.size());
        return saved;
    }

    private void creerEntrepots() {
        if (entrepotRepository.count() > 0) return;
        List<Entrepot> list = List.of(
            Entrepot.builder().code("ENT-001").nom("Entrepot Principal Lome").adresse("Zone Industrielle").ville("Lome").responsable("Kofi Mensah").telephone("+22890123456").actif(true).description("Entrepot principal").build(),
            Entrepot.builder().code("ENT-002").nom("Entrepot Laboratoire").adresse("Quartier Administratif").ville("Lome").responsable("Ama Darko").telephone("+22890234567").actif(true).description("Materiels de laboratoire").build(),
            Entrepot.builder().code("ENT-003").nom("Entrepot Securite").adresse("Boulevard du 13 Janvier").ville("Lome").responsable("Kwame Asante").telephone("+22890345678").actif(true).description("Materiels de securite").build(),
            Entrepot.builder().code("ENT-004").nom("Magasin Solaire").adresse("Zone Portuaire").ville("Lome").responsable("Kofi Mensah").telephone("+22890123456").actif(true).description("Panneaux solaires").build()
        );
        List<Entrepot> saved = entrepotRepository.saveAll(list);
        log.info("{} entrepots crees", saved.size());
    }

    private void creerUnitesMesure() {
        if (uniteMesureRepository.count() > 0) return;
        List<UniteMesure> list = List.of(
            UniteMesure.builder().code("UNI").nom("Unite").symbole("un").categorie(UniteMesure.CategorieUnite.UNITE).facteurConversion(1.0).build(),
            UniteMesure.builder().code("PAQUET").nom("Paquet").symbole("pqt").categorie(UniteMesure.CategorieUnite.UNITE).facteurConversion(1.0).build(),
            UniteMesure.builder().code("KG").nom("Kilogramme").symbole("kg").categorie(UniteMesure.CategorieUnite.POIDS).facteurConversion(1.0).build(),
            UniteMesure.builder().code("G").nom("Gramme").symbole("g").categorie(UniteMesure.CategorieUnite.POIDS).facteurConversion(0.001).build(),
            UniteMesure.builder().code("L").nom("Litre").symbole("L").categorie(UniteMesure.CategorieUnite.VOLUME).facteurConversion(1.0).build(),
            UniteMesure.builder().code("ML").nom("Millilitre").symbole("mL").categorie(UniteMesure.CategorieUnite.VOLUME).facteurConversion(0.001).build(),
            UniteMesure.builder().code("M").nom("Metre").symbole("m").categorie(UniteMesure.CategorieUnite.LONGUEUR).facteurConversion(1.0).build(),
            UniteMesure.builder().code("M2").nom("Metre carre").symbole("m2").categorie(UniteMesure.CategorieUnite.SURFACE).facteurConversion(1.0).build()
        );
        uniteMesureRepository.saveAll(list);
        log.info("{} unites de mesure crees", list.size());
    }

    private void creerLots(List<Article> articles) {
        if (lotRepository.count() > 0) return;
        if (articles.size() < 3) {
            log.warn("Pas assez d'articles pour creer les lots initiaux");
            return;
        }
        int as = articles.size();
        List<Lot> list = List.of(
            Lot.builder().numeroLot("LOT-CH-001").article(articles.get(10 % as)).quantiteInitiale(50).quantiteRestante(30).dateProduction(java.time.LocalDate.of(2026, 1, 15)).dateExpiration(java.time.LocalDate.of(2027, 1, 15)).coutUnitaire(new BigDecimal("45000")).fournisseur("Fisher Scientific").statut(Lot.StatutLot.ACTIF).build(),
            Lot.builder().numeroLot("LOT-CH-002").article(articles.get(10 % as)).quantiteInitiale(30).quantiteRestante(30).dateProduction(java.time.LocalDate.of(2026, 6, 1)).dateExpiration(java.time.LocalDate.of(2027, 6, 1)).coutUnitaire(new BigDecimal("42000")).fournisseur("Fisher Scientific").statut(Lot.StatutLot.ACTIF).build(),
            Lot.builder().numeroLot("LOT-CH-003").article(articles.get(11 % as)).quantiteInitiale(30).quantiteRestante(20).dateProduction(java.time.LocalDate.of(2026, 3, 1)).dateExpiration(java.time.LocalDate.of(2026, 7, 20)).coutUnitaire(new BigDecimal("125000")).fournisseur("Fisher Scientific").statut(Lot.StatutLot.ACTIF).build(),
            Lot.builder().numeroLot("LOT-SEC-001").article(articles.get(12 % as)).quantiteInitiale(10).quantiteRestante(6).dateProduction(java.time.LocalDate.of(2026, 2, 10)).coutUnitaire(new BigDecimal("450000")).fournisseur("ZKTeco").statut(Lot.StatutLot.ACTIF).build(),
            Lot.builder().numeroLot("LOT-INF-001").article(articles.get(Math.min(20, as - 1))).quantiteInitiale(10).quantiteRestante(10).dateProduction(java.time.LocalDate.of(2026, 5, 20)).coutUnitaire(new BigDecimal("1259000")).fournisseur("HP Inc.").statut(Lot.StatutLot.ACTIF).build(),
            Lot.builder().numeroLot("LOT-EQ-001").article(articles.get(2 % as)).quantiteInitiale(20).quantiteRestante(20).dateProduction(java.time.LocalDate.of(2026, 4, 15)).coutUnitaire(new BigDecimal("285000")).fournisseur("Schneider Electric").statut(Lot.StatutLot.ACTIF).build()
        );
        lotRepository.saveAll(list);
        log.info("{} lots crees", list.size());
    }

    private List<Commande> creerCommandes(List<Fournisseur> fournisseurs, List<Article> articles) {
        if (commandeRepository.count() > 0) return commandeRepository.findAll();
        if (articles.size() < 5 || fournisseurs.size() < 1) {
            log.warn("Pas assez d'articles/fournisseurs pour creer les commandes initiales");
            return commandeRepository.findAll();
        }
        LocalDateTime now = LocalDateTime.now();

        int fs = fournisseurs.size();
        int as = articles.size();

        Commande c1 = commandeRepository.save(Commande.builder()
            .numero("CMD-2026-001").client("Ministere de la Sante")
            .typeFlux(Commande.TypeFlux.ACHAT)
            .total(new BigDecimal("20750000"))
            .statut(Commande.StatutCommande.CONFIRMEE)
            .statutPaiement(Commande.StatutPaiement.PARTIEL)
            .pourcentagePaiement(new BigDecimal("50"))
            .montantPaye(new BigDecimal("10375000"))
            .delaiLivraison(java.time.LocalDate.now().plusDays(15))
            .dateCreation(now.minusDays(10)).dateModification(now.minusDays(2))
            .fournisseur(fournisseurs.get(2 % fs))
            .observation("Commande equipements laboratoire - 50% avance")
            .build());

        ligneCommandeRepository.saveAll(List.of(
            LigneCommande.builder().commande(c1).article(articles.get(5 % as)).quantite(2).prixUnitaire(new BigDecimal("1250000")).sousTotal(new BigDecimal("2500000")).build(),
            LigneCommande.builder().commande(c1).article(articles.get(7 % as)).quantite(2).prixUnitaire(new BigDecimal("8500000")).sousTotal(new BigDecimal("17000000")).build(),
            LigneCommande.builder().commande(c1).article(articles.get(8 % as)).quantite(1).prixUnitaire(new BigDecimal("2100000")).sousTotal(new BigDecimal("2100000")).build()
        ));

        Commande c2 = commandeRepository.save(Commande.builder()
            .numero("CMD-2026-002").client("Direction Generale des Impots")
            .typeFlux(Commande.TypeFlux.ACHAT)
            .total(new BigDecimal("5036000"))
            .statut(Commande.StatutCommande.LIVREE)
            .statutPaiement(Commande.StatutPaiement.PAYE)
            .pourcentagePaiement(new BigDecimal("100"))
            .montantPaye(new BigDecimal("5036000"))
            .dateCreation(now.minusDays(15)).dateModification(now.minusDays(8))
            .fournisseur(fournisseurs.get(7 % fs))
            .observation("Renouvellement parc informatique - regle")
            .build());

        ligneCommandeRepository.save(LigneCommande.builder().commande(c2).article(articles.get(Math.min(20, as - 1))).quantite(4).prixUnitaire(new BigDecimal("1259000")).sousTotal(new BigDecimal("5036000")).build());

        Commande c3 = commandeRepository.save(Commande.builder()
            .numero("CMD-2026-003").client("Societe Togolaise du Beton")
            .typeFlux(Commande.TypeFlux.VENTE)
            .total(new BigDecimal("4450000"))
            .statut(Commande.StatutCommande.BROUILLON)
            .statutPaiement(Commande.StatutPaiement.NON_PAYE)
            .dateCreation(now.minusDays(3)).dateModification(now.minusDays(1))
            .fournisseur(fournisseurs.get(0))
            .observation("Vente equipements industriels")
            .build());

        ligneCommandeRepository.save(LigneCommande.builder().commande(c3).article(articles.get(4 % as)).quantite(5).prixUnitaire(new BigDecimal("890000")).sousTotal(new BigDecimal("4450000")).build());

        log.info("3 commandes STEA creees");
        return List.of(c1, c2, c3);
    }

    private void creerLivraisons(List<Commande> commandes) {
        if (livraisonRepository.count() > 0) return;
        LocalDateTime now = LocalDateTime.now();
        livraisonRepository.saveAll(List.of(
            Livraison.builder().numeroBon("BL-2026-001").datePrevue(now.minusDays(20).toLocalDate()).destinataire("Ministere de la Sante").contenu("Equipements laboratoire").vehicule("Camion benne").nombreColis(3).statut(Livraison.StatutLivraison.LIVREE).commande(commandes.get(0)).statutPaiement(Livraison.StatutPaiementLivraison.PAIEMENT_VALIDE).dateValidationPaiement(now.minusDays(15)).build(),
            Livraison.builder().numeroBon("BL-2026-002").datePrevue(now.minusDays(5).toLocalDate()).destinataire("Direction Generale des Impots").contenu("Ordinateurs portables").vehicule("Fourgon").nombreColis(4).statut(Livraison.StatutLivraison.EN_COURS).commande(commandes.get(1)).statutPaiement(Livraison.StatutPaiementLivraison.EN_ATTENTE_PAIEMENT).build(),
            Livraison.builder().numeroBon("BL-2026-003").datePrevue(now.plusDays(3).toLocalDate()).destinataire("Societe Togolaise du Beton").contenu("Postes de soudure").vehicule("Pick-up").nombreColis(5).statut(Livraison.StatutLivraison.EN_ATTENTE).commande(commandes.get(2)).statutPaiement(Livraison.StatutPaiementLivraison.EN_ATTENTE_PAIEMENT).build()
        ));
        log.info("3 livraisons STEA creees");
    }

    private void creerMouvementsStock(List<Article> articles, Utilisateur operateur) {
        if (mouvementStockRepository.count() > 0) return;
        if (articles.size() < 3) {
            log.warn("Pas assez d'articles pour creer les mouvements initiaux");
            return;
        }
        int as = articles.size();
        List<MouvementStock> list = List.of(
            MouvementStock.builder().type(MouvementStock.TypeMouvement.ENTREE).article(articles.get(0 % as)).quantite(5).unite("Un").observation("Reception fournisseur KOHLER").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.SORTIE).article(articles.get(0 % as)).quantite(2).unite("Un").observation("Livraison Ministere Sante").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.ENTREE).article(articles.get(5 % as)).quantite(6).unite("Un").observation("Reception fournisseur HANNA").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.SORTIE).article(articles.get(5 % as)).quantite(2).unite("Un").observation("Livraison CEET Lome").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.ENTREE).article(articles.get(12 % as)).quantite(10).unite("Un").observation("Reception fournisseur ZKTeco").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.SORTIE).article(articles.get(12 % as)).quantite(4).unite("Un").observation("Livraison CEET Lome").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.TRANSFERT).article(articles.get(2 % as)).quantite(5).unite("Un").observation("Transfert vers Magasin Solaire").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.ENTREE).article(articles.get(Math.min(20, as - 1))).quantite(10).unite("Un").observation("Reception fournisseur HP").operateur(operateur).statut(MouvementStock.StatutMouvement.VALIDE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.ENTREE).article(articles.get(10 % as)).quantite(50).unite("Un").observation("Reception fournisseur Fisher").operateur(operateur).statut(MouvementStock.StatutMouvement.EN_ATTENTE).build(),
            MouvementStock.builder().type(MouvementStock.TypeMouvement.AJUSTEMENT).article(articles.get(18 % as)).quantite(1).unite("Un").observation("Ajustement inventaire mobilier").operateur(operateur).statut(MouvementStock.StatutMouvement.EN_ATTENTE).build()
        );
        mouvementStockRepository.saveAll(list);
        log.info("10 mouvements de stock crees");
    }
}
