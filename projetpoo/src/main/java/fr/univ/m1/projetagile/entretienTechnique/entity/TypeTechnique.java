package fr.univ.m1.projetagile.entretienTechnique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_technique")
public class TypeTechnique {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false, name = "km_recommandee")
  private Integer kmRecommandee;

  // Constructeur sans argument pour JPA
  protected TypeTechnique() {}

  public TypeTechnique(String nom, Integer kmRecommandee) {
    this.nom = nom;
    this.kmRecommandee = kmRecommandee;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public Integer getKmRecommandee() {
    return kmRecommandee;
  }

  public void setKmRecommandee(Integer kmRecommandee) {
    this.kmRecommandee = kmRecommandee;
  }
}
