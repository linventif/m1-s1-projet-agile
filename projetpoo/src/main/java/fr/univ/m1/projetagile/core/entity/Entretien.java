package fr.univ.m1.projetagile.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entretien represents a maintenance company that can log in to the system and manage maintenance
 * prices for different vehicle types.
 */
@Entity
@Table(name = "entretiens")
public class Entretien extends Utilisateur {

  @Column(nullable = false)
  private String nomEntreprise;

  @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PrixEntretien> prixEntretiens = new ArrayList<>();

  @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EntretienVehicule> entretienVehicules = new ArrayList<>();

  // Constructeur sans argument pour JPA
  protected Entretien() {
    super();
  }

  public Entretien(String email, String motDePasse, String nomEntreprise) {
    super(email, motDePasse);
    this.nomEntreprise = nomEntreprise;
  }

  // Getters et Setters
  public String getNomEntreprise() {
    return nomEntreprise;
  }

  public void setNomEntreprise(String nomEntreprise) {
    this.nomEntreprise = nomEntreprise;
  }

  // Implémentation des méthodes abstraites de Utilisateur
  @Override
  public String getNom() {
    return nomEntreprise;
  }

  @Override
  public void setNom(String nom) {
    this.nomEntreprise = nom;
  }

  @Override
  public String getPrenom() {
    return null; // Les entreprises d'entretien n'ont pas de prénom
  }

  @Override
  public void setPrenom(String prenom) {
    // Les entreprises d'entretien n'ont pas de prénom
  }

  @Override
  public String getAdresse() {
    return null; // Adresse à ajouter si nécessaire
  }

  @Override
  public void setAdresse(String adresse) {
    // Adresse à ajouter si nécessaire
  }

  public List<PrixEntretien> getPrixEntretiens() {
    return Collections.unmodifiableList(prixEntretiens);
  }

  public List<EntretienVehicule> getEntretienVehicules() {
    return Collections.unmodifiableList(entretienVehicules);
  }

  // Méthodes de gestion des prix
  public PrixEntretien ajouterPrixEntretien(PrixEntretien prixEntretien) {
    if (prixEntretien != null && !prixEntretiens.contains(prixEntretien)) {
      prixEntretiens.add(prixEntretien);
      prixEntretien.setEntretien(this);
    }
    return prixEntretien;
  }

  public void supprimerPrixEntretien(PrixEntretien prixEntretien) {
    if (prixEntretien != null) {
      prixEntretiens.remove(prixEntretien);
      prixEntretien.setEntretien(null);
    }
  }

  // Méthodes de gestion des entretiens de véhicules
  public EntretienVehicule ajouterEntretienVehicule(EntretienVehicule entretienVehicule) {
    if (entretienVehicule != null && !entretienVehicules.contains(entretienVehicule)) {
      entretienVehicules.add(entretienVehicule);
      entretienVehicule.setEntretien(this);
    }
    return entretienVehicule;
  }

  public void supprimerEntretienVehicule(EntretienVehicule entretienVehicule) {
    if (entretienVehicule != null) {
      entretienVehicules.remove(entretienVehicule);
      entretienVehicule.setEntretien(null);
    }
  }

  // Méthodes selon UML
  public PrixEntretien definirTarif(TypeV typeVehi, String modeleVehi, Double prix) {
    // Définit un tarif pour un type de véhicule et un modèle
    PrixEntretien prixEntretien = new PrixEntretien(typeVehi, modeleVehi, prix, this);
    prixEntretiens.add(prixEntretien);
    return prixEntretien;
  }

  public void importerTarif(String fichier) {
    // Importe un tarif depuis un fichier
    // TODO: Implémenter l'import depuis fichier (CSV, JSON, etc.)
  }

  public Double calculerTarif(Vehicule vehicule) {
    // Calcule le tarif d'entretien pour un véhicule donné
    for (PrixEntretien prixEntretien : prixEntretiens) {
      if (prixEntretien.getTypeVehi() == vehicule.getType()
          && prixEntretien.getModeleVehi().equals(vehicule.getModele())) {
        return prixEntretien.getPrix();
      }
    }
    return null; // Aucun tarif trouvé
  }

  public EntretienVehicule planifierEntretien(Vehicule vehicule, boolean automatique) {
    // Planifie un entretien pour un véhicule
    EntretienVehicule ev = new EntretienVehicule(automatique, vehicule, this);
    entretienVehicules.add(ev);
    return ev;
  }

  // Implémentation des méthodes abstraites de Utilisateur
  @Override
  public String getNom() {
    return nomEntreprise;
  }

  @Override
  public String getPrenom() {
    return null; // Les entreprises d'entretien n'ont pas de prénom
  }

  @Override
  public String getTelephone() {
    return null; // Peut être ajouté plus tard si nécessaire
  }

  @Override
  public String getAdresse() {
    return null; // Peut être ajouté plus tard si nécessaire
  }

  @Override
  public void setNom(String nom) {
    this.nomEntreprise = nom;
  }

  @Override
  public void setPrenom(String prenom) {
    // Les entreprises d'entretien n'ont pas de prénom
  }

  @Override
  public void setTelephone(String telephone) {
    // Peut être ajouté plus tard si nécessaire
  }

  @Override
  public void setAdresse(String adresse) {
    // Peut être ajouté plus tard si nécessaire
  }
}
