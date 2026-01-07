package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.*;

@Entity
@Table(name = "tarifs_vehicules")
public class TarifVehicule {

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
  @JoinColumn(name = "grille_tarif_id", nullable = false)
  private GrilleTarif grilleTarif;

  // Constructeur sans argument pour JPA
  protected TarifVehicule() {}

  public TarifVehicule(TypeV typeVehi, String modeleVehi, Double prix, GrilleTarif grilleTarif) {
    this.typeVehi = typeVehi;
    this.modeleVehi = modeleVehi;
    this.prix = prix;
    this.grilleTarif = grilleTarif;
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

  public GrilleTarif getGrilleTarif() {
    return grilleTarif;
  }

  public void setGrilleTarif(GrilleTarif grilleTarif) {
    this.grilleTarif = grilleTarif;
  }
}

