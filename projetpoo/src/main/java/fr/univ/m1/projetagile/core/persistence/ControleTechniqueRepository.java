package fr.univ.m1.projetagile.core.persistence;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.ControleTechnique;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des contrôles techniques
 */
public class ControleTechniqueRepository {

  /**
   * Enregistre un contrôle technique dans la base de données (création ou mise à jour)
   *
   * @param controleTechnique le contrôle technique à enregistrer
   * @return le contrôle technique enregistré avec son ID généré
   */
  public ControleTechnique save(ControleTechnique controleTechnique) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Si le contrôle technique a déjà un ID, on fait un merge, sinon persist
      if (controleTechnique.getId() == null) {
        em.persist(controleTechnique);
      } else {
        controleTechnique = em.merge(controleTechnique);
      }

      transaction.commit();
      return controleTechnique;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du contrôle technique", e);
    }
  }

  /**
   * Récupère un contrôle technique par son ID
   *
   * @param id l'identifiant du contrôle technique
   * @return le contrôle technique trouvé ou null
   */
  public ControleTechnique findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(ControleTechnique.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du contrôle technique", e);
    }
  }

  /**
   * Récupère le contrôle technique d'un véhicule
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return le contrôle technique du véhicule ou null
   */
  public ControleTechnique findByVehiculeId(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<ControleTechnique> query = em.createQuery(
          "SELECT ct FROM ControleTechnique ct WHERE ct.vehicule.id = :vehiculeId",
          ControleTechnique.class);
      query.setParameter("vehiculeId", vehiculeId);
      return query.getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Supprime un contrôle technique de la base de données
   *
   * @param id l'identifiant du contrôle technique à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      ControleTechnique controleTechnique = em.find(ControleTechnique.class, id);
      if (controleTechnique == null) {
        throw new IllegalArgumentException(
            "Aucun contrôle technique trouvé avec l'identifiant " + id);
      }
      em.remove(controleTechnique);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du contrôle technique", e);
    }
  }
}
