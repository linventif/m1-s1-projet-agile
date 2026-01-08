package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link Location}.
 * <p>
 * Fournit des opérations de type CRUD autour des locations :
 * <ul>
 * <li>création et mise à jour d'une location via {@link #save(Location)} ;</li>
 * <li>récupération des réservations d'un véhicule via
 * {@link #findAllReservationsByVehiculeId(Long)}.</li>
 * </ul>
 * Les opérations de persistance s'appuient sur un {@link EntityManager} obtenu via
 * {@link fr.univ.m1.projetagile.core.DatabaseConnection}.
 */
public class LocationRepository {

  /**
   * Sauvegarde une entité {@link Location} en base de données. Si la location ne possède pas encore
   * d'identifiant, elle est créée (persist), sinon elle est mise à jour (merge) dans une
   * transaction.
   *
   * @param location la location à sauvegarder
   * @return la location persistée ou fusionnée, potentiellement détachée après la fermeture de
   *         l'EntityManager
   * @throws RuntimeException si une erreur survient lors de l'enregistrement ou de la gestion de la
   *         transaction
   */
  public Location save(Location location) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si la location a déjà un ID, on fait un merge, sinon persist
      if (location.getId() == null) {
        em.persist(location);
      } else {
        location = em.merge(location);
      }

      transaction.commit();
      return location;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la location", e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche une location par son identifiant.
   *
   * @param id identifiant de la location
   * @return la location trouvée ou null si elle n'existe pas
   */
  public Location findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l "
          + "JOIN FETCH l.vehicule " + "JOIN FETCH l.loueur " + "WHERE l.id = :id", Location.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de la location " + id, e);
    }
  }

  /**
   * Récupère toutes les locations (dates réservées) pour un véhicule donné Exclut les locations
   * annulées et terminées
   *
   * @param vehiculeId L'identifiant du véhicule
   * @return Liste des locations avec leurs dates de début et de fin
   */
  public List<Location> findAllReservationsByVehiculeId(Long vehiculeId) {
    EntityManager em = DatabaseConnection.getEntityManager();

    try {
      TypedQuery<Location> query =
          em.createQuery("SELECT l FROM Location l WHERE l.vehicule.id = :vehiculeId "
              + "AND l.statut != :statutAnnule " + "AND l.statut != :statutTermine "
              + "ORDER BY l.dateDebut ASC", Location.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("statutAnnule", StatutLocation.ANNULE);
      query.setParameter("statutTermine", StatutLocation.TERMINE);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des réservations du véhicule", e);
    } finally {
      em.close();
    }
  }

  /**
   * Vérifie si un véhicule est disponible pour une période donnée
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param dateDebut la date de début de la période demandée
   * @param dateFin la date de fin de la période demandée
   * @return true si le véhicule est disponible pour la période, false sinon
   */
  public boolean isVehicleAvailable(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      // D'abord vérifier que le véhicule existe et est disponible
      Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
      if (vehicule == null || !vehicule.isDisponible()) {
        return false;
      }

      // Vérifier qu'il n'y a pas de conflit avec les locations existantes
      TypedQuery<Long> conflictQuery =
          em.createQuery("SELECT COUNT(l) FROM Location l " + "WHERE l.vehicule.id = :vehiculeId "
              + "AND l.statut != :statutTermine " + "AND l.statut != :statutAnnule "
              + "AND l.dateDebut <= :dateFin " + "AND l.dateFin >= :dateDebut", Long.class);

      conflictQuery.setParameter("vehiculeId", vehiculeId);
      conflictQuery.setParameter("statutTermine", StatutLocation.TERMINE);
      conflictQuery.setParameter("statutAnnule", StatutLocation.ANNULE);
      conflictQuery.setParameter("dateDebut", dateDebut.atStartOfDay());
      conflictQuery.setParameter("dateFin", dateFin.atStartOfDay());

      Long conflictCount = conflictQuery.getSingleResult();
      if (conflictCount > 0) {
        return false;
      }

      // Vérifier qu'il existe au moins une disponibilité qui couvre complètement la période
      // demandée
      TypedQuery<Long> disponibilityQuery = em.createQuery(
          "SELECT COUNT(d) FROM Disponibilite d " + "WHERE d.vehicule.id = :vehiculeId "
              + "AND d.dateDebut <= :dateDebut " + "AND d.dateFin >= :dateFin",
          Long.class);

      disponibilityQuery.setParameter("vehiculeId", vehiculeId);
      disponibilityQuery.setParameter("dateDebut", dateDebut);
      disponibilityQuery.setParameter("dateFin", dateFin);

      Long disponibilityCount = disponibilityQuery.getSingleResult();
      return disponibilityCount > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification de disponibilité du véhicule " + vehiculeId, e);
    }
  }

  /**
   * Récupère l'historique complet des locations pour un véhicule, trié du plus récent au plus
   * ancien
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return liste des locations triées par date de début décroissante
   */
  public List<Location> getHistoriqueLocations(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l "
          + "JOIN FETCH l.vehicule "
          + "JOIN FETCH l.loueur "
          + "WHERE l.vehicule.id = :vehiculeId "
          + "ORDER BY l.dateDebut DESC", Location.class);

      query.setParameter("vehiculeId", vehiculeId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de l'historique des locations pour le véhicule "
              + vehiculeId,
          e);
    }
  }

  /**
   * Supprime une location de la base de données.
   *
   * @param id identifiant de la location à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      Location location = em.find(Location.class, id);
      if (location == null) {
        throw new IllegalArgumentException("Aucune location trouvée avec l'identifiant " + id);
      }

      em.remove(location);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la location " + id, e);
    } finally {
      em.close();
    }
  }

}
