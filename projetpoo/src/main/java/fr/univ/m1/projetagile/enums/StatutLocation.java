package fr.univ.m1.projetagile.enums;

public enum StatutLocation {
    EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT,
    ACCEPTE,
    TERMINE,
    ANNULE;

    // Alias pour compatibilité
    public static StatutLocation EN_ATTENTE = EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT;
  
}
