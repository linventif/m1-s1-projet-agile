package fr.univ.m1.projetagile.assurance.entity;

import java.util.Objects;
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
    if (typeVehi == null) {
      throw new IllegalArgumentException("typeVehi ne peut pas être null");
    }
    if (modeleVehi == null || modeleVehi.isBlank()) {
      throw new IllegalArgumentException("modeleVehi ne peut pas être vide");
    }
    if (prix == null || prix < 0) {
      throw new IllegalArgumentException("prix doit être >= 0");
    }
    if (grilleTarif == null) {
      throw new IllegalArgumentException("grilleTarif ne peut pas être null");
    }
    this.typeVehi = typeVehi;
    this.modeleVehi = modeleVehi;
    this.prix = prix;
    this.grilleTarif = grilleTarif;
  }

  public Long getId() {
    return id;
  }

  public TypeV getTypeVehi() {
    return typeVehi;
  }

  public void setTypeVehi(TypeV typeVehi) {
    if (typeVehi == null)
      throw new IllegalArgumentException("typeVehi ne peut pas être null");
    this.typeVehi = typeVehi;
  }

  public String getModeleVehi() {
    return modeleVehi;
  }

  public void setModeleVehi(String modeleVehi) {
    if (modeleVehi == null || modeleVehi.isBlank()) {
      throw new IllegalArgumentException("modeleVehi ne peut pas être vide");
    }
    this.modeleVehi = modeleVehi;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    if (prix == null || prix < 0)
      throw new IllegalArgumentException("prix doit être >= 0");
    this.prix = prix;
  }

  public GrilleTarif getGrilleTarif() {
    return grilleTarif;
  }

  public void setGrilleTarif(GrilleTarif grilleTarif) {
    if (grilleTarif == null)
      throw new IllegalArgumentException("grilleTarif ne peut pas être null");
    this.grilleTarif = grilleTarif;
  }

  @Override
  public String toString() {
    return "TarifVehicule{" + "id=" + id + ", typeVehi=" + typeVehi + ", modeleVehi='" + modeleVehi
        + '\'' + ", prix=" + prix + '}';
  }

  /**
   * Important pour contains()/remove() sur List : - si l'id est non null, on compare par id
   * (entités persistées) - sinon, fallback sur (type, modele, prix) (avant persistance)
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TarifVehicule))
      return false;
    TarifVehicule that = (TarifVehicule) o;

    if (this.id != null && that.id != null) {
      return Objects.equals(this.id, that.id);
    }
    return typeVehi == that.typeVehi && Objects.equals(modeleVehi, that.modeleVehi)
        && Objects.equals(prix, that.prix);
  }

  @Override
  public int hashCode() {
    if (id != null)
      return Objects.hash(id);
    return Objects.hash(typeVehi, modeleVehi, prix);
  }
}
