package fr.univ.m1.projetagile.core.dto;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeAgent;

/**
 * DTO pour représenter le profil d'un agent avec ses informations enrichies
 */
public class AgentDTO {

  private Long idU;
  private String email;
  private TypeAgent typeAgent; // PARTICULIER ou PROFESSIONNEL
  private String nom;
  private String prenom; // Seulement pour AgentParticulier
  private String siret; // Seulement pour AgentProfessionnel
  private List<VehiculeDTO> vehicules; // Liste des véhicules disponibles
  private Double noteMoyenne; // Note moyenne de l'agent

  public AgentDTO() {
    this.vehicules = new ArrayList<>();
  }

  public AgentDTO(Long idU, String email, TypeAgent typeAgent, String nom, String prenom,
      String siret, List<VehiculeDTO> vehicules, Double noteMoyenne) {
    this.idU = idU;
    this.email = email;
    this.typeAgent = typeAgent;
    this.nom = nom;
    this.prenom = prenom;
    this.siret = siret;
    this.vehicules = vehicules != null ? vehicules : new ArrayList<>();
    this.noteMoyenne = noteMoyenne;
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

  public TypeAgent getTypeAgent() {
    return typeAgent;
  }

  public void setTypeAgent(TypeAgent typeAgent) {
    this.typeAgent = typeAgent;
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

  public String getSiret() {
    return siret;
  }

  public void setSiret(String siret) {
    this.siret = siret;
  }

  public List<VehiculeDTO> getVehicules() {
    return vehicules;
  }

  public void setVehicules(List<VehiculeDTO> vehicules) {
    this.vehicules = vehicules;
  }

  public Double getNoteMoyenne() {
    return noteMoyenne;
  }

  public void setNoteMoyenne(Double noteMoyenne) {
    this.noteMoyenne = noteMoyenne;
  }
}
