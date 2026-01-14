package fr.univ.m1.projetagile.entretienVehicule.persistence;

import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.entretienVehicule.entity.Entretien;
import fr.univ.m1.projetagile.entretienVehicule.entity.EntretienVehicule;
import fr.univ.m1.projetagile.enums.StatutEntretien;
import jakarta.persistence.EntityManager;

/**
 * Repository for managing EntretienVehicule (vehicle maintenance record) entities. Provides CRUD
 * operations and specific queries for vehicle maintenance tracking.
 */
public class EntretienVehiculeRepository {

  /**
   * Saves (creates or updates) an EntretienVehicule entity.
   *
   * @param entretienVehicule the entity to save
   * @return the saved entity
   * @throws IllegalArgumentException if entretienVehicule is null or invalid
   */
  public EntretienVehicule save(EntretienVehicule entretienVehicule) {
    if (entretienVehicule == null) {
      throw new IllegalArgumentException("EntretienVehicule ne peut pas être null");
    }

    if (entretienVehicule.getVehicule() == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }

    if (entretienVehicule.getEntretien() == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      em.getTransaction().begin();
      EntretienVehicule result;
      if (entretienVehicule.getId() == null) {
        em.persist(entretienVehicule);
        result = entretienVehicule;
      } else {
        result = em.merge(entretienVehicule);
      }
      em.getTransaction().commit();
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la sauvegarde de l'entretien véhicule", e);
    }
  }

  /**
   * Finds an EntretienVehicule by its ID.
   *
   * @param id the ID to search for
   * @return the EntretienVehicule entity, or null if not found
   */
  public EntretienVehicule findById(Long id) {
    if (id == null) {
      return null;
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(EntretienVehicule.class, id);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Finds all EntretienVehicule for a specific vehicle.
   *
   * @param vehicule the vehicle
   * @return list of maintenance records for this vehicle
   */
  public List<EntretienVehicule> findByVehicule(Vehicule vehicule) {
    if (vehicule == null || vehicule.getId() == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em
          .createQuery("SELECT ev FROM EntretienVehicule ev WHERE ev.vehicule.id = :vehiculeId",
              EntretienVehicule.class)
          .setParameter("vehiculeId", vehicule.getId()).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds all EntretienVehicule for a specific maintenance company.
   *
   * @param entretien the maintenance company
   * @return list of maintenance records for this company
   */
  public List<EntretienVehicule> findByEntretien(Entretien entretien) {
    if (entretien == null || entretien.getIdU() == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em
          .createQuery("SELECT ev FROM EntretienVehicule ev WHERE ev.entretien.idU = :entretienId",
              EntretienVehicule.class)
          .setParameter("entretienId", entretien.getIdU()).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds all automatic maintenance records.
   *
   * @return list of automatic maintenance records
   */
  public List<EntretienVehicule> findByAutomatique() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT ev FROM EntretienVehicule ev WHERE ev.automatique = true",
          EntretienVehicule.class).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds all manual maintenance records.
   *
   * @return list of manual maintenance records
   */
  public List<EntretienVehicule> findByManuel() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT ev FROM EntretienVehicule ev WHERE ev.automatique = false",
          EntretienVehicule.class).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds all maintenance records for a specific vehicle and company.
   *
   * @param vehicule the vehicle
   * @param entretien the maintenance company
   * @return list of maintenance records
   */
  public List<EntretienVehicule> findByVehiculeAndEntretien(Vehicule vehicule,
      Entretien entretien) {
    if (vehicule == null || vehicule.getId() == null || entretien == null
        || entretien.getIdU() == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em
          .createQuery(
              "SELECT ev FROM EntretienVehicule ev "
                  + "WHERE ev.vehicule.id = :vehiculeId AND ev.entretien.idU = :entretienId",
              EntretienVehicule.class)
          .setParameter("vehiculeId", vehicule.getId())
          .setParameter("entretienId", entretien.getIdU()).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds all maintenance records by status.
   *
   * @param statut the status to filter by (EN_ATTENTE, PLANIFIE, REALISE, ANNULE)
   * @return list of maintenance records with the given status
   */
  public List<EntretienVehicule> findByStatut(StatutEntretien statut) {
    if (statut == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT ev FROM EntretienVehicule ev WHERE ev.statut = :statut",
          EntretienVehicule.class).setParameter("statut", statut).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Retrieves all EntretienVehicule entities.
   *
   * @return list of all maintenance records
   */
  public List<EntretienVehicule> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT ev FROM EntretienVehicule ev", EntretienVehicule.class)
          .getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Deletes an EntretienVehicule by its ID.
   *
   * @param id the ID of the entity to delete
   * @throws IllegalArgumentException if id is null
   */
  public void delete(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      em.getTransaction().begin();
      EntretienVehicule entretienVehicule = em.find(EntretienVehicule.class, id);
      if (entretienVehicule != null) {
        em.remove(entretienVehicule);
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la suppression de l'entretien véhicule", e);
    }
  }

  /**
   * Deletes an EntretienVehicule entity.
   *
   * @param entretienVehicule the entity to delete
   */
  public void delete(EntretienVehicule entretienVehicule) {
    if (entretienVehicule != null && entretienVehicule.getId() != null) {
      delete(entretienVehicule.getId());
    }
  }
}
