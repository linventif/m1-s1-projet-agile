package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ControleTechnique {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Vehicule vehicule;

  private LocalDate dateControle;
  private LocalDate dateExpiration;

  private boolean valide;

  // Constructeurs
  public ControleTechnique() {}

  public ControleTechnique(Vehicule vehicule, LocalDate dateControle, LocalDate dateExpiration,
      boolean valide) {
    this.vehicule = vehicule;
    this.dateControle = dateControle;
    this.dateExpiration = dateExpiration;
    this.valide = valide;
  }

  // Getters / setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;

  }

  public LocalDate getDateControle() {
    return dateControle;
  }

  public void setDateControle(LocalDate dateControle) {
    this.dateControle = dateControle;
  }

  public LocalDate getDateExpiration() {
    return dateExpiration;
  }

  public void setDateExpiration(LocalDate dateExpiration) {
    this.dateExpiration = dateExpiration;
  }

  public boolean isValide() {
    return valide;
  }

  public void setValide(boolean valide) {
    this.valide = valide;
  }
}
