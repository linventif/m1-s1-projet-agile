package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prix_entretiens")
public class PrixEntretien {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "typeVehi")
  private TypeV typeVehi;

  @Column(nullable = false, name = "modeleVehi")
  private String modeleVehi;

  @Column(nullable = false)
  private Double prix;

  @ManyToOne
  @JoinColumn(name = "entretien_id", nullable = false)
  private Entretien entretien;

  // Constructeur sans argument pour JPA
  protected PrixEntretien() {}

  public PrixEntretien(TypeV typeVehi, String modeleVehi, Double prix, Entretien entretien) {
    this.typeVehi = typeVehi;
    this.modeleVehi = modeleVehi;
    this.prix = prix;
    this.entretien = entretien;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public TypeV getTypeVehi() {
    return typeVehi;
  }

  public void setTypeVehi(TypeV typeVehi) {
    this.typeVehi = typeVehi;
  }

  public String getModeleVehi() {
    return modeleVehi;
  }

  public void setModeleVehi(String modeleVehi) {
    this.modeleVehi = modeleVehi;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    this.prix = prix;
  }

  public Entretien getEntretien() {
    return entretien;
  }

  public void setEntretien(Entretien entretien) {
    this.entretien = entretien;
  }
}
