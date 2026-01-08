package fr.univ.m1.projetagile.core.dto;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.enums.StatutLocation;

/**
 * DTO pour représenter une location avec ses informations enrichies
 */
public class LocationDTO {

  private Long id;
  private LocalDateTime dateDebut;
  private LocalDateTime dateFin;
  private String lieuDepot;
  private StatutLocation statut;
  private VehiculeDTO vehicule;
  private Double prixTotal;

  public LocationDTO() {}

  public LocationDTO(Long id, LocalDateTime dateDebut, LocalDateTime dateFin, String lieuDepot,
      StatutLocation statut, VehiculeDTO vehicule, Double prixTotal) {
    this.id = id;
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.lieuDepot = lieuDepot;
    this.statut = statut;
    this.vehicule = vehicule;
    this.prixTotal = prixTotal;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDateTime dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDateTime getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDateTime dateFin) {
    this.dateFin = dateFin;
  }

  public String getLieuDepot() {
    return lieuDepot;
  }

  public void setLieuDepot(String lieuDepot) {
    this.lieuDepot = lieuDepot;
  }

  public StatutLocation getStatut() {
    return statut;
  }

  public void setStatut(StatutLocation statut) {
    this.statut = statut;
  }

  public VehiculeDTO getVehicule() {
    return vehicule;
  }

  public void setVehicule(VehiculeDTO vehicule) {
    this.vehicule = vehicule;
  }

  public Double getPrixTotal() {
    return prixTotal;
  }

  public void setPrixTotal(Double prixTotal) {
    this.prixTotal = prixTotal;
  }
}
