package fr.univ.m1.projetagile.entretienTechnique.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.entretienTechnique.entity.TypeTechnique;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des types techniques
 */
public class TypeTechniqueRepository {

  /**
   * Enregistre un type technique dans la base de données (création ou mise à jour)
   *
   * @param typeTechnique le type technique à enregistrer
   * @return le type technique enregistré avec son ID généré
   */
  public TypeTechnique save(TypeTechnique typeTechnique) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Si le type technique a déjà un ID, on fait un merge, sinon persist
      if (typeTechnique.getId() == null) {
        em.persist(typeTechnique);
      } else {
        typeTechnique = em.merge(typeTechnique);
      }

      transaction.commit();
      return typeTechnique;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement du type technique", e);
    }
  }

  /**
   * Récupère tous les types techniques de la base de données
   *
   * @return la liste de tous les types techniques
   */
  public List<TypeTechnique> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TypeTechnique> query =
          em.createQuery("SELECT t FROM TypeTechnique t ORDER BY t.nom ASC", TypeTechnique.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des types techniques", e);
    }
  }

  /**
   * Récupère un type technique par son ID
   *
   * @param id l'identifiant du type technique
   * @return le type technique trouvé ou null
   */
  public TypeTechnique findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(TypeTechnique.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du type technique", e);
    }
  }

  /**
   * Récupère un type technique par son nom
   *
   * @param nom le nom du type technique
   * @return le type technique trouvé ou null
   */
  public TypeTechnique findByNom(String nom) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<TypeTechnique> query =
          em.createQuery("SELECT t FROM TypeTechnique t WHERE t.nom = :nom", TypeTechnique.class);
      query.setParameter("nom", nom);
      List<TypeTechnique> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération du type technique par nom", e);
    }
  }

  /**
   * Supprime un type technique de la base de données
   *
   * @param id l'identifiant du type technique à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      TypeTechnique typeTechnique = em.find(TypeTechnique.class, id);
      if (typeTechnique == null) {
        throw new IllegalArgumentException("Aucun type technique trouvé avec l'identifiant " + id);
      }
      em.remove(typeTechnique);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression du type technique", e);
    }
  }
}
