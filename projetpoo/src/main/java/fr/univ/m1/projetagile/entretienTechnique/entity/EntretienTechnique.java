package fr.univ.m1.projetagile.entretienTechnique.entity;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entretiens_techniques")
public class EntretienTechnique {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "type_technique_id", nullable = false)
  private TypeTechnique typeTechnique;

  @Column(name = "date_entretien", nullable = false)
  private LocalDate date;

  // Constructeur sans argument pour JPA
  protected EntretienTechnique() {}

  public EntretienTechnique(Vehicule vehicule, TypeTechnique typeTechnique, LocalDate date) {
    this.vehicule = vehicule;
    this.typeTechnique = typeTechnique;
    this.date = date;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  public TypeTechnique getTypeTechnique() {
    return typeTechnique;
  }

  public void setTypeTechnique(TypeTechnique typeTechnique) {
    this.typeTechnique = typeTechnique;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
