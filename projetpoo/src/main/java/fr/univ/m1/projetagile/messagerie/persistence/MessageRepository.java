package fr.univ.m1.projetagile.messagerie.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des messages dans la base de données.
 *
 * <p>
 * Cette classe fournit toutes les opérations CRUD (Create, Read, Update, Delete) ainsi que des
 * requêtes spécialisées pour récupérer les messages selon différents critères.
 * </p>
 *
 * <h2>Opérations disponibles</h2>
 * <ul>
 * <li>Sauvegarder un message (création ou mise à jour)</li>
 * <li>Récupérer les messages envoyés par un utilisateur</li>
 * <li>Récupérer les messages reçus par un utilisateur</li>
 * <li>Récupérer tous les messages d'un utilisateur</li>
 * <li>Récupérer une conversation entre deux utilisateurs</li>
 * <li>Supprimer un message</li>
 * </ul>
 *
 * <h2>Gestion des transactions</h2>
 * <p>
 * Toutes les méthodes qui modifient des données (save, delete) gèrent automatiquement les
 * transactions et effectuent un rollback en cas d'erreur.
 * </p>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * MessageRepository repo = new MessageRepository();
 *
 * // Sauvegarder un message
 * Message msg = new Message("Bonjour", loueur, agent);
 * msg = repo.save(msg);
 *
 * // Récupérer tous les messages d'un utilisateur
 * List<Message> messages = repo.findAllMessagesByUser(utilisateur);
 *
 * // Récupérer une conversation
 * List<Message> conversation = repo.findConversationBetween(user1, user2);
 *
 * // Supprimer un message
 * repo.delete(msg.getId());
 * }</pre>
 *
 * @see Message
 * @see fr.univ.m1.projetagile.core.DatabaseConnection
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
public class MessageRepository {

  /**
   * Enregistre un message dans la base de données (création ou mise à jour).
   *
   * <p>
   * Si le message n'a pas d'ID (nouveau message), il sera créé avec persist(). Si le message a déjà
   * un ID (message existant), il sera mis à jour avec merge().
   * </p>
   *
   * <p>
   * La méthode gère automatiquement les transactions : elle commence une transaction, effectue
   * l'opération, puis commit. En cas d'erreur, un rollback automatique est effectué.
   * </p>
   *
   * @param message le message à enregistrer (avec ou sans ID)
   * @return le message enregistré avec son ID généré (si création)
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public Message save(Message message) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      if (message.getId() == null) {
        em.persist(message);
      } else {
        message = em.merge(message);
      }

      transaction.commit();
      return message;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du message", e);
    }
  }

  /**
   * Récupère tous les messages envoyés par un utilisateur donné.
   *
   * <p>
   * Cette méthode recherche les messages où l'utilisateur est l'expéditeur.
   * </p>
   *
   * @param utilisateur l'utilisateur expéditeur (Agent ou Loueur)
   * @return la liste des messages envoyés par cet utilisateur (peut être vide)
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Message> findMessagesSentBy(Utilisateur utilisateur) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT m FROM Message m WHERE m.expediteurId = :userId";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages envoyés", e);
    }
  }

  /**
   * Récupère tous les messages reçus par un utilisateur donné.
   *
   * <p>
   * Cette méthode recherche les messages où l'utilisateur est le destinataire.
   * </p>
   *
   * @param utilisateur l'utilisateur destinataire (Agent ou Loueur)
   * @return la liste des messages reçus par cet utilisateur (peut être vide)
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Message> findMessagesReceivedBy(Utilisateur utilisateur) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT m FROM Message m WHERE m.destinataireId = :userId";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages reçus", e);
    }
  }

  /**
   * Récupère tous les messages d'un utilisateur (envoyés ET reçus).
   *
   * <p>
   * Cette méthode combine les messages envoyés et reçus par l'utilisateur. Les résultats sont triés
   * par date d'envoi décroissante (les plus récents en premier).
   * </p>
   *
   * @param utilisateur l'utilisateur concerné (Agent ou Loueur)
   * @return la liste de tous ses messages, triés du plus récent au plus ancien
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Message> findAllMessagesByUser(Utilisateur utilisateur) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT m FROM Message m WHERE "
          + "(m.expediteurId = :userId OR m.destinataireId = :userId) "
          + "ORDER BY m.dateEnvoi DESC";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages de l'utilisateur", e);
    }
  }

  /**
   * Récupère la conversation complète entre deux utilisateurs.
   *
   * <p>
   * Cette méthode retourne tous les messages échangés entre deux utilisateurs, dans les deux sens
   * (user1 → user2 ET user2 → user1). Les messages sont triés par date d'envoi croissante (ordre
   * chronologique).
   * </p>
   *
   * @param user1 le premier utilisateur de la conversation
   * @param user2 le deuxième utilisateur de la conversation
   * @return la liste des messages échangés, dans l'ordre chronologique
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Message> findConversationBetween(Utilisateur user1, Utilisateur user2) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT m FROM Message m WHERE "
          + "((m.expediteurId = :user1Id AND m.destinataireId = :user2Id) OR "
          + "(m.expediteurId = :user2Id AND m.destinataireId = :user1Id)) "
          + "ORDER BY m.dateEnvoi ASC";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("user1Id", user1.getIdU());
      query.setParameter("user2Id", user2.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de la conversation", e);
    }
  }

  /**
   * Récupère un message par son identifiant unique.
   *
   * @param id l'identifiant du message à récupérer
   * @return le message trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Message findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      return em.find(Message.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du message", e);
    }
  }

  /**
   * Supprime un message de la base de données.
   *
   * <p>
   * Si le message avec l'ID spécifié n'existe pas, aucune erreur n'est levée (suppression
   * silencieuse). La méthode gère automatiquement la transaction.
   * </p>
   *
   * @param id l'identifiant du message à supprimer
   * @throws RuntimeException si une erreur survient lors de la suppression
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      Message message = em.find(Message.class, id);
      if (message != null) {
        em.remove(message);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du message", e);
    }
  }
}
