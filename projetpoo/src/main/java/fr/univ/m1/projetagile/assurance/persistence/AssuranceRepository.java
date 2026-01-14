package fr.univ.m1.projetagile.assurance.persistence;

import java.util.List;
import fr.univ.m1.projetagile.assurance.entity.Assurance;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link Assurance}.
 * <p>
 * Fournit des opérations CRUD pour les assurances.
 */
public class AssuranceRepository {

  /**
   * Sauvegarde une entité {@link Assurance} en base de données. Si l'assurance ne possède pas
   * encore d'identifiant, elle est créée (persist), sinon elle est mise à jour (merge) dans une
   * transaction.
   *
   * @param assurance l'assurance à sauvegarder
   * @return l'assurance persistée ou fusionnée
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public Assurance save(Assurance assurance) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si l'assurance a déjà un ID, on fait un merge, sinon persist
      if (assurance.getId() == null) {
        em.persist(assurance);
      } else {
        assurance = em.merge(assurance);
      }

      transaction.commit();
      return assurance;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de l'assurance", e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche une assurance par son identifiant.
   *
   * @param id identifiant de l'assurance
   * @return l'assurance trouvée ou null si elle n'existe pas
   */
  public Assurance findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Assurance> query =
          em.createQuery("SELECT a FROM Assurance a " + "JOIN FETCH a.grille " + "WHERE a.id = :id",
              Assurance.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de l'assurance " + id, e);
    }
  }

  /**
   * Récupère toutes les assurances.
   *
   * @return liste de toutes les assurances
   */
  public List<Assurance> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Assurance> query =
          em.createQuery("SELECT a FROM Assurance a " + "JOIN FETCH a.grille " + "ORDER BY a.nom",
              Assurance.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de toutes les assurances", e);
    }
  }

  /**
   * Recherche une assurance par son nom.
   *
   * @param nom le nom de l'assurance
   * @return l'assurance trouvée ou null si elle n'existe pas
   */
  public Assurance findByNom(String nom) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Assurance> query = em.createQuery(
          "SELECT a FROM Assurance a " + "JOIN FETCH a.grille " + "WHERE a.nom = :nom",
          Assurance.class);
      query.setParameter("nom", nom);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de l'assurance par nom " + nom, e);
    }
  }

  /**
   * Récupère toutes les assurances utilisant une grille tarifaire donnée.
   *
   * @param grilleId l'identifiant de la grille tarifaire
   * @return liste des assurances utilisant cette grille
   */
  public List<Assurance> findByGrilleId(Long grilleId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Assurance> query =
          em.createQuery("SELECT a FROM Assurance a " + "JOIN FETCH a.grille g "
              + "WHERE g.id = :grilleId " + "ORDER BY a.nom", Assurance.class);
      query.setParameter("grilleId", grilleId);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des assurances pour la grille " + grilleId, e);
    }
  }

  /**
   * Supprime une assurance de la base de données.
   *
   * @param id identifiant de l'assurance à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      Assurance assurance = em.find(Assurance.class, id);
      if (assurance == null) {
        throw new IllegalArgumentException("Aucune assurance trouvée avec l'identifiant " + id);
      }

      em.remove(assurance);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de l'assurance " + id, e);
    } finally {
      em.close();
    }
  }
}
