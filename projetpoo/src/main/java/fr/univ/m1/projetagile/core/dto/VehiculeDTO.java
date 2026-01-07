package fr.univ.m1.projetagile.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * DTO pour représenter un véhicule avec ses informations enrichies
 */
public class VehiculeDTO {

  private Long id;
  private TypeV type;
  private String marque;
  private String modele;
  private String couleur;
  private String ville; // Lieu du véhicule
  private Double prixJ;
  private boolean disponible;
  private Double noteMoyenne; // Note moyenne calculée
  private List<LocalDate[]> datesDispo; // Dates de disponibilités (début/fin)

  public VehiculeDTO() {
    this.datesDispo = new ArrayList<>();
  }

  public VehiculeDTO(Long id, TypeV type, String marque, String modele, String couleur,
      String ville, Double prixJ, boolean disponible, Double noteMoyenne,
      List<LocalDate[]> datesDispo) {
    this.id = id;
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
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<LocalDate[]> getDatesDispo() {
    return datesDispo;
  }

  public void setDatesDispo(List<LocalDate[]> datesDispo) {
    this.datesDispo = datesDispo;
  }
}
