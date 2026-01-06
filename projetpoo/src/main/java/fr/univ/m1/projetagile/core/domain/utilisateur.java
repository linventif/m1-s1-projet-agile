package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class utilisateur {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String prenom;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String motDePasse;

  @Column(nullable = false)
  private String role; // ADMIN, CLIENT, etc.

  // Constructeur sans argument exigé par JPA
  protected utilisateur() {
  }

  public utilisateur(String nom, String prenom, String email, String motDePasse, String role) {
    this.nom = nom;
    this.prenom = prenom;
    this.email = email;
    this.motDePasse = motDePasse;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
