package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import fr.univ.m1.projetagile.enums.StatutEntretien;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "statut", length = 50)
  private StatutEntretien statut;

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
    this.statut = StatutEntretien.EN_ATTENTE;
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

  public StatutEntretien getStatut() {
    return statut;
  }

  public void setStatut(StatutEntretien statut) {
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
    this.statut = StatutEntretien.PLANIFIE;
  }

  /**
   * Marque l'entretien comme réalisé.
   *
   * @throws IllegalStateException si l'entretien n'a pas été planifié avant d'être marqué comme réalisé
   */
  public void marquerRealise() {
    if (this.statut != StatutEntretien.PLANIFIE) {
      throw new IllegalStateException(
          "L'entretien doit être planifié (statut PLANIFIE) avant de pouvoir être marqué comme réalisé");
    }
    this.dateRealisation = LocalDate.now();
    this.statut = StatutEntretien.REALISE;
  }

  /**
   * Annule l'entretien planifié.
   */
  public void annuler() {
    this.statut = StatutEntretien.ANNULE;
  }
}
