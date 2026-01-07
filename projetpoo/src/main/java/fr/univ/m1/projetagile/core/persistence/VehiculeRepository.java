package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des véhicules
 */
public class VehiculeRepository {

  /**
   * Enregistre un véhicule dans la base de données (création ou mise à jour)
   *
   * @param vehicule le véhicule à enregistrer
   * @return le véhicule enregistré avec son ID généré
   */
  public Vehicule save(Vehicule vehicule) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si le véhicule a déjà un ID, on fait un merge, sinon persist
      if (vehicule.getId() == null) {
        em.persist(vehicule);
      } else {
        vehicule = em.merge(vehicule);
      }

      transaction.commit();
      return vehicule;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du véhicule", e);
    } finally {
      em.close();
    }
  }

  /**
   * Récupère tous les véhicules de la base de données
   *
   * @return la liste de tous les véhicules
   */
  public List<Vehicule> findAll() {
    EntityManager em = DatabaseConnection.getEntityManager();

    try {
      TypedQuery<Vehicule> query =
          em.createQuery("SELECT v FROM Vehicule v LEFT JOIN FETCH v.datesDispo", Vehicule.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des véhicules", e);
    } finally {
      em.close();
    }
  }

  /**
   * Récupère un véhicule par son ID
   *
   * @param id l'identifiant du véhicule
   * @return le véhicule trouvé ou null
   */
  public Vehicule findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();

    try {
      return em.find(Vehicule.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du véhicule", e);
    } finally {
      em.close();
    }
  }

  /**
   * Supprime un véhicule de la base de données
   *
   * @param id l'identifiant du véhicule à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      Vehicule vehicule = em.find(Vehicule.class, id);
      if (vehicule == null) {
        throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + id);
      }
      em.remove(vehicule);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du véhicule", e);
    } finally {
      em.close();
    }
  }

  /**
   * Récupère les dates de début et de fin des locations actives pour un véhicule (exclut les
   * locations avec statut TERMINE ou ANNULE)
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return liste de tableaux contenant [dateDebut, dateFin] pour chaque location active
   */
  public List<Object[]> getDatesLocationsActives(Long vehiculeId) {
    EntityManager em = DatabaseConnection.getEntityManager();

    try {
      TypedQuery<Object[]> query = em.createQuery("SELECT l.dateDebut, l.dateFin FROM Location l "
          + "WHERE l.vehicule.id = :vehiculeId " + "AND l.statut != :statutTermine "
          + "AND l.statut != :statutAnnule " + "ORDER BY l.dateDebut ASC", Object[].class);

      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("statutTermine", StatutLocation.TERMINE);
      query.setParameter("statutAnnule", StatutLocation.ANNULE);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des dates de locations actives pour le véhicule avec l'ID "
              + vehiculeId, e);
    } finally {
      em.close();
    }
  }
}
