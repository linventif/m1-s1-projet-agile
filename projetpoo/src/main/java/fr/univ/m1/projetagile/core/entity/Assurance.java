package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assurances")
public class Assurance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nom;

  @ManyToOne
  @JoinColumn(name = "grille_tarif_id", nullable = false)
  private GrilleTarif grille;

  protected Assurance() {}

  public Assurance(String nom, GrilleTarif grille) {
    this.nom = nom;
    this.grille = grille;
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

  public GrilleTarif getGrille() {
    return grille;
  }

  public void setGrille(GrilleTarif grille) {
    this.grille = grille;
  }

  // Si vous voulez garder la méthode UML, elle doit rester simple
  public void importerGrille(GrilleTarif nouvelleGrille) {
    if (nouvelleGrille != null) {
      this.grille = nouvelleGrille;
    }
  }
}
