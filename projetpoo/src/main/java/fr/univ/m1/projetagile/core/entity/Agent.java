package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.*;

import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeAgent;

@Entity
@Table(name = "agents")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_agent", discriminatorType = DiscriminatorType.STRING)
public abstract class Agent extends Utilisateur {

  @Enumerated(EnumType.STRING)
  @Column(name = "type_agent", insertable = false, updatable = false)
  private TypeAgent typeAgent;

  @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Vehicule> vehicules = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "agent_options", joinColumns = @JoinColumn(name = "agent_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "option_payante")
  private Set<OptionPayante> optionsActives = new HashSet<>();

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

  public String getProfil() {
    // À implémenter selon les besoins
    // Retourne les informations du profil de l'agent
    return "Profil de l'agent";
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

  public void contracterOption(OptionPayante option) {
    if (option != null) {
      optionsActives.add(option);
    }
  }

  public void annulerOption(OptionPayante option) {
    optionsActives.remove(option);
  }

  // Alias pour compatibilité
  public void activerOption(OptionPayante option) {
    contracterOption(option);
  }

  public void desactiverOption(OptionPayante option) {
    annulerOption(option);
  }

  public boolean aOption(OptionPayante option) {
    return optionsActives.contains(option);
  }

  public Set<OptionPayante> getOptionsActives() {
    return Collections.unmodifiableSet(optionsActives);
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

  public void accepterContratManuel(Location location) {
    // Accepte un contrat de location manuellement
    if (location != null) {
      location.setStatut(StatutLocation.ACCEPTE);
    }
  }

  public void refuserContratManuel(Location location) {
    // Refuse un contrat de location manuellement
    if (location != null) {
      location.setStatut(StatutLocation.ANNULE);
    }
  }

  public void noterLoueur(Loueur loueur, Double note1, Double note2, Double note3) {
    // Permet à l'agent de noter un loueur
    // TODO: Créer une NoteL et la persister
    // NoteL note = new NoteL(note1, note2, note3, this, loueur);
  }

  public void proposerAssurance(Location location, Assurance assurance) {
    // Propose une assurance pour une location
    // TODO: Implémenter la logique de proposition d'assurance
  }

  public void demanderEntretien(Vehicule vehicule, Entretien entretien) {
    // Demande un entretien pour un véhicule
    // TODO: Créer un EntretienVehicule et le persister
    // EntretienVehicule ev = new EntretienVehicule(false, vehicule, entretien);
  }

  public Double calculerNote() {
    // Calcule la note moyenne de l'agent
    // TODO: Récupérer toutes les NoteA pour cet agent et calculer la moyenne
    return 0.0; // Placeholder
  }

  // Méthode abstraite que les sous-classes doivent implémenter
  public abstract boolean estProfessionnel();
}
