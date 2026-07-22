package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuration_systeme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfigurationSysteme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    @Builder.Default
    private String langue = "FR";

    @Column(length = 10)
    @Builder.Default
    private String fuseauHoraire = "UTC+0";

    @Column(name = "unite_poids", length = 10)
    @Builder.Default
    private String unitePoids = "kg";

    @Column(name = "unite_volume", length = 10)
    @Builder.Default
    private String uniteVolume = "L";

    @Column(name = "seuil_alerte_global")
    @Builder.Default
    private Integer seuilAlerteGlobal = 20;

    @Column(name = "frequence_analyse", length = 20)
    @Builder.Default
    private String frequenceAnalyse = "HEBDOMADAIRE";

    @Column(name = "notif_email")
    @Builder.Default
    private Boolean notifEmail = true;

    @Column(name = "notif_in_app")
    @Builder.Default
    private Boolean notifInApp = true;

    @Column(name = "notif_sms")
    @Builder.Default
    private Boolean notifSms = false;

    @Column(name = "entreprise_nom", length = 200)
    @Builder.Default
    private String entrepriseNom = "STEA SARL";

    @Column(name = "entreprise_adresse", length = 300)
    private String entrepriseAdresse;

    @Column(name = "entreprise_email", length = 150)
    private String entrepriseEmail;

    @Column(name = "entreprise_telephone", length = 30)
    private String entrepriseTelephone;

    @Column(name = "entreprise_logo", length = 500)
    private String entrepriseLogo;
}
