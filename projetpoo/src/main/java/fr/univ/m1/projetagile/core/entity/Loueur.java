package fr.univ.m1.projetagile.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "loueurs")
@PrimaryKeyJoinColumn(name = "idU")
public class Loueur extends Utilisateur {

  @OneToMany(mappedBy = "loueur", cascade = CascadeType.ALL)
  private List<Location> locations = new ArrayList<>();

  // Constructeur sans argument pour JPA
  protected Loueur() {
    super();
  }

  public Loueur(String nom, String prenom, String email, String motDePasse) {
    super(email, motDePasse);
    this.nom = nom;
    this.prenom = prenom;
  }

  // Getters et Setters hérités de Utilisateur pour nom et prenom

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getNomComplet() {
    return prenom + " " + nom;
  }

  // Méthodes selon UML
  public List<Location> getLocations() {
    return Collections.unmodifiableList(locations);
  }

  public void ajouterLocation(Location l) {
    if (l != null) {
      locations.add(l);
      l.setLoueur(this);
    }
  }

  public void consulterProfil() {
    // À implémenter selon les besoins
    // Affiche ou retourne les informations du profil
  }

  public void voirLocationsPrecedentes() {
    // À implémenter selon les besoins
    // Affiche ou retourne les locations précédentes
    getLocations();
  }

  public List<Vehicule> consulterVehiculesDisponibles() {
    // Consulte les véhicules disponibles
    // TODO: Implémenter la logique pour récupérer les véhicules disponibles depuis la base de
    // données
    return new ArrayList<>();
  }

  public void preSignerContrat(Location location) {
    // Prépare la signature d'un contrat de location
    // TODO: Implémenter la logique de pré-signature
    if (location != null) {
      // Logique de pré-signature
    }
  }

  public List<Location> consulterLocations() {
    // Consulte toutes les locations du loueur
    return getLocations();
  }

  public Double calculerNote() {
    // Calcule la note moyenne du loueur
    // TODO: Récupérer toutes les NoteLoueur pour ce loueur et calculer la moyenne
    return 0.0; // Placeholder
  }

  @Override
  public String toString() {
    return "Loueur [id=" + getIdU() + ", nom=" + nom + ", prenom=" + prenom + ", email="
        + getEmail() + "]";
  }
}
