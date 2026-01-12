package fr.univ.m1.projetagile.VerificationLocation.entity;

import fr.univ.m1.projetagile.core.entity.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "verifications")
public class Verification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column(nullable = false)
  private Integer kilometrageDebut;

  @Column
  private Integer kilometrageFin;

  @Column
  private String photo;

  // Constructeur sans argument pour JPA
  protected Verification() {}

  public Verification(Location location, Integer kilometrageDebut) {
    this.location = location;
    this.kilometrageDebut = kilometrageDebut;
  }

  public Verification(Location location, Integer kilometrageDebut, String photo) {
    this.location = location;
    this.kilometrageDebut = kilometrageDebut;
    this.photo = photo;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Integer getKilometrageDebut() {
    return kilometrageDebut;
  }

  public void setKilometrageDebut(Integer kilometrageDebut) {
    this.kilometrageDebut = kilometrageDebut;
  }

  public Integer getKilometrageFin() {
    return kilometrageFin;
  }

  public void setKilometrageFin(Integer kilometrageFin) {
    if (kilometrageFin != null && this.kilometrageDebut != null && kilometrageFin < this.kilometrageDebut) {
      throw new IllegalArgumentException("kilometrageFin must be greater than or equal to kilometrageDebut");
    }
    this.kilometrageFin = kilometrageFin;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }
}
