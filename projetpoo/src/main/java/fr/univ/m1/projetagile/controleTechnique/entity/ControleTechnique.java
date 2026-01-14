package fr.univ.m1.projetagile.controleTechnique.entity;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "controles_techniques")
public class ControleTechnique {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "vehicule_id")
  private Vehicule vehicule;

  @Column(name = "date_mise_en_circulation")
  private LocalDate dateMiseEnCirculation; // première mise en circulation

  @Column(name = "date_dernier_controle")
  private LocalDate date; // dernier contrôle technique

  @Column(name = "kilometrage_actuel")
  private Integer kilometrageActuel; // mileage actuel

  @Column(name = "kilometrage_dernier_controle")
  private Integer kilometrageDernierControle; // mileage au dernier contrôle

  @Column(name = "date_prochain_controle")
  private LocalDate dateLimite; // prochain contrôle technique (calculer automatiquement)

  @Column(name = "date_dernier_entretien")
  private LocalDate dateDernierEntretien; // dernier entretien

  @Column(name = "resultat")
  private String resultat; // résultat du dernier contrôle technique

  // JPA exige un constructeur sans arguments
  protected ControleTechnique() {}

  public ControleTechnique(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
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

  @Override
  public String toString() {
    return "ControleTechnique [id=" + id + ", dateMiseEnCirculation=" + dateMiseEnCirculation
        + ", date=" + date + ", kilometrageActuel=" + kilometrageActuel
        + ", kilometrageDernierControle=" + kilometrageDernierControle + ", dateLimite="
        + dateLimite + ", dateDernierEntretien=" + dateDernierEntretien + ", resultat=" + resultat
        + "]";
  }
}
