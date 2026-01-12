package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
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

  @Column(name = "annulee", nullable = false)
  private boolean annulee = false;

  @Column(name = "date_annulation")
  private LocalDateTime dateAnnulation;

  // Constructeur sans argument pour JPA
  protected SouscriptionOption() {}

  // constructeur métier
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

  public boolean isAnnulee() {
    return annulee;
  }

  public LocalDateTime getDateAnnulation() {
    return dateAnnulation;
  }

  // Méthode métier d'annulation
  public void annulerOption() {
    if (!this.annulee) {
      this.annulee = true;
      this.dateAnnulation = LocalDateTime.now();
    }
  }
}
