package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  // IDs et types pour l'expéditeur (stockage simple sans relation JPA directe)
  @Column(name = "expediteur_id", nullable = false)
  private Long expediteurId;

  @Column(name = "expediteur_type", nullable = false, length = 50)
  private String expediteurType; // "AGENT_PARTICULIER", "AGENT_PROFESSIONNEL", "LOUEUR"

  // IDs et types pour le destinataire
  @Column(name = "destinataire_id", nullable = false)
  private Long destinataireId;

  @Column(name = "destinataire_type", nullable = false, length = 50)
  private String destinataireType; // "AGENT_PARTICULIER", "AGENT_PROFESSIONNEL", "LOUEUR"

  // Champs transients pour l'accès aux objets (pas persistés)
  private transient Utilisateur expediteur;
  private transient Utilisateur destinataire;

  // Constructeur sans argument pour JPA
  protected Message() {}

  public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
    this.contenu = contenu;
    this.date = LocalDateTime.now();
    setExpediteur(expediteur);
    setDestinataire(destinataire);
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

  // Getters et Setters pour les IDs et types
  public Long getExpediteurId() {
    return expediteurId;
  }

  public void setExpediteurId(Long expediteurId) {
    this.expediteurId = expediteurId;
  }

  public String getExpediteurType() {
    return expediteurType;
  }

  public void setExpediteurType(String expediteurType) {
    this.expediteurType = expediteurType;
  }

  public Long getDestinataireId() {
    return destinataireId;
  }

  public void setDestinataireId(Long destinataireId) {
    this.destinataireId = destinataireId;
  }

  public String getDestinataireType() {
    return destinataireType;
  }

  public void setDestinataireType(String destinataireType) {
    this.destinataireType = destinataireType;
  }

  // Méthodes helper pour gérer les objets Utilisateur
  public Utilisateur getExpediteur() {
    return expediteur;
  }

  public void setExpediteur(Utilisateur expediteur) {
    this.expediteur = expediteur;
    if (expediteur != null) {
      this.expediteurId = expediteur.getIdU();
      // Déterminer le type
      if (expediteur instanceof AgentParticulier) {
        this.expediteurType = "AGENT_PARTICULIER";
      } else if (expediteur instanceof AgentProfessionnel) {
        this.expediteurType = "AGENT_PROFESSIONNEL";
      } else if (expediteur instanceof Loueur) {
        this.expediteurType = "LOUEUR";
      }
    }
  }

  public Utilisateur getDestinataire() {
    return destinataire;
  }

  public void setDestinataire(Utilisateur destinataire) {
    this.destinataire = destinataire;
    if (destinataire != null) {
      this.destinataireId = destinataire.getIdU();
      // Déterminer le type
      if (destinataire instanceof AgentParticulier) {
        this.destinataireType = "AGENT_PARTICULIER";
      } else if (destinataire instanceof AgentProfessionnel) {
        this.destinataireType = "AGENT_PROFESSIONNEL";
      } else if (destinataire instanceof Loueur) {
        this.destinataireType = "LOUEUR";
      }
    }
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
