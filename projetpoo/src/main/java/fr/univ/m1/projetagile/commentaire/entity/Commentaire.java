package fr.univ.m1.projetagile.commentaire.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "commentaires",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"auteur_id", "profil_id"})})
public class Commentaire {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "auteur_id", nullable = false)
  private Long auteurId;

  @Column(name = "profil_id")
  private Long profilId;

  @Column(nullable = false, length = 1000)
  private String contenu;

  @Column(nullable = false)
  private int note; // Note de 1 à 5

  @Column(nullable = false)
  private LocalDateTime dateCreation;

  @Column(name = "reponse_id")
  private Long reponseId;

  @Column(name = "date_modification")
  private LocalDateTime dateModification;

  public Commentaire() {}

  public Commentaire(Long auteurId, Long profilId, String contenu, int note) {
    if (auteurId == null || profilId == null) {
      throw new IllegalArgumentException("L'auteur et le profil doivent être définis");
    }
    if (auteurId.equals(profilId)) {
      throw new IllegalArgumentException("Impossible de commenter son propre profil");
    }
    if (note < 1 || note > 5) {
      throw new IllegalArgumentException("La note doit être entre 1 et 5");
    }
    if (contenu == null || contenu.trim().isEmpty()) {
      throw new IllegalArgumentException("Le contenu ne peut pas être vide");
    }
    this.auteurId = auteurId;
    this.profilId = profilId;
    this.contenu = contenu;
    this.note = note;
    this.dateCreation = LocalDateTime.now();
  }

  // Constructeur pour une réponse
  public Commentaire(Long auteurId, Long commentaireOriginalId, String contenu) {
    if (auteurId == null || commentaireOriginalId == null) {
      throw new IllegalArgumentException(
          "L'auteur et le commentaire original doivent être définis");
    }
    if (contenu == null || contenu.trim().isEmpty()) {
      throw new IllegalArgumentException("Le contenu ne peut pas être vide");
    }
    this.auteurId = auteurId;
    this.profilId = null; // Les réponses n'ont pas de profilId
    this.reponseId = commentaireOriginalId;
    this.contenu = contenu;
    this.note = 0; // Les réponses n'ont pas de note
    this.dateCreation = LocalDateTime.now();
  }

  public void modifierContenu(String nouveauContenu) {
    if (nouveauContenu == null || nouveauContenu.trim().isEmpty()) {
      throw new IllegalArgumentException("Le contenu ne peut pas être vide");
    }
    this.contenu = nouveauContenu;
    this.dateModification = LocalDateTime.now();
  }

  public boolean isReponse() {
    return reponseId != null;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public Long getAuteurId() {
    return auteurId;
  }

  public Long getProfilId() {
    return profilId;
  }

  public String getContenu() {
    return contenu;
  }

  public int getNote() {
    return note;
  }

  public LocalDateTime getDateCreation() {
    return dateCreation;
  }

  public Long getReponseId() {
    return reponseId;
  }

  public LocalDateTime getDateModification() {
    return dateModification;
  }

  @Override
  public String toString() {
    if (isReponse()) {
      return "Réponse [id=" + id + ", auteurId=" + auteurId + ", contenu=" + contenu + "]";
    }
    return "Commentaire [id=" + id + ", auteurId=" + auteurId + ", profilId=" + profilId + ", note="
        + note + "/5, contenu=" + contenu + "]";
  }
}
