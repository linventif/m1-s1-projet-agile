package fr.univ.m1.projetagile.core.dto;

public class FactureOptionsMensuelleDTO {
  private Long loueurId;
  private String nomLoueur;
  private int annee;
  private int mois;
  private double montantTotalOptions;

  public Long getLoueurId() {
    return loueurId;
  }

  public void setLoueurId(Long loueurId) {
    this.loueurId = loueurId;
  }

  public String getNomLoueur() {
    return nomLoueur;
  }

  public void setNomLoueur(String nomLoueur) {
    this.nomLoueur = nomLoueur;
  }

  public int getAnnee() {
    return annee;
  }

  public void setAnnee(int annee) {
    this.annee = annee;
  }

  public int getMois() {
    return mois;
  }

    this.mois = mois;
  }

  public double getMontantTotalOptions() {
    return montantTotalOptions;
  }

  public void setMontantTotalOptions(double montantTotalOptions) {
    this.montantTotalOptions = montantTotalOptions;
  }
}
