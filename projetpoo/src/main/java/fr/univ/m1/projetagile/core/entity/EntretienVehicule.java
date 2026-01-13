package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entretiens_vehicules")
public class EntretienVehicule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean automatique;

  @Column(name = "date_planification")
  private LocalDate datePlanification;

  @Column(name = "date_realisation")
  private LocalDate dateRealisation;

  @Column(name = "statut", length = 50)
  private String statut; // EN_ATTENTE, PLANIFIE, REALISE, ANNULE

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "entretien_id", nullable = false)
  private Entretien entretien;

  // Constructeur sans argument pour JPA
  protected EntretienVehicule() {}

  public EntretienVehicule(Boolean automatique, Vehicule vehicule, Entretien entretien) {
    this.automatique = automatique;
    this.vehicule = vehicule;
    this.entretien = entretien;
    this.statut = "EN_ATTENTE";
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public Boolean getAutomatique() {
    return automatique;
  }

  public void setAutomatique(Boolean automatique) {
    this.automatique = automatique;
  }

  public LocalDate getDatePlanification() {
    return datePlanification;
  }

  public void setDatePlanification(LocalDate datePlanification) {
    this.datePlanification = datePlanification;
  }

  public LocalDate getDateRealisation() {
    return dateRealisation;
  }

  public void setDateRealisation(LocalDate dateRealisation) {
    this.dateRealisation = dateRealisation;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  public Entretien getEntretien() {
    return entretien;
  }

  public void setEntretien(Entretien entretien) {
    this.entretien = entretien;
  }

  /**
   * Planifie l'entretien pour une date donnée. Cette méthode peut être appelée à tout moment pour
   * planifier ou replanifier un entretien.
   *
   * @param date la date de planification de l'entretien
   * @throws IllegalArgumentException si la date est dans le passé
   */
  public void planifierEntretien(LocalDate date) {
    if (date != null && date.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException(
          "La date de planification ne peut pas être dans le passé");
    }
    this.datePlanification = date;
    this.statut = "PLANIFIE";
  }

  /**
   * Marque l'entretien comme réalisé.
   */
  public void marquerRealise() {
    this.dateRealisation = LocalDate.now();
    this.statut = "REALISE";
  }

  /**
   * Annule l'entretien planifié.
   */
  public void annuler() {
    this.statut = "ANNULE";
  }
}
