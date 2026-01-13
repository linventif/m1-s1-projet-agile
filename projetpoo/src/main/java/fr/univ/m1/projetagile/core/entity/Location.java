package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;
import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorType;
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

  // Délai d'acceptation en heures (6 heures)
  public static final long DELAI_ACCEPTATION_HEURES = 6L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime dateDebut;

  @Column(nullable = false)
  private LocalDateTime dateFin;

  @Column(nullable = false)
  private LocalDateTime dateCreation;

  @Any
  @AnyDiscriminator(DiscriminatorType.STRING)
  @AnyKeyJavaClass(Long.class)
  @AnyDiscriminatorValue(discriminator = "ADRESSE", entity = Adresse.class)
  @Column(name = "lieu_depot_type", nullable = true)
  @JoinColumn(name = "lieu_depot_id", nullable = true)
  private LieuRestitution lieuDepot;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutLocation statut = StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  // ===== Constructeurs =====

  protected Location() {
    this.dateCreation = LocalDateTime.now();
  }

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, Vehicule vehicule,
      Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.vehicule = vehicule;
    this.loueur = loueur;
    this.dateCreation = LocalDateTime.now();
  }

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, LieuRestitution lieuDepot,
      Vehicule vehicule, Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.lieuDepot = lieuDepot;
    this.vehicule = vehicule;
    this.loueur = loueur;
    this.dateCreation = LocalDateTime.now();
  }

  // ===== Getters / Setters =====

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

  public LieuRestitution getLieuDepot() {
    return lieuDepot;
  }

  public void setLieuDepot(LieuRestitution lieuDepot) {
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

  public LocalDateTime getDateCreation() {
    return dateCreation;
  }

  public void setDateCreation(LocalDateTime dateCreation) {
    this.dateCreation = dateCreation;
  }

  // ======================================================
  // ================== MÉTIER LOCATION ===================
  // ======================================================

  /**
   * Retourne le nombre de jours de location. Exemple : du 1 au 4 = 3 jours
   */
  public int getNombreJours() {
    return (int) ChronoUnit.DAYS.between(this.dateDebut, this.dateFin);
  }

  /**
   * Indique si la location est une Location Longue Durée (LLD). Règle métier : LLD à partir de 7
   * jours.
   */
  public boolean estLongueDuree() {
    return getNombreJours() >= 7;
  }

  /**
   * Vérifie si le délai d'acceptation de 6 heures a expiré pour cette location. Le délai commence à
   * partir de la dateCreation.
   *
   * @return true si le délai d'acceptation a expiré, false sinon
   */
  public boolean delaiAcceptationExpire() {
    if (dateCreation == null) {
      return false;
    }
    LocalDateTime dateExpiration = dateCreation.plusHours(DELAI_ACCEPTATION_HEURES);
    return LocalDateTime.now().isAfter(dateExpiration);
  }

  /**
   * Retourne le temps restant en heures avant l'expiration du délai d'acceptation. Retourne 0 si le
   * délai est déjà expiré.
   *
   * @return le nombre d'heures restantes (peut être fractionnaire)
   */
  public long getHeuresRestantesAvantExpiration() {
    if (dateCreation == null) {
      return 0;
    }
    LocalDateTime dateExpiration = dateCreation.plusHours(DELAI_ACCEPTATION_HEURES);
    LocalDateTime maintenant = LocalDateTime.now();
    if (maintenant.isAfter(dateExpiration)) {
      return 0;
    }
    return ChronoUnit.HOURS.between(maintenant, dateExpiration);
  }
}
