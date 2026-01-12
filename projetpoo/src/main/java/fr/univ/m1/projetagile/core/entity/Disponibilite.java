package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "disponibilites")
public class Disponibilite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @Column(nullable = false, name = "dateDebut")
  private LocalDate dateDebut;

  @Column(nullable = false, name = "dateFin")
  private LocalDate dateFin;

  // Constructeur sans argument pour JPA
  protected Disponibilite() {}

  public Disponibilite(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
    this.vehicule = vehicule;
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
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

  public LocalDate getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDate dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDate getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDate dateFin) {
    this.dateFin = dateFin;
  }

  @Override
  public String toString() {
    return "Disponibilite [id=" + id + ", vehicule=" + vehicule.getMarque() + " "
        + vehicule.getModele() + ", du " + dateDebut + " au " + dateFin + "]";
  }
}
