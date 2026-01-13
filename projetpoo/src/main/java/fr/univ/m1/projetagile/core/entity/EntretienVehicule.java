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

  /**
   * true si l'entretien a été déclenché automatiquement (par la plateforme), false si décidé /
   * programmé manuellement.
   */
  @Column(nullable = false)
  private Boolean automatique;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "entretien_id", nullable = false)
  private Entretien entretien;

  /**
   * Date à laquelle l'entretien a été effectué.
   */
  @Column(name = "date_entretien", nullable = false)
  private LocalDate dateEntretien;

  /**
   * Kilométrage du véhicule au moment de cet entretien.
   */
  @Column(name = "kilometrage_vehicule", nullable = false)
  private Integer kilometrageVehicule;

  // Constructeur sans argument pour JPA
  protected EntretienVehicule() {}

  // Ancien constructeur (si ton code l'utilise encore, tu peux garder)
  public EntretienVehicule(Boolean automatique, Vehicule vehicule, Entretien entretien) {
    this.automatique = automatique;
    this.vehicule = vehicule;
    this.entretien = entretien;
  }

  // Constructeur complet recommandé
  public EntretienVehicule(Boolean automatique, Vehicule vehicule, Entretien entretien,
      LocalDate dateEntretien, Integer kilometrageVehicule) {
    this.automatique = automatique;
    this.vehicule = vehicule;
    this.entretien = entretien;
    this.dateEntretien = dateEntretien;
    this.kilometrageVehicule = kilometrageVehicule;
  }

  // Getters / Setters

  public Long getId() {
    return id;
  }

  public Boolean getAutomatique() {
    return automatique;
  }

  public void setAutomatique(Boolean automatique) {
    this.automatique = automatique;
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

  public LocalDate getDateEntretien() {
    return dateEntretien;
  }

  public void setDateEntretien(LocalDate dateEntretien) {
    this.dateEntretien = dateEntretien;
  }

  public Integer getKilometrageVehicule() {
    return kilometrageVehicule;
  }

  public void setKilometrageVehicule(Integer kilometrageVehicule) {
    this.kilometrageVehicule = kilometrageVehicule;
  }
}
