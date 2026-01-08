package fr.univ.m1.projetagile.messagerie;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des messages
 */
public class MessageRepository {

  /**
   * Enregistre un message dans la base de données (création ou mise à jour)
   *
   * @param message le message à enregistrer
   * @return le message enregistré avec son ID généré
   */
  public Message save(Message message) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
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
   * Récupère tous les messages envoyés par un utilisateur
   *
   * @param utilisateur l'utilisateur expéditeur
   * @return la liste des messages envoyés
   */
  public List<Message> findMessagesSentBy(Utilisateur utilisateur) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT m FROM Message m WHERE "
          + "(m.expediteurAgent.idU = :userId OR m.expediteurLoueur.idU = :userId)";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages envoyés", e);
    }
  }

  /**
   * Récupère tous les messages reçus par un utilisateur
   *
   * @param utilisateur l'utilisateur destinataire
   * @return la liste des messages reçus
   */
  public List<Message> findMessagesReceivedBy(Utilisateur utilisateur) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT m FROM Message m WHERE "
          + "(m.destinataireAgent.idU = :userId OR m.destinataireLoueur.idU = :userId)";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages reçus", e);
    }
  }

  /**
   * Récupère tous les messages d'un utilisateur (envoyés + reçus)
   *
   * @param utilisateur l'utilisateur
   * @return la liste de tous ses messages
   */
  public List<Message> findAllMessagesByUser(Utilisateur utilisateur) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT m FROM Message m WHERE "
          + "(m.expediteurAgent.idU = :userId OR m.expediteurLoueur.idU = :userId OR "
          + "m.destinataireAgent.idU = :userId OR m.destinataireLoueur.idU = :userId) "
          + "ORDER BY m.dateEnvoi DESC";

      TypedQuery<Message> query = em.createQuery(jpql, Message.class);
      query.setParameter("userId", utilisateur.getIdU());

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des messages de l'utilisateur", e);
    }
  }

  /**
   * Récupère la conversation entre deux utilisateurs
   *
   * @param user1 premier utilisateur
   * @param user2 deuxième utilisateur
   * @return la liste des messages échangés entre ces deux utilisateurs
   */
  public List<Message> findConversationBetween(Utilisateur user1, Utilisateur user2) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT m FROM Message m WHERE "
          + "((m.expediteurAgent.idU = :user1Id OR m.expediteurLoueur.idU = :user1Id) AND "
          + "(m.destinataireAgent.idU = :user2Id OR m.destinataireLoueur.idU = :user2Id)) OR "
          + "((m.expediteurAgent.idU = :user2Id OR m.expediteurLoueur.idU = :user2Id) AND "
          + "(m.destinataireAgent.idU = :user1Id OR m.destinataireLoueur.idU = :user1Id)) "
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
   * Récupère un message par son ID
   *
   * @param id l'identifiant du message
   * @return le message trouvé ou null
   */
  public Message findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(Message.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du message", e);
    }
  }

  /**
   * Supprime un message de la base de données
   *
   * @param id l'identifiant du message à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
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
