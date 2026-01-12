package fr.univ.m1.projetagile.parrainage.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.parrainage.entity.Parrainage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des parrainages dans la base de données.
 *
 * <p>
 * Cette classe fournit toutes les opérations CRUD (Create, Read, Update, Delete) ainsi que des
 * requêtes spécialisées pour récupérer les parrainages selon différents critères.
 * </p>
 *
 * <h2>Opérations disponibles</h2>
 * <ul>
 * <li>Sauvegarder un parrainage (création ou mise à jour)</li>
 * <li>Récupérer un parrainage par son ID</li>
 * <li>Récupérer le parrain d'un utilisateur parrainé</li>
 * <li>Récupérer tous les parrainés d'un utilisateur parrain</li>
 * <li>Vérifier si un parrainage existe entre deux utilisateurs</li>
 * <li>Supprimer un parrainage</li>
 * </ul>
 *
 * <h2>Gestion des transactions</h2>
 * <p>
 * Toutes les méthodes qui modifient des données (save, delete) gèrent automatiquement les
 * transactions et effectuent un rollback en cas d'erreur.
 * </p>
 *
 * @see Parrainage
 * @see fr.univ.m1.projetagile.core.DatabaseConnection
 *
 * @author Projet Agile M1
 * @version 1.0
 */
public class ParrainageRepository {

  /**
   * Enregistre un parrainage dans la base de données (création ou mise à jour).
   *
   * <p>
   * Si le parrainage n'a pas d'ID (nouveau parrainage), il sera créé avec persist(). Si le
   * parrainage a déjà un ID (parrainage existant), il sera mis à jour avec merge().
   * </p>
   *
   * <p>
   * La méthode gère automatiquement les transactions : elle commence une transaction, effectue
   * l'opération, puis commit. En cas d'erreur, un rollback automatique est effectué.
   * </p>
   *
   * @param parrainage le parrainage à enregistrer (avec ou sans ID)
   * @return le parrainage enregistré avec son ID généré (si création)
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public Parrainage save(Parrainage parrainage) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      if (parrainage.getId() == null) {
        em.persist(parrainage);
      } else {
        parrainage = em.merge(parrainage);
      }

      transaction.commit();
      return parrainage;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du parrainage", e);
    }
  }

  /**
   * Récupère un parrainage par son identifiant unique.
   *
   * @param id l'identifiant du parrainage à récupérer
   * @return le parrainage trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Parrainage findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      return em.find(Parrainage.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du parrainage", e);
    }
  }

  /**
   * Récupère le parrainage d'un utilisateur parrainé (obtient le parrain d'un utilisateur).
   *
   * <p>
   * Cette méthode recherche le parrainage où l'utilisateur spécifié est le parrainé.
   * </p>
   *
   * @param parraineId l'ID de l'utilisateur parrainé
   * @return le parrainage trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Parrainage findByParraineId(Long parraineId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT p FROM Parrainage p WHERE p.parraineId = :parraineId";

      TypedQuery<Parrainage> query = em.createQuery(jpql, Parrainage.class);
      query.setParameter("parraineId", parraineId);

      return query.getResultStream().findFirst().orElse(null);

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération du parrainage pour le parrainé " + parraineId, e);
    }
  }

  /**
   * Récupère tous les parrainages où un utilisateur est le parrain (obtient tous les parrainés d'un
   * utilisateur).
   *
   * <p>
   * Cette méthode recherche tous les parrainages où l'utilisateur spécifié est le parrain.
   * </p>
   *
   * @param parrainId l'ID de l'utilisateur parrain
   * @return la liste des parrainages où cet utilisateur est le parrain (peut être vide)
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Parrainage> findByParrainId(Long parrainId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT p FROM Parrainage p WHERE p.parrainId = :parrainId";

      TypedQuery<Parrainage> query = em.createQuery(jpql, Parrainage.class);
      query.setParameter("parrainId", parrainId);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des parrainages pour le parrain " + parrainId, e);
    }
  }

  /**
   * Vérifie si un parrainage existe entre deux utilisateurs (dans n'importe quel sens).
   *
   * <p>
   * Cette méthode vérifie si un parrainage existe où user1 est le parrain de user2, ou où user2 est
   * le parrain de user1.
   * </p>
   *
   * @param user1Id l'ID du premier utilisateur
   * @param user2Id l'ID du deuxième utilisateur
   * @return true si un parrainage existe entre ces deux utilisateurs, false sinon
   * @throws RuntimeException si une erreur survient lors de la vérification
   */
  public boolean existsParrainageBetween(Long user1Id, Long user2Id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT COUNT(p) FROM Parrainage p WHERE "
          + "(p.parrainId = :user1Id AND p.parraineId = :user2Id) OR "
          + "(p.parrainId = :user2Id AND p.parraineId = :user1Id)";

      TypedQuery<Long> query = em.createQuery(jpql, Long.class);
      query.setParameter("user1Id", user1Id);
      query.setParameter("user2Id", user2Id);

      Long count = query.getSingleResult();
      return count > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification du parrainage entre les utilisateurs " + user1Id + " et "
              + user2Id,
          e);
    }
  }

  /**
   * Vérifie si un utilisateur a déjà un parrain.
   *
   * @param utilisateurId l'ID de l'utilisateur à vérifier
   * @return true si l'utilisateur a déjà un parrain, false sinon
   * @throws RuntimeException si une erreur survient lors de la vérification
   */
  public boolean hasParrain(Long utilisateurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT COUNT(p) FROM Parrainage p WHERE p.parraineId = :utilisateurId";

      TypedQuery<Long> query = em.createQuery(jpql, Long.class);
      query.setParameter("utilisateurId", utilisateurId);

      Long count = query.getSingleResult();
      return count > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification si l'utilisateur " + utilisateurId + " a un parrain", e);
    }
  }

  /**
   * Récupère tous les parrainages de la base de données.
   *
   * @return la liste de tous les parrainages
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Parrainage> findAll() {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT p FROM Parrainage p";

      TypedQuery<Parrainage> query = em.createQuery(jpql, Parrainage.class);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de tous les parrainages", e);
    }
  }

  /**
   * Supprime un parrainage de la base de données.
   *
   * <p>
   * Si le parrainage avec l'ID spécifié n'existe pas, aucune erreur n'est levée (suppression
   * silencieuse). La méthode gère automatiquement la transaction.
   * </p>
   *
   * @param id l'identifiant du parrainage à supprimer
   * @throws RuntimeException si une erreur survient lors de la suppression
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      Parrainage parrainage = em.find(Parrainage.class, id);
      if (parrainage != null) {
        em.remove(parrainage);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du parrainage", e);
    }
  }
}
