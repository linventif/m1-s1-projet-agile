package fr.univ.m1.projetagile.core.entity;

import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adresses")
public class Adresse implements LieuRestitution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false)
  private String rue;

  @Column(nullable = false, name = "cp")
  private String cp; // code postal

  @Column(nullable = false)
  private String ville;

  // Constructeur sans argument pour JPA
  protected Adresse() {}

  public Adresse(String rue, String cp, String ville) {
    this.rue = rue;
    this.cp = cp;
    this.ville = ville;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public String getRue() {
    return rue;
  }

  public void setRue(String rue) {
    this.rue = rue;
  }

  public String getCp() {
    return cp;
  }

  public void setCp(String cp) {
    this.cp = cp;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  // Implémentation de l'interface LieuRestitution
  @Override
  public String getAdresse() {
    return rue + ", " + cp + " " + ville;
  }

  @Override
  public Double getCoutSupp() {
    return 0.0;
  }

  @Override
  public String toString() {
    return "Adresse [id=" + id + ", rue=" + rue + ", cp=" + cp + ", ville=" + ville + "]";
  }
}
