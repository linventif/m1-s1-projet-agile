package fr.univ.m1.projetagile.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeAgent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_agent", discriminatorType = DiscriminatorType.STRING)
public abstract class Agent extends Utilisateur {

  @Enumerated(EnumType.STRING)
  @Column(name = "type_agent", insertable = false, updatable = false)
  private TypeAgent typeAgent;

  @Column(name = "nom_commercial", length = 100)
  private String nomCommercial;

  @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Vehicule> vehicules = new ArrayList<>();

  // Constructeur sans argument pour JPA
  protected Agent() {
    super();
  }

  // Constructeur pour les sous-classes qui appellent super(email, motDePasse)
  protected Agent(String email, String motDePasse, TypeAgent typeAgent) {
    super(email, motDePasse);
    this.typeAgent = typeAgent;
  }

  public TypeAgent getTypeAgent() {
    if (typeAgent == null) {
      // Déterminer automatiquement depuis la classe concrète
      typeAgent = estProfessionnel() ? TypeAgent.PROFESSIONNEL : TypeAgent.PARTICULIER;
    }
    return typeAgent;
  }

  // Méthodes selon UML
  public List<Vehicule> getVehicules() {
    return Collections.unmodifiableList(vehicules);
  }

  public String getNomCommercial() {
    return nomCommercial;
  }

  public void setNomCommercial(String nomCommercial) {
    this.nomCommercial = nomCommercial;
  }

  public void addVehicule(Vehicule v) {
    if (v != null) {
      vehicules.add(v);
      v.setProprietaire(this);
    }
  }

  public void deleteVehicule(Vehicule v) {
    if (v != null) {
      vehicules.remove(v);
      if (v.getProprietaire() == this) {
        v.setProprietaire(null);
      }
    }
  }

  // Alias pour compatibilité
  public void ajouterVehicule(Vehicule v) {
    addVehicule(v);
  }

  public void retirerVehicule(Vehicule v) {
    deleteVehicule(v);
  }

  public void accepterLocation(Location location) {
    // À implémenter selon la logique métier
    // Change le statut de la location à ACCEPTE
    if (location != null) {
      location.setStatut(StatutLocation.ACCEPTE);
    }
  }

  public void modifierVehicule(Vehicule vehicule, String marque, String modele, String couleur,
      String ville, Double prix) {
    // Modifie les informations d'un véhicule
    if (vehicule != null && vehicules.contains(vehicule)) {
      if (marque != null)
        vehicule.setMarque(marque);
      if (modele != null)
        vehicule.setModele(modele);
      if (couleur != null)
        vehicule.setCouleur(couleur);
      if (ville != null)
        vehicule.setVille(ville);
      if (prix != null)
        vehicule.setPrixJ(prix);
    }
  }

  // Méthode abstraite que les sous-classes doivent implémenter
  public abstract boolean estProfessionnel();
}
