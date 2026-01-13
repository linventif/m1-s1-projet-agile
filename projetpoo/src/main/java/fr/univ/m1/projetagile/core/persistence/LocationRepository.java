package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
 */
public class LocationRepository {

  public Location save(Location location) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

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
   * Récupère toutes les locations (réservations) pour un véhicule donné. Exclut les locations
   * annulées et terminées.
   */
  public List<Location> findAllReservationsByVehiculeId(Long vehiculeId) {
    EntityManager em = DatabaseConnection.getEntityManager();

    try {
      TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l "
          + "WHERE l.vehicule.id = :vehiculeId " + "AND l.statut != :statutAnnule "
          + "AND l.statut != :statutTermine " + "ORDER BY l.dateDebut ASC", Location.class);

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
   * Vérifie si un véhicule est disponible pour une période donnée.
   */
  public boolean isVehicleAvailable(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {

      Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
      if (vehicule == null || !vehicule.isDisponible()) {
        return false;
      }

      // ✅ bornes temps robustes
      LocalDateTime start = dateDebut.atStartOfDay();
      LocalDateTime end = dateFin.atTime(LocalTime.of(23, 59, 59));

      // Conflits avec locations existantes
      TypedQuery<Long> conflictQuery =
          em.createQuery("SELECT COUNT(l) FROM Location l " + "WHERE l.vehicule.id = :vehiculeId "
              + "AND l.statut != :statutTermine " + "AND l.statut != :statutAnnule "
              + "AND l.dateDebut <= :end " + "AND l.dateFin >= :start", Long.class);

      conflictQuery.setParameter("vehiculeId", vehiculeId);
      conflictQuery.setParameter("statutTermine", StatutLocation.TERMINE);
      conflictQuery.setParameter("statutAnnule", StatutLocation.ANNULE);
      conflictQuery.setParameter("start", start);
      conflictQuery.setParameter("end", end);

      Long conflictCount = conflictQuery.getSingleResult();
      if (conflictCount != null && conflictCount > 0) {
        return false;
      }

      // Disponibilités couvrant complètement la période demandée
      TypedQuery<Long> disponibilityQuery = em.createQuery(
          "SELECT COUNT(d) FROM Disponibilite d " + "WHERE d.vehicule.id = :vehiculeId "
              + "AND d.dateDebut <= :dateDebut " + "AND d.dateFin >= :dateFin",
          Long.class);

      disponibilityQuery.setParameter("vehiculeId", vehiculeId);
      disponibilityQuery.setParameter("dateDebut", dateDebut);
      disponibilityQuery.setParameter("dateFin", dateFin);

      Long disponibilityCount = disponibilityQuery.getSingleResult();
      return disponibilityCount != null && disponibilityCount > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification de disponibilité du véhicule " + vehiculeId, e);
    }
  }

  /**
   * ✅ #42 : Historique complet des locations pour un véhicule (du plus récent au plus ancien)
   */
  public List<Location> getHistoriqueLocations(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Location> query =
          em.createQuery(
              "SELECT l FROM Location l " + "JOIN FETCH l.vehicule " + "JOIN FETCH l.loueur "
                  + "WHERE l.vehicule.id = :vehiculeId " + "ORDER BY l.dateDebut DESC",
              Location.class);

      query.setParameter("vehiculeId", vehiculeId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de l'historique des locations pour le véhicule "
              + vehiculeId,
          e);
    }
  }

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
