package fr.univ.m1.projetagile.core.entity;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Utilisateur {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "utilisateur_id_seq")
  @jakarta.persistence.SequenceGenerator(name = "utilisateur_id_seq",
      sequenceName = "UTILISATEUR_ID_SEQ", allocationSize = 1, initialValue = 1)
  @Column(name = "ID")
  protected Long idU;

  @Column(nullable = false, unique = true)
  protected String email;

  @Column(nullable = false, name = "motdePasse")
  protected String motDePasse;

  // Constructeur sans argument pour JPA
  protected Utilisateur() {}

  protected Utilisateur(String email, String motDePasse) {
    this.email = email;
    this.motDePasse = motDePasse;
  }

  protected Utilisateur(Long idU, String email, String motDePasse) {
    this.idU = idU;
    this.email = email;
    this.motDePasse = motDePasse;
  }

  // Getters
  public Long getIdU() {
    return idU;
  }

  public String getEmail() {
    return email;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  // Setters
  public void setEmail(String email) {
    this.email = email;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public boolean verifierMotDePasse(String mdp) {
    return Objects.equals(this.motDePasse, mdp);
  }

  // Méthodes selon UML
  public void contacterAgent(Agent agent) {
    // À implémenter avec le système de messagerie
    // TODO: implémenter la logique de contact avec un agent
  }

  public void contacterLoueur(Loueur loueur) {
    // À implémenter avec le système de messagerie
    // TODO: implémenter la logique de contact avec un loueur
  }

  public boolean seConnecter(String email, String motDePasse) {
    // Vérifie les identifiants et connecte l'utilisateur
    return this.email.equals(email) && verifierMotDePasse(motDePasse);
  }

  public void changerMDP(String ancienMDP, String nouveauMDP) {
    // Change le mot de passe si l'ancien est correct
    if (verifierMotDePasse(ancienMDP)) {
      this.motDePasse = nouveauMDP;
    } else {
      throw new IllegalArgumentException("Ancien mot de passe incorrect");
    }
  }

  public void changerEmail(String nouveauEmail) {
    // Change l'email de l'utilisateur
    if (nouveauEmail != null && !nouveauEmail.trim().isEmpty()) {
      this.email = nouveauEmail;
    }
  }

  @Override
  public String toString() {
    return "[" + idU + "] <" + email + ">";
  }
}
