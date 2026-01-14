package fr.univ.m1.projetagile.entretienVehicule.persistence;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.persistence.UtilisateurRepository;
import fr.univ.m1.projetagile.entretienVehicule.entity.Entretien;
import jakarta.persistence.EntityManager;

/**
 * Repository for managing Entretien (maintenance company) entities. Extends UtilisateurRepository
 * to provide authentication and user management capabilities for maintenance companies.
 */
public class EntretienRepository extends UtilisateurRepository<Entretien> {

  public EntretienRepository() {
    super(Entretien.class);
  }

  /**
   * Finds an Entretien by company name.
   *
   * @param nomEntreprise the company name to search for
   * @return the Entretien entity with the given company name, or null if not found
   */
  public Entretien findByNomEntreprise(String nomEntreprise) {
    if (nomEntreprise == null || nomEntreprise.trim().isEmpty()) {
      return null;
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em
          .createQuery("SELECT e FROM Entretien e WHERE e.nomEntreprise = :nom", Entretien.class)
          .setParameter("nom", nomEntreprise).getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Checks if a company name already exists in the database.
   *
   * @param nomEntreprise the company name to check
   * @return true if the name exists, false otherwise
   */
  public boolean existsByNomEntreprise(String nomEntreprise) {
    if (nomEntreprise == null || nomEntreprise.trim().isEmpty()) {
      return false;
    }

    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      Long count = em
          .createQuery("SELECT COUNT(e) FROM Entretien e WHERE e.nomEntreprise = :nom", Long.class)
          .setParameter("nom", nomEntreprise).getSingleResult();
      return count > 0;
    } catch (Exception e) {
      return false;
    }
  }
}
