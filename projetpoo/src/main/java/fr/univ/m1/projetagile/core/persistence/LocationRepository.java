package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link Location}.
 * <p>
 * Fournit des opérations de type CRUD autour des locations :
 * <ul>
 *   <li>création et mise à jour d'une location via {@link #save(Location)} ;</li>
 *   <li>récupération des réservations d'un véhicule via
 *   {@link #findAllReservationsByVehiculeId(Long)}.</li>
 * </ul>
 * Les opérations de persistance s'appuient sur un {@link EntityManager} obtenu
 * via {@link fr.univ.m1.projetagile.core.DatabaseConnection}.
 */
public class LocationRepository {

  /**
   * Sauvegarde une entité {@link Location} en base de données. Si la location ne possède pas
   * encore d'identifiant, elle est créée (persist), sinon elle est mise à jour (merge) dans une
   * transaction.
   *
   * @param location la location à sauvegarder
   * @return la location persistée ou fusionnée, potentiellement détachée après la fermeture de
   *         l'EntityManager
   * @throws RuntimeException si une erreur survient lors de l'enregistrement ou de la gestion de
   *                          la transaction
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
