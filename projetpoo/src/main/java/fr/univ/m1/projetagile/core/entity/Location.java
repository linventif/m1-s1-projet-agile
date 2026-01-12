package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import fr.univ.m1.projetagile.core.service.LocationConfig;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeDureeLocation;
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
@Table(name = "locations")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime dateDebut;

  @Column(nullable = false)
  private LocalDateTime dateFin;

  @Column(nullable = true)
  private String lieuDepot;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutLocation statut = StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  // 🔹 Constructeur requis par JPA
  protected Location() {}

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, Vehicule vehicule,
      Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, String lieuDepot,
      Vehicule vehicule, Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.lieuDepot = lieuDepot;
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  // ==================== GETTERS / SETTERS ====================

  public Long getId() {
    return id;
  }

  public LocalDateTime getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDateTime dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDateTime getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDateTime dateFin) {
    this.dateFin = dateFin;
  }

  public String getLieuDepot() {
    return lieuDepot;
  }

  public void setLieuDepot(String lieuDepot) {
    this.lieuDepot = lieuDepot;
  }

  public StatutLocation getStatut() {
    return statut;
  }

  public void setStatut(StatutLocation statut) {
    this.statut = statut;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  public Loueur getLoueur() {
    return loueur;
  }

  public void setLoueur(Loueur loueur) {
    this.loueur = loueur;
  }

  // ==================== LOGIQUE LLD (TICKET #99) ====================

  /**
   * Nombre de jours de location (en jours pleins).
   */
  public int getNombreJours() {
    if (dateDebut == null || dateFin == null) {
      throw new IllegalStateException("Dates de location non définies");
    }

    long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);

    if (jours <= 0) {
      throw new IllegalArgumentException("dateFin doit être après dateDebut");
    }

    return (int) jours;
  }

  /**
   * Indique si la location est une location longue durée (LLD).
   */
  public boolean estLongueDuree() {
    return getNombreJours() >= LocationConfig.SEUIL_LONGUE_DUREE_JOURS;
  }

  /**
   * Type de durée de la location (COURTE ou LONGUE).
   */
  public TypeDureeLocation getTypeDuree() {
    return estLongueDuree() ? TypeDureeLocation.LONGUE_DUREE : TypeDureeLocation.COURTE_DUREE;
  }
}
