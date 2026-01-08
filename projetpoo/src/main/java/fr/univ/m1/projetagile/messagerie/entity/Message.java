package fr.univ.m1.projetagile.messagerie.entity;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
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
  private LocalDateTime dateEnvoi;

  @ManyToOne
  @JoinColumn(name = "expediteur_agent_id")
  private Agent expediteurAgent;

  @ManyToOne
  @JoinColumn(name = "expediteur_loueur_id")
  private Loueur expediteurLoueur;

  @ManyToOne
  @JoinColumn(name = "destinataire_agent_id")
  private Agent destinataireAgent;

  @ManyToOne
  @JoinColumn(name = "destinataire_loueur_id")
  private Loueur destinataireLoueur;

  // Constructeur sans argument pour JPA
  protected Message() {}

  public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
    this.contenu = contenu;
    setExpediteur(expediteur);
    setDestinataire(destinataire);
    this.dateEnvoi = LocalDateTime.now();
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

  public LocalDateTime getDateEnvoi() {
    return dateEnvoi;
  }

  public void setDateEnvoi(LocalDateTime dateEnvoi) {
    this.dateEnvoi = dateEnvoi;
  }

  public Utilisateur getExpediteur() {
    if (expediteurAgent != null) {
      return expediteurAgent;
    }
    return expediteurLoueur;
  }

  public void setExpediteur(Utilisateur expediteur) {
    if (expediteur instanceof Agent) {
      this.expediteurAgent = (Agent) expediteur;
      this.expediteurLoueur = null;
    } else if (expediteur instanceof Loueur) {
      this.expediteurLoueur = (Loueur) expediteur;
      this.expediteurAgent = null;
    } else {
      throw new IllegalArgumentException("L'expéditeur doit être un Agent ou un Loueur");
    }
  }

  public Utilisateur getDestinataire() {
    if (destinataireAgent != null) {
      return destinataireAgent;
    }
    return destinataireLoueur;
  }

  public void setDestinataire(Utilisateur destinataire) {
    if (destinataire instanceof Agent) {
      this.destinataireAgent = (Agent) destinataire;
      this.destinataireLoueur = null;
    } else if (destinataire instanceof Loueur) {
      this.destinataireLoueur = (Loueur) destinataire;
      this.destinataireAgent = null;
    } else {
      throw new IllegalArgumentException("Le destinataire doit être un Agent ou un Loueur");
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
