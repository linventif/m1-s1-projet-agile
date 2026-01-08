package fr.univ.m1.projetagile.core.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour représenter le profil d'un loueur avec ses informations enrichies
 */
public class LoueurDTO {

  private Long idU;
  private String email;
  private String nom;
  private String prenom;
  private Double noteMoyenne; // Note moyenne du loueur
  private List<LocationDTO> currentLocations; // Locations en cours
  private List<LocationDTO> oldLocations; // Locations terminées (historique)

  public LoueurDTO() {
    this.currentLocations = new ArrayList<>();
    this.oldLocations = new ArrayList<>();
  }

  public LoueurDTO(Long idU, String email, String nom, String prenom, Double noteMoyenne,
      List<LocationDTO> currentLocations, List<LocationDTO> oldLocations) {
    this.idU = idU;
    this.email = email;
    this.nom = nom;
    this.prenom = prenom;
    this.noteMoyenne = noteMoyenne;
    this.currentLocations = currentLocations != null ? currentLocations : new ArrayList<>();
    this.oldLocations = oldLocations != null ? oldLocations : new ArrayList<>();
  }

  // Getters et Setters
  public Long getIdU() {
    return idU;
  }

  public void setIdU(Long idU) {
    this.idU = idU;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getNomComplet() {
    return prenom + " " + nom;
  }

  public Double getNoteMoyenne() {
    return noteMoyenne;
  }

  public void setNoteMoyenne(Double noteMoyenne) {
    this.noteMoyenne = noteMoyenne;
  }

  public List<LocationDTO> getCurrentLocations() {
    return currentLocations;
  }

  public void setCurrentLocations(List<LocationDTO> currentLocations) {
    this.currentLocations = currentLocations;
  }

  public List<LocationDTO> getOldLocations() {
    return oldLocations;
  }

  public void setOldLocations(List<LocationDTO> oldLocations) {
    this.oldLocations = oldLocations;
  }
}
