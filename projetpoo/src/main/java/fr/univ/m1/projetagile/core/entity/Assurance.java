package fr.univ.m1.projetagile.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

  @OneToMany(mappedBy = "assurance", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SouscriptionAssurance> souscriptions = new ArrayList<>();

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

  public List<SouscriptionAssurance> getSouscriptions() {
    return Collections.unmodifiableList(souscriptions);
  }

  public void ajouterSouscription(SouscriptionAssurance s) {
    if (s != null) {
      souscriptions.add(s);
      s.setAssurance(this);
    }
  }

  // Si vous voulez garder la méthode UML, elle doit rester simple
  public void importerGrille(GrilleTarif nouvelleGrille) {
    if (nouvelleGrille != null) {
      this.grille = nouvelleGrille;
    }
  }
}
