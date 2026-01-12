package fr.univ.m1.projetagile.notes.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.notes.entity.Critere;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des critères dans la base de données.
 *
 * <p>
 * Cette classe fournit toutes les opérations CRUD (Create, Read, Update, Delete) ainsi que des
 * requêtes spécialisées pour récupérer les critères.
 * </p>
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
public class CritereRepository {

  /**
   * Enregistre un critère dans la base de données (création ou mise à jour).
   *
   * @param critere le critère à enregistrer
   * @return le critère enregistré avec son ID généré
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public Critere save(Critere critere) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      if (critere.getId() == null) {
        em.persist(critere);
      } else {
        critere = em.merge(critere);
      }

      transaction.commit();
      return critere;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du critère", e);
    }
  }

  /**
   * Récupère un critère par son identifiant unique.
   *
   * @param id l'identifiant du critère à récupérer
   * @return le critère trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Critere findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(Critere.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du critère avec l'ID " + id, e);
    }
  }

  /**
   * Récupère un critère par son nom exact.
   *
   * @param nom le nom du critère à rechercher
   * @return le critère trouvé, ou null s'il n'existe pas
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public Critere findByNom(String nom) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT c FROM Critere c WHERE c.nom = :nom";

      TypedQuery<Critere> query = em.createQuery(jpql, Critere.class);
      query.setParameter("nom", nom);

      List<Critere> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la recherche du critère par nom: " + nom, e);
    }
  }

  /**
   * Récupère tous les critères de la base de données.
   *
   * @return la liste de tous les critères
   * @throws RuntimeException si une erreur survient lors de la récupération
   */
  public List<Critere> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT c FROM Critere c ORDER BY c.nom";
      return em.createQuery(jpql, Critere.class).getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de tous les critères", e);
    }
  }

  /**
   * Supprime un critère de la base de données.
   *
   * @param id l'identifiant du critère à supprimer
   * @throws RuntimeException si une erreur survient lors de la suppression
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      Critere critere = em.find(Critere.class, id);
      if (critere != null) {
        em.remove(critere);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du critère avec l'ID " + id, e);
    }
  }

  /**
   * Compte le nombre total de critères dans la base de données.
   *
   * @return le nombre de critères
   * @throws RuntimeException si une erreur survient lors du comptage
   */
  public Long count() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      String jpql = "SELECT COUNT(c) FROM Critere c";
      return em.createQuery(jpql, Long.class).getSingleResult();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors du comptage des critères", e);
    }
  }
}
