package fr.univ.m1.projetagile.assurance.persistence;

import java.util.List;
import fr.univ.m1.projetagile.assurance.entity.TarifVehicule;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link TarifVehicule}.
 * <p>
 * Fournit des opérations CRUD pour les tarifs de véhicules.
 */
public class TarifVehiculeRepository {

  /**
   * Sauvegarde une entité {@link TarifVehicule} en base de données. Si le tarif ne possède pas
   * encore d'identifiant, il est créé (persist), sinon il est mis à jour (merge) dans une
   * transaction.
   *
   * @param tarif le tarif véhicule à sauvegarder
   * @return le tarif persisté ou fusionné
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public TarifVehicule save(TarifVehicule tarif) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si le tarif a déjà un ID, on fait un merge, sinon persist
      if (tarif.getId() == null) {
        em.persist(tarif);
      } else {
        tarif = em.merge(tarif);
      }

      transaction.commit();
      return tarif;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du tarif véhicule", e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche un tarif véhicule par son identifiant.
   *
   * @param id identifiant du tarif
   * @return le tarif trouvé ou null si il n'existe pas
   */
  public TarifVehicule findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifVehicule> query = em.createQuery(
          "SELECT t FROM TarifVehicule t " + "JOIN FETCH t.grilleTarif " + "WHERE t.id = :id",
          TarifVehicule.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du tarif véhicule " + id, e);
    }
  }

  /**
   * Récupère tous les tarifs véhicules d'une grille tarifaire.
   *
   * @param grilleId l'identifiant de la grille tarifaire
   * @return liste des tarifs véhicules de cette grille
   */
  public List<TarifVehicule> findByGrilleId(Long grilleId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifVehicule> query = em.createQuery(
          "SELECT t FROM TarifVehicule t " + "JOIN FETCH t.grilleTarif g "
              + "WHERE g.id = :grilleId " + "ORDER BY t.typeVehi, t.modeleVehi",
          TarifVehicule.class);
      query.setParameter("grilleId", grilleId);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des tarifs véhicules pour la grille " + grilleId, e);
    }
  }

  /**
   * Recherche un tarif véhicule par type et modèle dans une grille donnée.
   *
   * @param grilleId l'identifiant de la grille tarifaire
   * @param type le type de véhicule
   * @param modele le modèle de véhicule
   * @return le tarif trouvé ou null si il n'existe pas
   */
  public TarifVehicule findByGrilleAndTypeAndModele(Long grilleId, TypeV type, String modele) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifVehicule> query = em.createQuery("SELECT t FROM TarifVehicule t "
          + "JOIN FETCH t.grilleTarif g " + "WHERE g.id = :grilleId " + "AND t.typeVehi = :type "
          + "AND LOWER(t.modeleVehi) = LOWER(:modele)", TarifVehicule.class);
      query.setParameter("grilleId", grilleId);
      query.setParameter("type", type);
      query.setParameter("modele", modele);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du tarif véhicule pour type="
          + type + ", modele=" + modele, e);
    }
  }

  /**
   * Récupère tous les tarifs véhicules.
   *
   * @return liste de tous les tarifs véhicules
   */
  public List<TarifVehicule> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifVehicule> query = em.createQuery("SELECT t FROM TarifVehicule t "
          + "JOIN FETCH t.grilleTarif " + "ORDER BY t.typeVehi, t.modeleVehi", TarifVehicule.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de tous les tarifs véhicules", e);
    }
  }

  /**
   * Supprime un tarif véhicule de la base de données.
   *
   * @param id identifiant du tarif à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      TarifVehicule tarif = em.find(TarifVehicule.class, id);
      if (tarif == null) {
        throw new IllegalArgumentException("Aucun tarif véhicule trouvé avec l'identifiant " + id);
      }

      em.remove(tarif);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du tarif véhicule " + id, e);
    } finally {
      em.close();
    }
  }
}
