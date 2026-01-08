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

  @Column(nullable = false)
  private String nom;

  // Constructeur sans argument pour JPA
  protected AgentProfessionnel() {
    super();
  }

  public AgentProfessionnel(String email, String motDePasse, String siret, String nom) {
    super(email, motDePasse, TypeAgent.PROFESSIONNEL);
    this.siret = siret;
    this.nom = nom;
  }

  // Getters et Setters
  public String getSiret() {
    return siret;
  }

  public void setSiret(String siret) {
    this.siret = siret;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public boolean estProfessionnel() {
    return true;
  }

  @Override
  public String toString() {
    return "AgentProfessionnel [id=" + getIdU() + ", entreprise=" + nom + ", email=" + getEmail()
        + ", siret=" + siret + "]";
  }
}
