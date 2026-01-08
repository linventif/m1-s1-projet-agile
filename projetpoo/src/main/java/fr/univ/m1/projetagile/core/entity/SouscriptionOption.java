package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "souscription_options")
public class SouscriptionOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "option_id", nullable = false)
  private Options option;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  // Constructeur sans argument pour JPA
  protected SouscriptionOption() {}

  // >>> constructeur utilisé dans ton service <<<
  public SouscriptionOption(Options option, Location location) {
    this.option = option;
    this.location = location;
  }

  // Getters / Setters
  public Long getId() {
    return id;
  }

  public Options getOption() {
    return option;
  }

  public void setOption(Options option) {
    this.option = option;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  // Méthode selon UML
  public void annulerOption() {
    // À implémenter si tu veux une logique métier spécifique
  }
}
