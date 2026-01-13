package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.enums.TypeAgent;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents_particuliers")
@DiscriminatorValue("PARTICULIER")
public class AgentParticulier extends Agent {

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String prenom;

  @Column(name = "telephone", length = 20)
  private String telephone;

  @Column(name = "adresse", length = 200)
  private String adresse;

  // Constructeur sans argument pour JPA
  protected AgentParticulier() {
    super();
  }

  public AgentParticulier(String nom, String prenom, String email, String motDePasse,
      String telephone) {
    super(email, motDePasse, TypeAgent.PARTICULIER);
    this.nom = nom;
    this.prenom = prenom;
    this.telephone = telephone;
  }

  // Getters et Setters - implémentation des méthodes abstraites
  @Override
  public String getNom() {
    return nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public String getPrenom() {
    return prenom;
  }

  @Override
  public void setPrenom(String prenom) {
    this.prenom = prenom;
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
    return false;
  }

  @Override
  public String toString() {
    return "AgentParticulier [id=" + getIdU() + ", nom=" + nom + ", prenom=" + prenom + ", email="
        + getEmail() + ", telephone=" + telephone + "]";
  }
}
