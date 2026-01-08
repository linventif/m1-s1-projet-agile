package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.enums.TypeAgent;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents_particuliers")
@PrimaryKeyJoinColumn(name = "idU")
@DiscriminatorValue("PARTICULIER")
public class AgentParticulier extends Agent {

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String prenom;

  private String telephone;

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

  // Getters et Setters
  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
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
