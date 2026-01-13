package fr.univ.m1.projetagile.messagerie.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;

/**
 * Service pour gérer l'envoi et la réception de messages entre utilisateurs.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus du repository pour gérer la messagerie. Il
 * s'occupe de la validation des messages, de leur sauvegarde automatique et de la récupération des
 * conversations.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Envoi de messages avec validation automatique</li>
 * <li>Sauvegarde automatique en base de données</li>
 * <li>Récupération des messages par utilisateur</li>
 * <li>Gestion des conversations entre deux utilisateurs</li>
 * <li>Suppression de messages</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * MessagerieService service = new MessagerieService();
 *
 * // Envoyer un message (validation et sauvegarde automatiques)
 * Message msg = service.envoyerMessage(loueur, agent, "Bonjour, le véhicule est-il disponible ?");
 *
 * // Récupérer tous les messages d'un utilisateur
 * List<Message> messages = service.getMessagesUtilisateur(loueur);
 *
 * // Récupérer une conversation
 * List<Message> conversation = service.getConversation(loueur, agent);
 *
 * // Marquer des messages comme lus
 * service.marquerCommeLus(messagesRecus);
 * }</pre>
 *
 * @see Message
 * @see MessageRepository
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
public class MessagerieService {

  private final MessageRepository messageRepository;

  /**
   * Constructeur par défaut. Initialise le repository de messages.
   */
  public MessagerieService() {
    this.messageRepository = new MessageRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param messageRepository le repository de messages à utiliser
   */
  public MessagerieService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  /**
   * Envoie un message d'un utilisateur à un autre avec validation et sauvegarde automatiques.
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Vérifie que l'expéditeur et le destinataire sont valides</li>
   * <li>Crée le message avec le contenu fourni</li>
   * <li>Valide le contenu du message</li>
   * <li>Sauvegarde le message en base de données</li>
   * <li>Retourne le message sauvegardé avec son ID généré</li>
   * </ol>
   *
   * @param expediteur l'utilisateur qui envoie le message (Agent ou Loueur)
   * @param destinataire l'utilisateur qui reçoit le message (Agent ou Loueur)
   * @param contenu le texte du message (max 1000 caractères)
   * @return le message sauvegardé avec son ID généré
   * @throws IllegalArgumentException si l'expéditeur ou le destinataire est null
   * @throws IllegalArgumentException si le contenu n'est pas valide
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Message envoyerMessage(Utilisateur expediteur, Utilisateur destinataire, String contenu) {
    // Validation des paramètres
    if (expediteur == null) {
      throw new IllegalArgumentException("L'expéditeur ne peut pas être null");
    }
    if (destinataire == null) {
      throw new IllegalArgumentException("Le destinataire ne peut pas être null");
    }
    if (expediteur.getIdU() == null) {
      throw new IllegalArgumentException("L'expéditeur doit être enregistré en base de données");
    }
    if (destinataire.getIdU() == null) {
      throw new IllegalArgumentException("Le destinataire doit être enregistré en base de données");
    }

    // Création du message
    Message message = new Message(contenu, expediteur, destinataire);

    // Validation du contenu
    if (!message.verifierContenu()) {
      throw new IllegalArgumentException(
          "Le contenu du message n'est pas valide (vide ou trop long)");
    }

    // Sauvegarde automatique
    return messageRepository.save(message);
  }

  /**
   * Récupère tous les messages d'un utilisateur (envoyés et reçus).
   *
   * @param utilisateur l'utilisateur concerné
   * @return la liste de tous ses messages, triés du plus récent au plus ancien
   * @throws IllegalArgumentException si l'utilisateur est null
   */
  public List<Message> getMessagesUtilisateur(Utilisateur utilisateur) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    return messageRepository.findAllMessagesByUser(utilisateur);
  }

  /**
   * Récupère les messages envoyés par un utilisateur.
   *
   * @param utilisateur l'utilisateur expéditeur
   * @return la liste des messages envoyés
   * @throws IllegalArgumentException si l'utilisateur est null
   */
  public List<Message> getMessagesEnvoyes(Utilisateur utilisateur) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    return messageRepository.findMessagesSentBy(utilisateur);
  }

  /**
   * Récupère les messages reçus par un utilisateur.
   *
   * @param utilisateur l'utilisateur destinataire
   * @return la liste des messages reçus
   * @throws IllegalArgumentException si l'utilisateur est null
   */
  public List<Message> getMessagesRecus(Utilisateur utilisateur) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    return messageRepository.findMessagesReceivedBy(utilisateur);
  }

  /**
   * Récupère la conversation complète entre deux utilisateurs.
   *
   * <p>
   * Retourne tous les messages échangés entre les deux utilisateurs, dans l'ordre chronologique.
   * </p>
   *
   * @param utilisateur1 le premier utilisateur
   * @param utilisateur2 le deuxième utilisateur
   * @return la liste des messages échangés, triés par date croissante
   * @throws IllegalArgumentException si l'un des utilisateurs est null
   */
  public List<Message> getConversation(Utilisateur utilisateur1, Utilisateur utilisateur2) {
    if (utilisateur1 == null || utilisateur2 == null) {
      throw new IllegalArgumentException("Les deux utilisateurs doivent être non null");
    }
    return messageRepository.findConversationBetween(utilisateur1, utilisateur2);
  }

  /**
   * Récupère un message par son identifiant.
   *
   * @param messageId l'identifiant du message
   * @return le message trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public Message getMessageById(Long messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("L'ID du message ne peut pas être null");
    }
    return messageRepository.findById(messageId);
  }

  /**
   * Supprime un message.
   *
   * <p>
   * Note : Cette méthode supprime définitivement le message. Il serait préférable d'implémenter un
   * système de "soft delete" pour permettre la récupération.
   * </p>
   *
   * @param messageId l'identifiant du message à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerMessage(Long messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("L'ID du message ne peut pas être null");
    }
    messageRepository.delete(messageId);
  }

  /**
   * Compte le nombre de messages échangés entre deux utilisateurs.
   *
   * @param utilisateur1 le premier utilisateur
   * @param utilisateur2 le deuxième utilisateur
   * @return le nombre de messages échangés
   */
  public int compterMessagesConversation(Utilisateur utilisateur1, Utilisateur utilisateur2) {
    return getConversation(utilisateur1, utilisateur2).size();
  }

  /**
   * Compte le nombre de messages non lus d'un utilisateur.
   *
   * <p>
   * Note : Cette méthode retourne actuellement le nombre total de messages reçus. Pour un véritable
   * système de "messages non lus", il faudrait ajouter un champ "lu" dans l'entité Message.
   * </p>
   *
   * @param utilisateur l'utilisateur concerné
   * @return le nombre de messages reçus (à améliorer avec un statut "lu")
   */
  public int compterMessagesNonLus(Utilisateur utilisateur) {
    // TODO: Implémenter un véritable système de messages non lus
    // Pour l'instant, retourne simplement le nombre de messages reçus
    return getMessagesRecus(utilisateur).size();
  }

  /**
   * Vérifie si deux utilisateurs ont déjà échangé des messages.
   *
   * @param utilisateur1 le premier utilisateur
   * @param utilisateur2 le deuxième utilisateur
   * @return true s'ils ont déjà échangé au moins un message
   */
  public boolean ontEchangeMessages(Utilisateur utilisateur1, Utilisateur utilisateur2) {
    return !getConversation(utilisateur1, utilisateur2).isEmpty();
  }



}
