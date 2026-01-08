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

/**
 * Représente un message échangé entre deux utilisateurs de la plateforme.
 *
 * <p>
 * Un message peut être envoyé par un Agent ou un Loueur, et peut être destiné à un Agent ou un
 * Loueur. La classe utilise des relations polymorphes pour gérer les différents types d'expéditeurs
 * et de destinataires.
 * </p>
 *
 * <h2>Contraintes</h2>
 * <ul>
 * <li>Le contenu ne peut pas être vide</li>
 * <li>Le contenu ne peut pas dépasser 1000 caractères</li>
 * <li>Un expéditeur et un destinataire sont obligatoires</li>
 * <li>La date d'envoi est automatiquement définie à la création</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * // Créer un message d'un loueur vers un agent
 * Loueur loueur = new Loueur("Dupont", "Jean", "jean@example.com", "pass");
 * Agent agent = new AgentParticulier("Martin", "Alice", "alice@example.com", "pass", "0612345678");
 *
 * Message message = new Message("Bonjour, je suis intéressé par votre véhicule.", loueur, agent);
 *
 * // Vérifier le contenu avant l'envoi
 * if (message.verifierContenu()) {
 *   messageRepository.save(message);
 * }
 * }</pre>
 *
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 * @see fr.univ.m1.projetagile.core.entity.Agent
 * @see fr.univ.m1.projetagile.core.entity.Loueur
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "messages")
public class Message {

  /**
   * Identifiant unique du message (généré automatiquement).
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Contenu textuel du message (max 1000 caractères).
   */
  @Column(nullable = false, length = 1000)
  private String contenu;

  /**
   * Date et heure d'envoi du message (définie automatiquement à la création).
   */
  @Column(nullable = false)
  private LocalDateTime dateEnvoi;

  /**
   * Expéditeur du message s'il s'agit d'un Agent. Mutuellement exclusif avec
   * {@link #expediteurLoueur}.
   */
  @ManyToOne
  @JoinColumn(name = "expediteur_agent_id")
  private Agent expediteurAgent;

  /**
   * Expéditeur du message s'il s'agit d'un Loueur. Mutuellement exclusif avec
   * {@link #expediteurAgent}.
   */
  @ManyToOne
  @JoinColumn(name = "expediteur_loueur_id")
  private Loueur expediteurLoueur;

  /**
   * Destinataire du message s'il s'agit d'un Agent. Mutuellement exclusif avec
   * {@link #destinataireLoueur}.
   */
  @ManyToOne
  @JoinColumn(name = "destinataire_agent_id")
  private Agent destinataireAgent;

  /**
   * Destinataire du message s'il s'agit d'un Loueur. Mutuellement exclusif avec
   * {@link #destinataireAgent}.
   */
  @ManyToOne
  @JoinColumn(name = "destinataire_loueur_id")
  private Loueur destinataireLoueur;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected Message() {}

  /**
   * Crée un nouveau message avec un contenu, un expéditeur et un destinataire. La date d'envoi est
   * automatiquement définie à l'instant de création.
   *
   * @param contenu le texte du message (max 1000 caractères)
   * @param expediteur l'utilisateur qui envoie le message (Agent ou Loueur)
   * @param destinataire l'utilisateur qui reçoit le message (Agent ou Loueur)
   * @throws IllegalArgumentException si l'expéditeur ou le destinataire n'est ni un Agent ni un
   *         Loueur
   */
  public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
    this.contenu = contenu;
    setExpediteur(expediteur);
    setDestinataire(destinataire);
    this.dateEnvoi = LocalDateTime.now();
  }

  /**
   * Retourne l'identifiant unique du message.
   *
   * @return l'ID du message, ou null si le message n'a pas encore été persisté
   */
  public Long getId() {
    return id;
  }

  /**
   * Retourne le contenu textuel du message.
   *
   * @return le contenu du message
   */
  public String getContenu() {
    return contenu;
  }

  /**
   * Modifie le contenu du message.
   *
   * @param contenu le nouveau contenu (max 1000 caractères)
   */
  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  /**
   * Retourne la date et l'heure d'envoi du message.
   *
   * @return la date d'envoi
   */
  public LocalDateTime getDateEnvoi() {
    return dateEnvoi;
  }

  /**
   * Modifie la date d'envoi du message.
   *
   * @param dateEnvoi la nouvelle date d'envoi
   */
  public void setDateEnvoi(LocalDateTime dateEnvoi) {
    this.dateEnvoi = dateEnvoi;
  }

  /**
   * Retourne l'expéditeur du message (Agent ou Loueur).
   *
   * @return l'utilisateur expéditeur
   */
  public Utilisateur getExpediteur() {
    if (expediteurAgent != null) {
      return expediteurAgent;
    }
    return expediteurLoueur;
  }

  /**
   * Définit l'expéditeur du message. Configure automatiquement soit {@link #expediteurAgent} soit
   * {@link #expediteurLoueur} en fonction du type d'utilisateur.
   *
   * @param expediteur l'utilisateur expéditeur (Agent ou Loueur)
   * @throws IllegalArgumentException si l'expéditeur n'est ni un Agent ni un Loueur
   */
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

  /**
   * Retourne le destinataire du message (Agent ou Loueur).
   *
   * @return l'utilisateur destinataire
   */
  public Utilisateur getDestinataire() {
    if (destinataireAgent != null) {
      return destinataireAgent;
    }
    return destinataireLoueur;
  }

  /**
   * Définit le destinataire du message. Configure automatiquement soit {@link #destinataireAgent}
   * soit {@link #destinataireLoueur} en fonction du type d'utilisateur.
   *
   * @param destinataire l'utilisateur destinataire (Agent ou Loueur)
   * @throws IllegalArgumentException si le destinataire n'est ni un Agent ni un Loueur
   */
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

  /**
   * Vérifie que le contenu du message est valide selon les règles métier.
   *
   * <p>
   * Règles de validation :
   * </p>
   * <ul>
   * <li>Le contenu ne doit pas être null ou vide</li>
   * <li>Le contenu ne doit pas dépasser 1000 caractères</li>
   * </ul>
   *
   * @return true si le contenu est valide, false sinon
   */
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
