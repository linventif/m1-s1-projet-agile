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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idU")
  protected Long idU;

  @Column(nullable = false, unique = true)
  protected String email;

  @Column(nullable = false, name = "motdePasse")
  protected String motDePasse;

  @Column(name = "bio", length = 500)
  protected String bio;

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
  public void setIdU(Long idU) {
    this.idU = idU;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public boolean verifierMotDePasse(String mdp) {
    return Objects.equals(this.motDePasse, mdp);
  }

  // Méthodes abstraites à implémenter dans les sous-classes
  public abstract String getNom();

  public abstract String getPrenom();

  public abstract String getAdresse();

  public abstract void setNom(String nom);

  public abstract void setPrenom(String prenom);

  public abstract void setAdresse(String adresse);

  /**
   * Retourne le nom complet de l'utilisateur
   */
  public String getNomComplet() {
    String nom = getNom();
    String prenom = getPrenom();

    if (nom == null && prenom == null) {
      return "Utilisateur #" + idU;
    }
    if (nom == null) {
      return prenom;
    }
    if (prenom == null) {
      return nom;
    }
    return prenom + " " + nom;
  }

  // Méthodes selon UML

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

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [id=" + idU + ", email=" + email + "]";
  }
}
