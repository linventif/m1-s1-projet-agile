package fr.univ.m1.projetagile.parking.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.parking.entity.Parking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des parkings
 */
public class ParkingRepository {

  /**
   * Enregistre un parking dans la base de données (création ou mise à jour)
   *
   * @param parking le parking à enregistrer
   * @return le parking enregistré avec son ID généré
   */
  public Parking save(Parking parking) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Si le parking a déjà un ID, on fait un merge, sinon persist
      if (parking.getId() == null) {
        em.persist(parking);
      } else {
        parking = em.merge(parking);
      }

      transaction.commit();
      return parking;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du parking", e);
    }
  }

  /**
   * Récupère un parking par son ID
   *
   * @param id l'identifiant du parking
   * @return le parking trouvé ou null
   */
  public Parking findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(Parking.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du parking", e);
    }
  }

  /**
   * Supprime un parking de la base de données
   *
   * @param id l'identifiant du parking à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      Parking parking = em.find(Parking.class, id);
      if (parking == null) {
        throw new IllegalArgumentException("Aucun parking trouvé avec l'identifiant " + id);
      }
      em.remove(parking);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du parking", e);
    }
  }

  /**
   * Récupère tous les parkings d'une ville donnée
   *
   * @param ville la ville de recherche
   * @return la liste des parkings trouvés dans cette ville
   */
  public List<Parking> findByVille(String ville) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Parking> query = em.createQuery(
          "SELECT p FROM Parking p WHERE LOWER(p.ville) = LOWER(:ville)", Parking.class);
      query.setParameter("ville", ville);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des parkings par ville", e);
    }
  }
}
