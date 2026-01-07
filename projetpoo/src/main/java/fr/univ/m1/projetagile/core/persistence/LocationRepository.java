package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class LocationRepository {

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

}
