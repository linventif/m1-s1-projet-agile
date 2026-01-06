package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicules")
public class Vehicule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type; // voiture, moto, camion

  @Column(nullable = false)
  private String marque; // Peugeot, Mercedes

  @Column(nullable = false)
  private String modele;

  private String couleur;

  private boolean disponible = true;

  @ManyToOne
  @JoinColumn(name = "proprietaire_id")
  private Agent proprietaire;

  // JPA exige un constructeur sans arguments
  protected Vehicule() {
  }

  public Vehicule(String type, String marque, String modele, String couleur) {
    this.type = type;
    this.marque = marque;
    this.modele = modele;
    this.couleur = couleur;
  }

  // Getters (et setters si besoin)
  public Long getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getMarque() {
    return marque;
  }

  public String getModele() {
    return modele;
  }

  public String getCouleur() {
    return couleur;
  }

  public boolean isDisponible() {
    return disponible;
  }

  public void setDisponible(boolean disponible) {
    this.disponible = disponible;
  }

  public Agent getProprietaire() {
    return proprietaire;
  }

  public void setProprietaire(Agent proprietaire) {
    this.proprietaire = proprietaire;
  }
}
