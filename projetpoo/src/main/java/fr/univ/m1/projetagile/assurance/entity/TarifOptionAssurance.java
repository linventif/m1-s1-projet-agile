package fr.univ.m1.projetagile.assurance.entity;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarifs_options")
public class TarifOptionAssurance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, name = "nomOption")
  private String nomOption;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private Double prix;

  @ManyToOne
  @JoinColumn(name = "grille_tarif_id", nullable = false)
  private GrilleTarif grilleTarif;

  protected TarifOptionAssurance() {}

  public TarifOptionAssurance(String nomOption, String description, Double prix,
      GrilleTarif grilleTarif) {
    if (nomOption == null || nomOption.isBlank()) {
      throw new IllegalArgumentException("nomOption ne peut pas être vide");
    }
    if (prix == null || prix < 0) {
      throw new IllegalArgumentException("prix doit être >= 0");
    }
    if (grilleTarif == null) {
      throw new IllegalArgumentException("grilleTarif ne peut pas être null");
    }
    this.nomOption = nomOption;
    this.description = description;
    this.prix = prix;
    this.grilleTarif = grilleTarif;
  }

  public Long getId() {
    return id;
  }

  public String getNomOption() {
    return nomOption;
  }

  public void setNomOption(String nomOption) {
    if (nomOption == null || nomOption.isBlank()) {
      throw new IllegalArgumentException("nomOption ne peut pas être vide");
    }
    this.nomOption = nomOption;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    if (prix == null || prix < 0) {
      throw new IllegalArgumentException("prix doit être >= 0");
    }
    this.prix = prix;
  }

  public GrilleTarif getGrilleTarif() {
    return grilleTarif;
  }

  public void setGrilleTarif(GrilleTarif grilleTarif) {
    if (grilleTarif == null) {
      throw new IllegalArgumentException("grilleTarif ne peut pas être null");
    }
    this.grilleTarif = grilleTarif;
  }

  @Override
  public String toString() {
    return "TarifOptionAssurance{" + "id=" + id + ", nomOption='" + nomOption + '\'' + ", prix="
        + prix + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TarifOptionAssurance))
      return false;
    TarifOptionAssurance that = (TarifOptionAssurance) o;

    if (this.id != null && that.id != null) {
      return Objects.equals(this.id, that.id);
    }
    return Objects.equals(nomOption, that.nomOption) && Objects.equals(prix, that.prix);
  }

  @Override
  public int hashCode() {
    if (id != null)
      return Objects.hash(id);
    return Objects.hash(nomOption, prix);
  }
}
