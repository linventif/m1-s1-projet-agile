package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

  // Méthodes selon UML
  public double getPrixLocation() {
    if (vehicule != null && vehicule.getPrixJ() != null && dateDebut != null && dateFin != null) {
      long jours = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
      return vehicule.getPrixJ() * jours;
    }
    return 0.0;
  }

  public void louerVehicule(String lieuDepot, LocalDateTime dateDebut, LocalDateTime dateFin, Assurance assurance) {
    // Méthode selon UML - à compléter selon la logique métier
    this.lieuDepot = lieuDepot;
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.statut = StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT;
    // TODO: Gérer la souscription d'assurance si nécessaire
  }

  public void noterVehicule(NoteV note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour le véhicule
  }

  public void noterAgent(NoteA note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour l'agent
  }

  public void genererPDF() {
    // Génère un PDF pour la location
    // TODO: Implémenter la génération de PDF
  }

  public void annuler() {
    // Annule la location
    this.statut = StatutLocation.ANNULE;
  }

  public void terminer() {
    // Termine la location
    this.statut = StatutLocation.TERMINE;
  }

  // JPA exige un constructeur sans arguments
  protected Location() {
  }

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, Vehicule vehicule, Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  public Location(LocalDateTime dateDebut, LocalDateTime dateFin, String lieuDepot, Vehicule vehicule, Loueur loueur) {
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.lieuDepot = lieuDepot;
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  // Getters et setters
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
  
  public double getPrixLocation() {
    // Calcul du nombre de jours de location
    long nombreJours = ChronoUnit.DAYS.between(dateDebut, dateFin);
    
    // Prix de base = prix par jour × nombre de jours
    double prixBase = this.vehicule.getPrixJ() * nombreJours;
    
    // Commission de 10% sur le prix de base
    double commissionProportionnelle = prixBase * 0.1;
    
    // Frais fixes de 2€ par jour
    double fraisFixes = 2.0 * nombreJours;
    
    // Prix total = prix de base + commission + frais fixes
    return prixBase + commissionProportionnelle + fraisFixes;
  }

  // Énumération pour le statut selon UML
  public enum StatutLocation {
    EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT,
    ACCEPTE,
    TERMINE,
    ANNULE;

    // Alias pour compatibilité
    public static StatutLocation EN_ATTENTE = EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT;
  }
}
