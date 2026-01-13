package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "souscription_assurances")
public class SouscriptionAssurance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ElementCollection
  @CollectionTable(name = "souscription_assurance_options",
      joinColumns = @JoinColumn(name = "souscription_id"))
  @Column(name = "option_name")
  private List<String> options = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @ManyToOne
  @JoinColumn(name = "assurance_id", nullable = false)
  private Assurance assurance;

  // Constructeur sans argument pour JPA
  protected SouscriptionAssurance() {}

  public SouscriptionAssurance(Location location, Assurance assurance) {
    this.location = location;
    this.assurance = assurance;
  }

  // Méthode selon UML
  public static SouscriptionAssurance souscrire(Location location, Assurance assurance,
      List<String> options) {
    SouscriptionAssurance souscription = new SouscriptionAssurance(location, assurance);
    if (options != null) {
      for (String opt : options) {
        if (opt != null && !opt.isBlank()) {
          souscription.options.add(opt);
        }
      }
    }
    return souscription;
  }

  public Long getId() {
    return id;
  }

  public List<String> getOptions() {
    return Collections.unmodifiableList(options);
  }

  public void ajouterOption(String option) {
    if (option != null && !option.isBlank()) {
      options.add(option);
    }
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Assurance getAssurance() {
    return assurance;
  }

  public void setAssurance(Assurance assurance) {
    this.assurance = assurance;
  }

  /**
   * Calcule le prix total de la souscription d'assurance : (tarifVehiculeParJour +
   * sommeOptionsParJour) * nbJours
   *
   * nbJours est calculé depuis Location.dateDebut/dateFin (LocalDateTime).
   */
  public Double calculerPrix() {
    if (assurance == null || assurance.getGrille() == null) {
      throw new IllegalStateException("Assurance ou grille tarifaire manquante");
    }
    if (location == null) {
      throw new IllegalStateException("Location manquante");
    }
    if (location.getVehicule() == null) {
      throw new IllegalStateException("Véhicule manquant dans la location");
    }

    // 1) Nb jours (LocalDateTime -> jours)
    LocalDateTime debut = location.getDateDebut();
    LocalDateTime fin = location.getDateFin();
    if (debut == null || fin == null) {
      throw new IllegalStateException("Dates de location manquantes");
    }

    long jours = ChronoUnit.DAYS.between(debut, fin);
    if (jours <= 0) {
      throw new IllegalArgumentException(
          "Dates invalides: la date de fin doit être après la date de début");
    }
    int nbJours = (int) jours;

    // 2) Info véhicule
    TypeV typeVehicule = location.getVehicule().getType();
    String modeleVehicule = location.getVehicule().getModele();

    // 3) Tarif véhicule par jour (via GrilleTarif)
    TarifVehicule tarifVehicule =
        assurance.getGrille().trouverTarifVehicule(typeVehicule, modeleVehicule);

    if (tarifVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun tarif véhicule trouvé pour type=" + typeVehicule + ", modele=" + modeleVehicule);
    }

    double prixVehiculeParJour = tarifVehicule.getPrix();

    // 4) Options par jour
    double prixOptionsParJour = 0.0;
    for (String opt : options) {
      TarifOptionAssurance tarifOpt = assurance.getGrille().trouverTarifOption(opt);
      if (tarifOpt == null) {
        throw new IllegalArgumentException("Option inconnue dans la grille: " + opt);
      }
      prixOptionsParJour += tarifOpt.getPrix();
    }

    return (prixVehiculeParJour + prixOptionsParJour) * nbJours;
  }
}
