package fr.univ.m1.projetagile.parrainage.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.parrainage.entity.Crédit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des crédits dans la base de données.
 *
 * <p>
 * Cette classe fournit toutes les opérations CRUD (Create, Read, Update, Delete) ainsi que des
 * requêtes spécialisées pour récupérer les crédits selon différents critères.
 * </p>
 *
 * <h2>Opérations disponibles</h2>
 * <ul>
 * <li>Sauvegarder un crédit (création ou mise à jour)</li>
 * <li>Récupérer un crédit par son ID</li>
 * <li>Récupérer le crédit d'un utilisateur</li>
 * <li>Vérifier si un utilisateur a un crédit</li>
 * <li>Récupérer tous les crédits</li>
 * <li>Supprimer un crédit</li>
 * </ul>
 *
 * <h2>Gestion des transactions</h2>
 * <p>
 * Toutes les méthodes qui modifient des données (save, delete) gèrent automatiquement les
 * transactions et effectuent un rollback en cas d'erreur.
 * </p>
 *
 * @see Crédit
 * @see fr.univ.m1.projetagile.core.DatabaseConnection
 *
 * @author Projet Agile M1
 * @version 1.0
 */
public class CreditRepository {

  /**
   * Enregistre un crédit dans la base de données (création ou mise à jour).
   *
   * <p>
   * Si le crédit n'a pas d'ID (nouveau crédit), il sera créé avec persist(). Si le crédit a déjà un
   * ID (crédit existant), il sera mis à jour avec merge().
   * </p>
   *
   * <p>
   * La méthode gère automatiquement les transactions : elle commence une transaction, effectue
   * l'opération, puis commit. En cas d'erreur, un rollback automatique est effectué.
   * </p>
   *
   * @param credit le crédit à enregistrer (avec ou sans ID)
   * @return le crédit enregistré avec son ID généré (si création)
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public Crédit save(Crédit credit) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      if (credit.getId() == null) {
        em.persist(credit);
      } else {
        credit = em.merge(credit);
      }

      transaction.commit();
      return credit;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du crédit", e);
    }
  }

  /**
   * Récupère un crédit par son identifiant unique.
   *
   * @param id l'identifiant du crédit à récupérer
   * @return le crédit trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Crédit findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      return em.find(Crédit.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du crédit", e);
    }
  }

  /**
   * Récupère le crédit d'un utilisateur.
   *
   * <p>
   * Cette méthode recherche le crédit associé à l'utilisateur spécifié.
   * </p>
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @return le crédit trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Crédit findByUtilisateurId(Long utilisateurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT c FROM Crédit c WHERE c.utilisateurId = :utilisateurId";

      TypedQuery<Crédit> query = em.createQuery(jpql, Crédit.class);
      query.setParameter("utilisateurId", utilisateurId);

      return query.getResultStream().findFirst().orElse(null);

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération du crédit pour l'utilisateur " + utilisateurId, e);
    }
  }

  /**
   * Vérifie si un utilisateur a un crédit enregistré.
   *
   * @param utilisateurId l'ID de l'utilisateur à vérifier
   * @return true si l'utilisateur a un crédit, false sinon
   * @throws RuntimeException si une erreur survient lors de la vérification
   */
  public boolean hasCredit(Long utilisateurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT COUNT(c) FROM Crédit c WHERE c.utilisateurId = :utilisateurId";

      TypedQuery<Long> query = em.createQuery(jpql, Long.class);
      query.setParameter("utilisateurId", utilisateurId);

      Long count = query.getSingleResult();
      return count > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification si l'utilisateur " + utilisateurId + " a un crédit", e);
    }
  }

  /**
   * Récupère tous les crédits de la base de données.
   *
   * @return la liste de tous les crédits
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Crédit> findAll() {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      String jpql = "SELECT c FROM Crédit c";

      TypedQuery<Crédit> query = em.createQuery(jpql, Crédit.class);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de tous les crédits", e);
    }
  }

  /**
   * Supprime un crédit de la base de données.
   *
   * <p>
   * Si le crédit avec l'ID spécifié n'existe pas, aucune erreur n'est levée (suppression
   * silencieuse). La méthode gère automatiquement la transaction.
   * </p>
   *
   * @param id l'identifiant du crédit à supprimer
   * @throws RuntimeException si une erreur survient lors de la suppression
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      Crédit credit = em.find(Crédit.class, id);
      if (credit != null) {
        em.remove(credit);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du crédit", e);
    }
  }

  /**
   * Supprime le crédit d'un utilisateur.
   *
   * @param utilisateurId l'ID de l'utilisateur dont le crédit doit être supprimé
   * @throws RuntimeException si une erreur survient lors de la suppression
   */
  public void deleteByUtilisateurId(Long utilisateurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      Crédit credit = findByUtilisateurId(utilisateurId);
      if (credit != null) {
        em.remove(credit);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException(
          "Erreur lors de la suppression du crédit de l'utilisateur " + utilisateurId, e);
    }
  }
}
