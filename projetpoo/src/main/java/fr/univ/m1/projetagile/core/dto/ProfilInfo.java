package fr.univ.m1.projetagile.core.dto;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.commentaire.entity.Commentaire;
import fr.univ.m1.projetagile.core.entity.Vehicule;

public class ProfilInfo {

  private Long idUtilisateur;
  private String nom;
  private String prenom;
  private String email;
  private String adresse;
  private String bio;
  private String nomCommercial;
  private List<Vehicule> vehiculesDisponibles = new ArrayList<>();
  private List<Commentaire> commentaires = new ArrayList<>();
  private double moyenneNotes;
  private long nombreCommentaires;

  // Getters et setters
  public Long getIdUtilisateur() {
    return idUtilisateur;
  }

  public void setIdUtilisateur(Long idUtilisateur) {
    this.idUtilisateur = idUtilisateur;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getNomCommercial() {
    return nomCommercial;
  }

  public void setNomCommercial(String nomCommercial) {
    this.nomCommercial = nomCommercial;
  }

  public List<Vehicule> getVehiculesDisponibles() {
    return vehiculesDisponibles;
  }

  public void setVehiculesDisponibles(List<Vehicule> vehiculesDisponibles) {
    this.vehiculesDisponibles = vehiculesDisponibles;
  }

  public List<Commentaire> getCommentaires() {
    return commentaires;
  }

  public void setCommentaires(List<Commentaire> commentaires) {
    this.commentaires = commentaires;
  }

  public double getMoyenneNotes() {
    return moyenneNotes;
  }

  public void setMoyenneNotes(double moyenneNotes) {
    this.moyenneNotes = moyenneNotes;
  }

  public long getNombreCommentaires() {
    return nombreCommentaires;
  }

  public void setNombreCommentaires(long nombreCommentaires) {
    this.nombreCommentaires = nombreCommentaires;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Profil ===\n");
    if (prenom != null && nom != null) {
      sb.append("Nom: ").append(prenom).append(" ").append(nom).append("\n");
    }
    if (nomCommercial != null) {
      sb.append("Nom commercial: ").append(nomCommercial).append("\n");
    }
    sb.append("Email: ").append(email).append("\n");
    if (adresse != null) {
      sb.append("Adresse: ").append(adresse).append("\n");
    }
    if (bio != null) {
      sb.append("Bio: ").append(bio).append("\n");
    }
    sb.append("\n=== Avis ===\n");
    sb.append("Note moyenne: ").append(String.format("%.1f", moyenneNotes)).append("/5\n");
    sb.append("Nombre d'avis: ").append(nombreCommentaires).append("\n");
    if (!vehiculesDisponibles.isEmpty()) {
      sb.append("\n=== Véhicules disponibles (").append(vehiculesDisponibles.size())
          .append(") ===\n");
      for (Vehicule v : vehiculesDisponibles) {
        sb.append("  - ").append(v.getMarque()).append(" ").append(v.getModele()).append("\n");
      }
    }
    return sb.toString();
  }
}
