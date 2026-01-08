package fr.univ.m1.projetagile.messagerie.entity;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente un message échangé entre deux utilisateurs de la plateforme.
 *
 * <p>
 * Un message peut être envoyé par n'importe quel type d'utilisateur (Agent ou Loueur) vers
 * n'importe quel autre utilisateur. La classe stocke uniquement les IDs des utilisateurs pour
 * simplifier le modèle et exploiter le polymorphisme.
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
 *
 * @author Projet Agile M1
 * @version 2.0
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
   * ID de l'utilisateur expéditeur du message.
   */
  @Column(name = "expediteur_id", nullable = false)
  private Long expediteurId;

  /**
   * ID de l'utilisateur destinataire du message.
   */
  @Column(name = "destinataire_id", nullable = false)
  private Long destinataireId;

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
   */
  public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
    this.contenu = contenu;
    this.expediteurId = expediteur.getIdU();
    this.destinataireId = destinataire.getIdU();
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
   * Définit l'identifiant unique du message.
   *
   * @param id l'ID du message
   */
  public void setId(Long id) {
    this.id = id;
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
   * Retourne l'ID de l'utilisateur expéditeur.
   *
   * @return l'ID de l'expéditeur
   */
  public Long getExpediteurId() {
    return expediteurId;
  }

  /**
   * Définit l'ID de l'utilisateur expéditeur.
   *
   * @param expediteurId l'ID de l'expéditeur
   */
  public void setExpediteurId(Long expediteurId) {
    this.expediteurId = expediteurId;
  }

  /**
   * Retourne l'ID de l'utilisateur destinataire.
   *
   * @return l'ID du destinataire
   */
  public Long getDestinataireId() {
    return destinataireId;
  }

  /**
   * Définit l'ID de l'utilisateur destinataire.
   *
   * @param destinataireId l'ID du destinataire
   */
  public void setDestinataireId(Long destinataireId) {
    this.destinataireId = destinataireId;
  }

  /**
   * Retourne l'utilisateur expéditeur du message. Charge l'utilisateur depuis la base de données.
   *
   * @return l'utilisateur expéditeur, ou null si non trouvé
   */
  public Utilisateur getExpediteur() {
    if (expediteurId == null) {
      return null;
    }
    return DatabaseConnection.getEntityManager().find(Utilisateur.class, expediteurId);
  }

  /**
   * Définit l'expéditeur du message.
   *
   * @param expediteur l'utilisateur expéditeur
   */
  public void setExpediteur(Utilisateur expediteur) {
    this.expediteurId = expediteur != null ? expediteur.getIdU() : null;
  }

  /**
   * Retourne l'utilisateur destinataire du message. Charge l'utilisateur depuis la base de données.
   *
   * @return l'utilisateur destinataire, ou null si non trouvé
   */
  public Utilisateur getDestinataire() {
    if (destinataireId == null) {
      return null;
    }
    return DatabaseConnection.getEntityManager().find(Utilisateur.class, destinataireId);
  }

  /**
   * Définit le destinataire du message.
   *
   * @param destinataire l'utilisateur destinataire
   */
  public void setDestinataire(Utilisateur destinataire) {
    this.destinataireId = destinataire != null ? destinataire.getIdU() : null;
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

  @Override
  public String toString() {
    return "Message [id=" + id + ", expediteur=" + expediteurId + ", destinataire=" + destinataireId
        + ", date=" + dateEnvoi + ", contenu="
        + (contenu != null && contenu.length() > 50 ? contenu.substring(0, 47) + "..." : contenu)
        + "]";
  }
}
