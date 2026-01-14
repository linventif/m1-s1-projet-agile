package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.TarifOptionAssurance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link TarifOptionAssurance}.
 * <p>
 * Fournit des opérations CRUD pour les tarifs d'options d'assurance.
 */
public class TarifOptionAssuranceRepository {

  /**
   * Sauvegarde une entité {@link TarifOptionAssurance} en base de données.
   * Si le tarif ne possède pas encore d'identifiant, il est créé (persist),
   * sinon il est mis à jour (merge) dans une transaction.
   *
   * @param tarif le tarif d'option à sauvegarder
   * @return le tarif persisté ou fusionné
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public TarifOptionAssurance save(TarifOptionAssurance tarif) {
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
      throw new RuntimeException("Erreur lors de l'enregistrement du tarif d'option d'assurance",
          e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche un tarif d'option par son identifiant.
   *
   * @param id identifiant du tarif
   * @return le tarif trouvé ou null si il n'existe pas
   */
  public TarifOptionAssurance findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifOptionAssurance> query = em.createQuery(
          "SELECT t FROM TarifOptionAssurance t " + "JOIN FETCH t.grilleTarif " + "WHERE t.id = :id",
          TarifOptionAssurance.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération du tarif d'option d'assurance " + id, e);
    }
  }

  /**
   * Récupère tous les tarifs d'options d'une grille tarifaire.
   *
   * @param grilleId l'identifiant de la grille tarifaire
   * @return liste des tarifs d'options de cette grille
   */
  public List<TarifOptionAssurance> findByGrilleId(Long grilleId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifOptionAssurance> query = em.createQuery(
          "SELECT t FROM TarifOptionAssurance t " + "JOIN FETCH t.grilleTarif g "
              + "WHERE g.id = :grilleId " + "ORDER BY t.nomOption",
          TarifOptionAssurance.class);
      query.setParameter("grilleId", grilleId);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des tarifs d'options pour la grille " + grilleId, e);
    }
  }

  /**
   * Recherche un tarif d'option par son nom dans une grille donnée.
   *
   * @param grilleId l'identifiant de la grille tarifaire
   * @param nomOption le nom de l'option
   * @return le tarif trouvé ou null si il n'existe pas
   */
  public TarifOptionAssurance findByGrilleAndNom(Long grilleId, String nomOption) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifOptionAssurance> query = em.createQuery(
          "SELECT t FROM TarifOptionAssurance t " + "JOIN FETCH t.grilleTarif g "
              + "WHERE g.id = :grilleId " + "AND LOWER(t.nomOption) = LOWER(:nomOption)",
          TarifOptionAssurance.class);
      query.setParameter("grilleId", grilleId);
      query.setParameter("nomOption", nomOption);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération du tarif d'option pour nom=" + nomOption, e);
    }
  }

  /**
   * Récupère tous les tarifs d'options.
   *
   * @return liste de tous les tarifs d'options
   */
  public List<TarifOptionAssurance> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TarifOptionAssurance> query =
          em.createQuery("SELECT t FROM TarifOptionAssurance t " + "JOIN FETCH t.grilleTarif "
              + "ORDER BY t.nomOption", TarifOptionAssurance.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de tous les tarifs d'options d'assurance", e);
    }
  }

  /**
   * Supprime un tarif d'option de la base de données.
   *
   * @param id identifiant du tarif à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      TarifOptionAssurance tarif = em.find(TarifOptionAssurance.class, id);
      if (tarif == null) {
        throw new IllegalArgumentException(
            "Aucun tarif d'option d'assurance trouvé avec l'identifiant " + id);
      }

      em.remove(tarif);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException(
          "Erreur lors de la suppression du tarif d'option d'assurance " + id, e);
    } finally {
      em.close();
    }
  }
}
