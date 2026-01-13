package fr.univ.m1.projetagile.core.dto;

import java.time.LocalDate;

/**
 * DTO pour représenter un contrôle technique
 */
public class ControleTechniqueDTO {

  private Long id;
  private LocalDate dateMiseEnCirculation;
  private LocalDate date; // date du dernier contrôle technique
  private Integer kilometrageActuel;
  private Integer kilometrageDernierControle;
  private LocalDate dateLimite; // date du prochain contrôle technique
  private LocalDate dateDernierEntretien;
  private String resultat; // résultat du dernier contrôle technique

  public ControleTechniqueDTO() {}

  public ControleTechniqueDTO(Long id, LocalDate dateMiseEnCirculation, LocalDate date,
      Integer kilometrageActuel, Integer kilometrageDernierControle, LocalDate dateLimite,
      LocalDate dateDernierEntretien, String resultat) {
    this.id = id;
    this.dateMiseEnCirculation = dateMiseEnCirculation;
    this.date = date;
    this.kilometrageActuel = kilometrageActuel;
    this.kilometrageDernierControle = kilometrageDernierControle;
    this.dateLimite = dateLimite;
    this.dateDernierEntretien = dateDernierEntretien;
    this.resultat = resultat;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDateMiseEnCirculation() {
    return dateMiseEnCirculation;
  }

  public void setDateMiseEnCirculation(LocalDate dateMiseEnCirculation) {
    this.dateMiseEnCirculation = dateMiseEnCirculation;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Integer getKilometrageActuel() {
    return kilometrageActuel;
  }

  public void setKilometrageActuel(Integer kilometrageActuel) {
    this.kilometrageActuel = kilometrageActuel;
  }

  public Integer getKilometrageDernierControle() {
    return kilometrageDernierControle;
  }

  public void setKilometrageDernierControle(Integer kilometrageDernierControle) {
    this.kilometrageDernierControle = kilometrageDernierControle;
  }

  public LocalDate getDateLimite() {
    return dateLimite;
  }

  public void setDateLimite(LocalDate dateLimite) {
    this.dateLimite = dateLimite;
  }

  public LocalDate getDateDernierEntretien() {
    return dateDernierEntretien;
  }

  public void setDateDernierEntretien(LocalDate dateDernierEntretien) {
    this.dateDernierEntretien = dateDernierEntretien;
  }

  public String getResultat() {
    return resultat;
  }

  public void setResultat(String resultat) {
    this.resultat = resultat;
  }
}
