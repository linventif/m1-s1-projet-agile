package fr.univ.m1.projetagile.assurance.persistence;

import java.util.List;
import fr.univ.m1.projetagile.assurance.entity.SouscriptionAssurance;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository responsable de la persistance des entités {@link SouscriptionAssurance}.
 * <p>
 * Fournit des opérations CRUD pour les souscriptions d'assurance liées aux locations.
 */
public class SouscriptionAssuranceRepository {

  /**
   * Sauvegarde une entité {@link SouscriptionAssurance} en base de données. Si la souscription ne
   * possède pas encore d'identifiant, elle est créée (persist), sinon elle est mise à jour (merge)
   * dans une transaction.
   *
   * @param souscription la souscription d'assurance à sauvegarder
   * @return la souscription persistée ou fusionnée
   * @throws RuntimeException si une erreur survient lors de l'enregistrement
   */
  public SouscriptionAssurance save(SouscriptionAssurance souscription) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Si la souscription a déjà un ID, on fait un merge, sinon persist
      if (souscription.getId() == null) {
        em.persist(souscription);
      } else {
        souscription = em.merge(souscription);
      }

      transaction.commit();
      return souscription;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la souscription d'assurance",
          e);
    } finally {
      em.close();
    }
  }

  /**
   * Recherche une souscription d'assurance par son identifiant.
   *
   * @param id identifiant de la souscription
   * @return la souscription trouvée ou null si elle n'existe pas
   */
  public SouscriptionAssurance findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<SouscriptionAssurance> query =
          em.createQuery("SELECT s FROM SouscriptionAssurance s " + "JOIN FETCH s.location "
              + "JOIN FETCH s.assurance " + "WHERE s.id = :id", SouscriptionAssurance.class);
      query.setParameter("id", id);
      return query.getResultStream().findFirst().orElse(null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de la souscription d'assurance " + id, e);
    }
  }

  /**
   * Récupère toutes les souscriptions d'assurance pour une location donnée.
   * Charge eagerly l'assurance, la grille tarifaire et ses collections (tarifVehi et tarifOptions)
   * ainsi que les options de la souscription pour éviter les LazyInitializationException.
   * 
   * Note: Utilise trois requêtes pour éviter MultipleBagFetchException de Hibernate.
   *
   * @param locationId l'identifiant de la location
   * @return liste des souscriptions d'assurance pour cette location
   */
  public List<SouscriptionAssurance> findByLocationId(Long locationId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      // First query: fetch souscription with its options collection, assurance and grille
      TypedQuery<SouscriptionAssurance> query1 =
          em.createQuery("SELECT DISTINCT s FROM SouscriptionAssurance s " 
              + "LEFT JOIN FETCH s.options "
              + "JOIN FETCH s.assurance a "
              + "JOIN FETCH a.grille g "
              + "WHERE s.location.id = :locationId", SouscriptionAssurance.class);
      query1.setParameter("locationId", locationId);
      List<SouscriptionAssurance> souscriptions = query1.getResultList();
      
      // Second query: fetch tarifVehi for the grilles
      if (!souscriptions.isEmpty()) {
        TypedQuery<SouscriptionAssurance> query2 =
            em.createQuery("SELECT DISTINCT s FROM SouscriptionAssurance s " 
                + "JOIN FETCH s.assurance a "
                + "JOIN FETCH a.grille g "
                + "LEFT JOIN FETCH g.tarifVehi "
                + "WHERE s.location.id = :locationId", SouscriptionAssurance.class);
        query2.setParameter("locationId", locationId);
        query2.getResultList(); // Execute to populate tarifVehi in the same session
        
        // Third query: fetch tarifOptions for the grilles
        TypedQuery<SouscriptionAssurance> query3 =
            em.createQuery("SELECT DISTINCT s FROM SouscriptionAssurance s " 
                + "JOIN FETCH s.assurance a "
                + "JOIN FETCH a.grille g "
                + "LEFT JOIN FETCH g.tarifOptions "
                + "WHERE s.location.id = :locationId", SouscriptionAssurance.class);
        query3.setParameter("locationId", locationId);
        query3.getResultList(); // Execute to populate tarifOptions in the same session
      }
      
      return souscriptions;
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des souscriptions pour la location " + locationId, e);
    }
  }

  /**
   * Récupère toutes les souscriptions pour une assurance donnée.
   *
   * @param assuranceId l'identifiant de l'assurance
   * @return liste des souscriptions pour cette assurance
   */
  public List<SouscriptionAssurance> findByAssuranceId(Long assuranceId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<SouscriptionAssurance> query = em.createQuery(
          "SELECT s FROM SouscriptionAssurance s " + "JOIN FETCH s.location "
              + "WHERE s.assurance.id = :assuranceId " + "ORDER BY s.location.dateDebut DESC",
          SouscriptionAssurance.class);
      query.setParameter("assuranceId", assuranceId);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des souscriptions pour l'assurance " + assuranceId, e);
    }
  }

  /**
   * Supprime une souscription d'assurance de la base de données.
   *
   * @param id identifiant de la souscription à supprimer
   */
  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      SouscriptionAssurance souscription = em.find(SouscriptionAssurance.class, id);
      if (souscription == null) {
        throw new IllegalArgumentException(
            "Aucune souscription d'assurance trouvée avec l'identifiant " + id);
      }

      em.remove(souscription);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException(
          "Erreur lors de la suppression de la souscription d'assurance " + id, e);
    } finally {
      em.close();
    }
  }

  /**
   * Récupère toutes les souscriptions d'assurance.
   *
   * @return liste de toutes les souscriptions
   */
  public List<SouscriptionAssurance> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<SouscriptionAssurance> query = em.createQuery(
          "SELECT s FROM SouscriptionAssurance s " + "JOIN FETCH s.location "
              + "JOIN FETCH s.assurance " + "ORDER BY s.location.dateDebut DESC",
          SouscriptionAssurance.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération de toutes les souscriptions d'assurance", e);
    }
  }
}
