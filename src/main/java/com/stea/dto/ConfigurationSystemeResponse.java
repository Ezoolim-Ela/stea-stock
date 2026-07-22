package com.stea.dto;

import lombok.Data;

@Data
public class ConfigurationSystemeResponse {
    private Long id;
    private String langue;
    private String fuseauHoraire;
    private String unitePoids;
    private String uniteVolume;
    private Integer seuilAlerteGlobal;
    private String frequenceAnalyse;
    private Boolean notifEmail;
    private Boolean notifInApp;
    private Boolean notifSms;
    private String entrepriseNom;
    private String entrepriseAdresse;
    private String entrepriseEmail;
    private String entrepriseTelephone;
    private String entrepriseLogo;
}
