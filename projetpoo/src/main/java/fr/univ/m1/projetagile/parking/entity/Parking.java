package fr.univ.m1.projetagile.parking.entity;

import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parkings")
public class Parking implements LieuRestitution {

  /**
   * ID de l'option Parking dans le système
   */
  public static final Long PARKING_OPTION_ID = 5L;

  /**
   * Taux de réduction appliqué au prix total de la location lorsque le lieu de dépôt est un parking
   * (0.9 = 10% de réduction)
   */
  public static final Double DISCOUNT_RATE = 0.9;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String rue;

  @Column(nullable = false)
  private String ville;

  @Column(nullable = false, name = "cp")
  private String cp; // code postal

  @Column(nullable = false)
  private Double prix;

  // Constructeur sans argument pour JPA
  protected Parking() {}

  public Parking(String nom, String rue, String ville, String cp, Double prix) {
    this.nom = nom;
    this.rue = rue;
    this.ville = ville;
    this.cp = cp;
    this.prix = prix;
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

  public String getRue() {
    return rue;
  }

  public void setRue(String rue) {
    this.rue = rue;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  public String getCp() {
    return cp;
  }

  public void setCp(String cp) {
    this.cp = cp;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    this.prix = prix;
  }

  // Implémentation de l'interface LieuRestitution
  @Override
  public String getAdresse() {
    return rue + ", " + cp + " " + ville;
  }

  @Override
  public Double getCoutSupp() {
    return prix;
  }

  @Override
  public String toString() {
    return "Parking [id=" + id + ", nom=" + nom + ", rue=" + rue + ", ville=" + ville + ", cp=" + cp
        + ", prix=" + prix + " EUR]";
  }
}
