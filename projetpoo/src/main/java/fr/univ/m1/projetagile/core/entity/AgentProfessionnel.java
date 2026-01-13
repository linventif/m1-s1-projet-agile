package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.enums.TypeAgent;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents_professionnels")
@PrimaryKeyJoinColumn(name = "idU")
@DiscriminatorValue("PROFESSIONNEL")
public class AgentProfessionnel extends Agent {

  @Column(nullable = false)
  private String siret;

  @Column(nullable = false, name = "nom_entreprise")
  private String nomEntreprise;

  @Column(name = "telephone", length = 20)
  private String telephone;

  @Column(name = "adresse", length = 200)
  private String adresse;

  // Constructeur sans argument pour JPA
  protected AgentProfessionnel() {
    super();
  }

  public AgentProfessionnel(String email, String motDePasse, String siret, String nomEntreprise) {
    super(email, motDePasse, TypeAgent.PROFESSIONNEL);
    this.siret = siret;
    this.nomEntreprise = nomEntreprise;
  }

  // Getters et Setters
  public String getSiret() {
    return siret;
  }

  public void setSiret(String siret) {
    this.siret = siret;
  }

  public String getNomEntreprise() {
    return nomEntreprise;
  }

  public void setNomEntreprise(String nomEntreprise) {
    this.nomEntreprise = nomEntreprise;
  }

  // Implémentation des méthodes abstraites
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
    return null; // Les professionnels n'ont pas de prénom
  }

  @Override
  public void setPrenom(String prenom) {
    // Les professionnels n'ont pas de prénom
  }

  @Override
  public String getTelephone() {
    return telephone;
  }

  @Override
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  @Override
  public String getAdresse() {
    return adresse;
  }

  @Override
  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  @Override
  public boolean estProfessionnel() {
    return true;
  }

  @Override
  public String toString() {
    return "AgentProfessionnel [id=" + getIdU() + ", entreprise=" + nomEntreprise + ", email="
        + getEmail() + ", siret=" + siret + "]";
  }
}
