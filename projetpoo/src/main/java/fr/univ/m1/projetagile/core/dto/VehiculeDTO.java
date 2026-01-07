package fr.univ.m1.projetagile.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * DTO pour représenter un véhicule avec ses informations enrichies
 */
public class VehiculeDTO {

  private Long idV;
  private TypeV type;
  private String marque;
  private String modele;
  private String couleur;
  private String ville; // Lieu du véhicule
  private Double prixJ;
  private boolean disponible;
  private Double noteMoyenne; // Note moyenne calculée
  private List<DisponibiliteDTO> datesDispo; // Dates de disponibilités

  public VehiculeDTO() {
    this.datesDispo = new ArrayList<>();
  }

  public VehiculeDTO(Long idV, TypeV type, String marque, String modele, String couleur,
      String ville, Double prixJ, boolean disponible, Double noteMoyenne,
      List<DisponibiliteDTO> datesDispo) {
    this.idV = idV;
    this.type = type;
    this.marque = marque;
    this.modele = modele;
    this.couleur = couleur;
    this.ville = ville;
    this.prixJ = prixJ;
    this.disponible = disponible;
    this.noteMoyenne = noteMoyenne;
    this.datesDispo = datesDispo != null ? datesDispo : new ArrayList<>();
  }

  // Getters et Setters
  public Long getIdV() {
    return idV;
  }

  public void setIdV(Long idV) {
    this.idV = idV;
  }

  public TypeV getType() {
    return type;
  }

  public void setType(TypeV type) {
    this.type = type;
  }

  public String getMarque() {
    return marque;
  }

  public void setMarque(String marque) {
    this.marque = marque;
  }

  public String getModele() {
    return modele;
  }

  public void setModele(String modele) {
    this.modele = modele;
  }

  public String getCouleur() {
    return couleur;
  }

  public void setCouleur(String couleur) {
    this.couleur = couleur;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  public Double getPrixJ() {
    return prixJ;
  }

  public void setPrixJ(Double prixJ) {
    this.prixJ = prixJ;
  }

  public boolean isDisponible() {
    return disponible;
  }

  public void setDisponible(boolean disponible) {
    this.disponible = disponible;
  }

  public Double getNoteMoyenne() {
    return noteMoyenne;
  }

  public void setNoteMoyenne(Double noteMoyenne) {
    this.noteMoyenne = noteMoyenne;
  }

  public List<DisponibiliteDTO> getDatesDispo() {
    return datesDispo;
  }

  public void setDatesDispo(List<DisponibiliteDTO> datesDispo) {
    this.datesDispo = datesDispo;
  }

  /**
   * Classe interne pour représenter une disponibilité
   */
  public static class DisponibiliteDTO {
    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public DisponibiliteDTO() {}

    public DisponibiliteDTO(Long id, LocalDate dateDebut, LocalDate dateFin) {
      this.id = id;
      this.dateDebut = dateDebut;
      this.dateFin = dateFin;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
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
  }
}
