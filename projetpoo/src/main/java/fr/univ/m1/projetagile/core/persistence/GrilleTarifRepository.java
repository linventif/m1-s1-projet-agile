package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.GrilleTarif;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link GrilleTarif}.
 * <p>
 * Fournit des opérations CRUD pour les grilles tarifaires.
 */
public class GrilleTarifRepository {

  /**
   * Sauvegarde une entité {@link GrilleTarif} en base de données.
   * Si la grille ne possède pas encore d'identifiant, elle est créée (persist),
   * sinon elle est mise à jour (merge) dans une transaction.
   *
   * @param grille la grille tarifaire à sauvegarder
   * @return la grille persistée ou fusionnée
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public GrilleTarif save(GrilleTarif grille) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si la grille a déjà un ID, on fait un merge, sinon persist
      if (grille.getId() == null) {
        em.persist(grille);
      } else {
        grille = em.merge(grille);
      }

      transaction.commit();
      return grille;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la grille tarifaire", e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche une grille tarifaire par son identifiant.
   *
   * @param id identifiant de la grille
   * @return la grille trouvée ou null si elle n'existe pas
   */
  public GrilleTarif findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<GrilleTarif> query = em.createQuery(
          "SELECT g FROM GrilleTarif g " + "LEFT JOIN FETCH g.tarifVehi "
              + "LEFT JOIN FETCH g.tarifOptions " + "WHERE g.id = :id",
          GrilleTarif.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de la grille tarifaire " + id, e);
    }
  }

  /**
   * Récupère toutes les grilles tarifaires.
   *
   * @return liste de toutes les grilles
   */
  public List<GrilleTarif> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<GrilleTarif> query =
          em.createQuery("SELECT DISTINCT g FROM GrilleTarif g " + "LEFT JOIN FETCH g.tarifVehi "
              + "LEFT JOIN FETCH g.tarifOptions", GrilleTarif.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de toutes les grilles tarifaires",
          e);
    }
  }

  /**
   * Supprime une grille tarifaire de la base de données.
   *
   * @param id identifiant de la grille à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      GrilleTarif grille = em.find(GrilleTarif.class, id);
      if (grille == null) {
        throw new IllegalArgumentException(
            "Aucune grille tarifaire trouvée avec l'identifiant " + id);
      }

      em.remove(grille);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la grille tarifaire " + id, e);
    } finally {
      em.close();
    }
  }
}
