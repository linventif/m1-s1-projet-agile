package fr.univ.m1.projetagile.core.persistence;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Entretien;
import fr.univ.m1.projetagile.core.entity.PrixEntretien;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

/**
 * Repository for managing PrixEntretien (maintenance pricing) entities. Provides CRUD operations
 * and specific queries for maintenance pricing management.
 */
public class PrixEntretienRepository {

  /**
   * Saves (creates or updates) a PrixEntretien entity.
   *
   * @param prixEntretien the entity to save
   * @return the saved entity
   * @throws IllegalArgumentException if prixEntretien is null or invalid
   */
  public PrixEntretien save(PrixEntretien prixEntretien) {
    if (prixEntretien == null) {
      throw new IllegalArgumentException("PrixEntretien ne peut pas être null");
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      em.getTransaction().begin();
      PrixEntretien result;
      if (prixEntretien.getId() == null) {
        em.persist(prixEntretien);
        result = prixEntretien;
      } else {
        result = em.merge(prixEntretien);
      }
      em.getTransaction().commit();
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la sauvegarde du prix d'entretien", e);
    }
  }

  /**
   * Finds a PrixEntretien by its ID.
   *
   * @param id the ID to search for
   * @return the PrixEntretien entity, or null if not found
   */
  public PrixEntretien findById(Long id) {
    if (id == null) {
      return null;
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(PrixEntretien.class, id);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Finds all PrixEntretien for a specific Entretien (maintenance company).
   *
   * @param entretien the maintenance company
   * @return list of pricing entries for this company
   */
  public List<PrixEntretien> findByEntretien(Entretien entretien) {
    if (entretien == null || entretien.getIdU() == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT p FROM PrixEntretien p WHERE p.entretien.idU = :entretienId",
          PrixEntretien.class).setParameter("entretienId", entretien.getIdU()).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Finds a specific price for a vehicle type and model from a maintenance company.
   *
   * @param entretien the maintenance company
   * @param typeVehi the vehicle type
   * @param modeleVehi the vehicle model
   * @return the PrixEntretien if found, null otherwise
   */
  public PrixEntretien findByEntretienAndVehiculeTypeAndModel(Entretien entretien, TypeV typeVehi,
      String modeleVehi) {
    if (entretien == null || entretien.getIdU() == null || typeVehi == null || modeleVehi == null
        || modeleVehi.trim().isEmpty()) {
      return null;
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery(
          "SELECT p FROM PrixEntretien p WHERE p.entretien.idU = :entretienId "
              + "AND p.typeVehi = :type AND p.modeleVehi = :modele",
          PrixEntretien.class).setParameter("entretienId", entretien.getIdU())
          .setParameter("type", typeVehi).setParameter("modele", modeleVehi).getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Finds all prices for a specific vehicle type.
   *
   * @param typeVehi the vehicle type
   * @return list of pricing entries for this vehicle type
   */
  public List<PrixEntretien> findByTypeVehicule(TypeV typeVehi) {
    if (typeVehi == null) {
      return Collections.emptyList();
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT p FROM PrixEntretien p WHERE p.typeVehi = :type",
          PrixEntretien.class).setParameter("type", typeVehi).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Retrieves all PrixEntretien entities.
   *
   * @return list of all pricing entries
   */
  public List<PrixEntretien> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.createQuery("SELECT p FROM PrixEntretien p", PrixEntretien.class).getResultList();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Deletes a PrixEntretien by its ID.
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
      PrixEntretien prixEntretien = em.find(PrixEntretien.class, id);
      if (prixEntretien != null) {
        em.remove(prixEntretien);
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la suppression du prix d'entretien", e);
    }
  }

  /**
   * Deletes a PrixEntretien entity.
   *
   * @param prixEntretien the entity to delete
   */
  public void delete(PrixEntretien prixEntretien) {
    if (prixEntretien != null && prixEntretien.getId() != null) {
      delete(prixEntretien.getId());
    }
  }
}
