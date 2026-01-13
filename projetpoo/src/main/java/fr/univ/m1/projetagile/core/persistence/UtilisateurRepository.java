package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository de base pour gérer la persistance des utilisateurs (Agent et Loueur) Cette classe
 * fournit les méthodes CRUD communes pour tous les types d'utilisateurs
 *
 * @param <T> le type d'utilisateur (Agent ou Loueur)
 */
public abstract class UtilisateurRepository<T extends Utilisateur> {

  private final Class<T> entityClass;

  protected UtilisateurRepository(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Enregistre un utilisateur dans la base de données (création ou mise à jour)
   *
   * @param utilisateur l'utilisateur à enregistrer
   * @return l'utilisateur enregistré avec son ID généré
   */
  public T save(T utilisateur) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Si l'utilisateur a déjà un ID, on fait un merge, sinon persist
      if (utilisateur.getIdU() == null) {
        em.persist(utilisateur);
      } else {
        utilisateur = em.merge(utilisateur);
      }

      transaction.commit();
      return utilisateur;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur", e);
    }
  }

  /**
   * Récupère un utilisateur par son ID
   *
   * @param id l'identifiant de l'utilisateur
   * @return l'utilisateur trouvé ou null
   */
  public T findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(entityClass, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de l'utilisateur", e);
    }
  }

  /**
   * Récupère un utilisateur par son email
   *
   * Pour les entités abstraites avec JOINED inheritance (comme Agent), JPA interroge
   * automatiquement toutes les tables concrètes (AgentParticulier, AgentProfessionnel).
   *
   * @param email l'email de l'utilisateur
   * @return l'utilisateur trouvé ou null si aucun utilisateur ne correspond
   */
  public T findByEmail(String email) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<T> query = em.createQuery(
          "SELECT u FROM " + entityClass.getSimpleName() + " u WHERE u.email = :email",
          entityClass);
      query.setParameter("email", email);

      List<T> results = query.getResultList();

      if (results.isEmpty()) {
        throw new IllegalArgumentException("Aucun utilisateur trouvé avec l'email " + email);
      }

      if (results.size() > 1) {
        throw new RuntimeException(
            "Plusieurs utilisateurs trouvés avec l'email " + email + " (incohérence de données)");
      }

      return results.get(0);

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de l'utilisateur par email", e);
    }
  }

  /**
   * Supprime un utilisateur de la base de données
   *
   * @param id l'identifiant de l'utilisateur à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      T utilisateur = em.find(entityClass, id);
      if (utilisateur == null) {
        throw new IllegalArgumentException("Aucun utilisateur trouvé avec l'identifiant " + id);
      }
      em.remove(utilisateur);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
    }
  }

  /**
   * Vérifie si le mot de passe correspond à celui de l'utilisateur avec l'email donné
   *
   * @param email l'email de l'utilisateur
   * @param motDePasse le mot de passe à vérifier
   * @return true si l'email existe et le mot de passe est correct, false sinon
   */
  public boolean verifyPassword(String email, String motDePasse) {
    if (email == null || motDePasse == null) {
      return false;
    }

    T utilisateur = findByEmail(email);
    if (utilisateur == null) {
      return false;
    }

    return utilisateur.verifierMotDePasse(motDePasse);
  }
}
