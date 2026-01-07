package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
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
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      Agent proprietaire = vehicule.getProprietaire();
      if (proprietaire == null) {
        throw new IllegalArgumentException("Le véhicule doit avoir un propriétaire existant.");
      }
      Long proprietaireId = proprietaire.getIdU();
      if (proprietaireId == null) {
        throw new IllegalArgumentException(
            "Le propriétaire doit déjà être enregistré (identifiant manquant).");
      }
      Agent proprietaireManaged = em.find(Agent.class, proprietaireId);
      if (proprietaireManaged == null) {
        throw new IllegalArgumentException(
            "Le propriétaire fourni n'existe pas en base (id=" + proprietaireId + ").");
      }
      vehicule.setProprietaire(proprietaireManaged);

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
    }
  }

  /**
   * Récupère tous les véhicules de la base de données
   *
   * @return la liste de tous les véhicules
   */
  public List<Vehicule> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Vehicule> query =
          em.createQuery("SELECT v FROM Vehicule v LEFT JOIN FETCH v.datesDispo", Vehicule.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des véhicules", e);
    }
  }

  /**
   * Récupère un véhicule par son ID
   *
   * @param id l'identifiant du véhicule
   * @return le véhicule trouvé ou null
   */
  public Vehicule findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(Vehicule.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du véhicule", e);
    }
  }

  /**
   * Supprime un véhicule de la base de données
   *
   * @param id l'identifiant du véhicule à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      Vehicule vehicule = em.find(Vehicule.class, id);
      if (vehicule == null) {
        throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + id);
      }
      em.remove(vehicule);

      transaction.commit();

    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du véhicule", e);
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
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Object[]> query = em.createQuery("SELECT l.dateDebut, l.dateFin FROM Location l "
          + "WHERE l.vehicule.id = :vehiculeId " + "AND l.statut != :statutTermine "
          + "AND l.statut != :statutAnnule " + "ORDER BY l.dateDebut ASC", Object[].class);

      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("statutTermine", StatutLocation.TERMINE);
      query.setParameter("statutAnnule", StatutLocation.ANNULE);

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des dates de location pour le véhicule " + vehiculeId, e);
    }
  }
}
