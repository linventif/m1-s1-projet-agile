package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicules")
public class Vehicule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "proprietaire_id")
  private Agent proprietaire;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TypeV type; // Utilisation de l'enum TypeV selon UML

  @Column(nullable = false)
  private String marque; // Peugeot, Mercedes

  @Column(nullable = false)
  private String modele;

  private String couleur;

  @Column(nullable = false)
  private String ville;

  @Column(nullable = false, name = "prixJ")
  private Double prixJ; // prix journalier

  @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Disponibilite> datesDispo = new ArrayList<>();

  @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
  private List<Location> locations = new ArrayList<>();

  @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
  private List<SouscriptionOption> souscriptionOptions = new ArrayList<>();

  @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
  private List<EntretienVehicule> entretienVehicules = new ArrayList<>();

  private boolean disponible = true;

  // JPA exige un constructeur sans arguments
  public Vehicule() {}

  public Vehicule(TypeV type, String marque, String modele, String couleur, String ville,
      Double prixJ, Agent proprietaire) {
    this.type = type;
    this.marque = marque;
    this.modele = modele;
    this.couleur = couleur;
    this.ville = ville;
    this.prixJ = prixJ;
    this.disponible = true;
    this.proprietaire = proprietaire;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public TypeV getType() {
    return type;
  }

  public void setType(TypeV type) {
    this.type = type;
  }

  public String getMarque() {
    return marque;
  }

  public void setMarque(String marque) {
    this.marque = marque;
  }

  public String getModele() {
    return modele;
  }

  public void setModele(String modele) {
    this.modele = modele;
  }

  public String getCouleur() {
    return couleur;
  }

  public void setCouleur(String couleur) {
    this.couleur = couleur;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  public Double getPrixJ() {
    return prixJ;
  }

  public void setPrixJ(Double prixJ) {
    this.prixJ = prixJ;
  }

  public boolean isDisponible() {
    return disponible;
  }

  public void setDisponible(boolean disponible) {
    this.disponible = disponible;
  }

  public Agent getProprietaire() {
    return proprietaire;
  }

  public void setProprietaire(Agent proprietaire) {
    this.proprietaire = proprietaire;
  }

  // Relations selon UML
  public List<LocalDate[]> getDatesDispo() {
    LocalDate aujourdhui = LocalDate.now();
    List<LocalDate[]> disponibilitesFutures = new ArrayList<>();

    for (Disponibilite dispo : datesDispo) {
      LocalDate debut = dispo.getDateDebut();
      LocalDate fin = dispo.getDateFin();

      // Ignorer les disponibilités complètement passées
      if (fin.isBefore(aujourdhui)) {
        continue;
      }

      // Si la disponibilité commence avant aujourd'hui, on tronque au jour courant
      LocalDate dateDebutEffective = debut.isBefore(aujourdhui) ? aujourdhui : debut;
      disponibilitesFutures.add(new LocalDate[] {dateDebutEffective, fin});
    }

    return Collections.unmodifiableList(disponibilitesFutures);
  }

  public void ajouterDisponibilite(Disponibilite disponibilite) {
    if (disponibilite != null) {
      datesDispo.add(disponibilite);
      disponibilite.setVehicule(this);
    }
  }

  public List<Location> getLocations() {
    return Collections.unmodifiableList(locations);
  }

  public List<SouscriptionOption> getSouscriptionOptions() {
    return Collections.unmodifiableList(souscriptionOptions);
  }

  public List<EntretienVehicule> getEntretienVehicules() {
    return Collections.unmodifiableList(entretienVehicules);
  }

  // Méthodes selon UML
  public List<Disponibilite> calculerDisponibilites() {
    // À implémenter selon la logique métier
    // Calcule les disponibilités du véhicule
    return Collections.unmodifiableList(datesDispo);
  }

  public boolean csDisponible(java.time.LocalDate dateDebut, java.time.LocalDate dateFin) {
    // Vérifie si le véhicule est disponible pour la période donnée
    if (!disponible) {
      return false;
    }

    // Vérifie les disponibilités
    for (Disponibilite dispo : datesDispo) {
      if (dispo.getDateDebut().isBefore(dateFin) && dispo.getDateFin().isAfter(dateDebut)) {
        // Vérifie s'il n'y a pas de location en conflit
        for (Location loc : locations) {
          if (loc.getStatut() != StatutLocation.ANNULE
              && loc.getStatut() != StatutLocation.TERMINE) {
            java.time.LocalDate locDebut = loc.getDateDebut().toLocalDate();
            java.time.LocalDate locFin = loc.getDateFin().toLocalDate();
            if (locDebut.isBefore(dateFin) && locFin.isAfter(dateDebut)) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  public Double calculerNote() {
    // Calcule la note moyenne du véhicule
    // TODO: Récupérer toutes les NoteV pour ce véhicule et calculer la moyenne
    return 0.0; // Placeholder
  }

  public List<Location> getHistoriqueLocations() {
    // Retourne l'historique des locations du véhicule
    return Collections.unmodifiableList(locations);
  }

  @Override
  public String toString() {
    return "Vehicule [id=" + id + ", type=" + type + ", marque=" + marque + ", modele=" + modele
        + ", couleur=" + couleur + ", ville=" + ville + ", prix=" + prixJ + "€/j"
        + ", proprietaire=" + (proprietaire != null ? proprietaire.getIdU() : "null") + "]";
  }

  // Méthode filter serait typiquement dans un service/repository, pas dans l'entité
  // public static List<Vehicule> filter(...) { ... }
}
