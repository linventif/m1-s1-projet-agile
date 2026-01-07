package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 1000)
  private String contenu;

  @Column(nullable = false)
  private LocalDateTime date;

  @ManyToOne
  @JoinColumn(name = "expediteur_id", nullable = false)
  private Utilisateur expediteur;

  @ManyToOne
  @JoinColumn(name = "destinataire_id", nullable = false)
  private Utilisateur destinataire;

  // Constructeur sans argument pour JPA
  protected Message() {}

  public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
    this.contenu = contenu;
    this.expediteur = expediteur;
    this.destinataire = destinataire;
    this.date = LocalDateTime.now();
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public String getContenu() {
    return contenu;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Utilisateur getExpediteur() {
    return expediteur;
  }

  public void setExpediteur(Utilisateur expediteur) {
    this.expediteur = expediteur;
  }

  public Utilisateur getDestinataire() {
    return destinataire;
  }

  public void setDestinataire(Utilisateur destinataire) {
    this.destinataire = destinataire;
  }

  // Méthode selon UML
  public boolean verifierContenu() {
    // Vérifie que le contenu du message est valide
    if (contenu == null || contenu.trim().isEmpty()) {
      return false;
    }
    // Vérifier la longueur maximale
    if (contenu.length() > 1000) {
      return false;
    }
    // Autres validations possibles (mots interdits, etc.)
    return true;
  }
}
