package fr.univ.m1.projetagile.VerificationLocation.persistence;

import fr.univ.m1.projetagile.VerificationLocation.entity.Verification;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link Verification}.
 * <p>
 * Fournit des opérations de type CRUD autour des vérifications :
 * <ul>
 * <li>création et mise à jour d'une vérification via {@link #save(Verification)} ;</li>
 * <li>récupération d'une vérification par son identifiant via {@link #findById(Long)} ;</li>
 * <li>récupération d'une vérification par l'identifiant de la location via
 * {@link #findByLocationId(Long)}.</li>
 * </ul>
 * Les opérations de persistance s'appuient sur un {@link EntityManager} obtenu via
 * {@link fr.univ.m1.projetagile.core.DatabaseConnection}.
 */
public class VerificationRepository {

  /**
   * Sauvegarde une entité {@link Verification} en base de données. Si la vérification ne possède
   * pas encore d'identifiant, elle est créée (persist), sinon elle est mise à jour (merge) dans une
   * transaction.
   *
   * @param verification la vérification à sauvegarder
   * @return la vérification persistée ou fusionnée, potentiellement détachée après la fermeture de
   *         l'EntityManager
   * @throws RuntimeException si une erreur survient lors de l'enregistrement ou de la gestion de la
   *         transaction
   */
  public Verification save(Verification verification) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si la vérification a déjà un ID, on fait un merge, sinon persist
      if (verification.getId() == null) {
        em.persist(verification);
      } else {
        verification = em.merge(verification);
      }

      transaction.commit();
      return verification;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la vérification", e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche une vérification par son identifiant.
   *
   * @param id identifiant de la vérification
   * @return la vérification trouvée ou null si elle n'existe pas
   */
  public Verification findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Verification> query =
          em.createQuery("SELECT v FROM Verification v JOIN FETCH v.location WHERE v.id = :id",
              Verification.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de la vérification " + id, e);
    }
  }

  /**
   * Recherche une vérification par l'identifiant de la location associée.
   *
   * @param locationId identifiant de la location
   * @return la vérification trouvée ou null si elle n'existe pas
   */
  public Verification findByLocationId(Long locationId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Verification> query = em.createQuery(
          "SELECT v FROM Verification v JOIN FETCH v.location WHERE v.location.id = :locationId",
          Verification.class);
      query.setParameter("locationId", locationId);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de la vérification pour la location " + locationId, e);
    }
  }

  /**
   * Récupère le dernier kilométrage disponible pour un véhicule donné. Le kilométrage est recherché
   * dans toutes les vérifications des locations du véhicule. Si une vérification a un kilométrage
   * de fin, celui-ci est utilisé, sinon le kilométrage de début. Le kilométrage le plus récent
   * (basé sur la date de fin de location) est retourné.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return le dernier kilométrage trouvé, ou null si aucune vérification n'existe pour ce véhicule
   */
  public Integer getDernierKilometrage(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      // Récupérer toutes les vérifications pour les locations de ce véhicule
      // On préfère le kilometrageFin s'il existe, sinon kilometrageDebut
      // On trie par date de fin de location décroissante pour avoir le plus récent
      TypedQuery<Integer> query = em
          .createQuery("SELECT COALESCE(v.kilometrageFin, v.kilometrageDebut) FROM Verification v "
              + "JOIN v.location l " + "WHERE l.vehicule.id = :vehiculeId "
              + "AND (v.kilometrageDebut IS NOT NULL OR v.kilometrageFin IS NOT NULL) "
              + "ORDER BY l.dateFin DESC", Integer.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setMaxResults(1);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération du dernier kilométrage pour le véhicule " + vehiculeId,
          e);
    }
  }

  /**
   * Supprime une vérification de la base de données.
   *
   * @param id identifiant de la vérification à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      Verification verification = em.find(Verification.class, id);
      if (verification == null) {
        throw new IllegalArgumentException("Aucune vérification trouvée avec l'identifiant " + id);
      }

      em.remove(verification);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la vérification " + id, e);
    } finally {
      em.close();
    }
  }
}
