package fr.univ.m1.projetagile.entretienTechnique.persistence;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique;
import fr.univ.m1.projetagile.entretienTechnique.entity.TypeTechnique;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des entretiens techniques
 */
public class EntretienTechniqueRepository {

  /**
   * Enregistre un entretien technique dans la base de données (création ou mise à jour)
   *
   * @param entretienTechnique l'entretien technique à enregistrer
   * @return l'entretien technique enregistré avec son ID généré
   */
  public EntretienTechnique save(EntretienTechnique entretienTechnique) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Vérifier que le véhicule existe
      Vehicule vehicule = entretienTechnique.getVehicule();
      if (vehicule == null) {
        throw new IllegalArgumentException("L'entretien technique doit être lié à un véhicule.");
      }

      Long vehiculeId = vehicule.getId();
      if (vehiculeId == null) {
        throw new IllegalArgumentException("Le véhicule doit déjà être enregistré.");
      }

      Vehicule vehiculeManaged = em.find(Vehicule.class, vehiculeId);
      if (vehiculeManaged == null) {
        throw new IllegalArgumentException(
            "Le véhicule fourni n'existe pas en base (id=" + vehiculeId + ").");
      }
      entretienTechnique.setVehicule(vehiculeManaged);

      // Vérifier que le type technique existe
      TypeTechnique typeTechnique = entretienTechnique.getTypeTechnique();
      if (typeTechnique == null) {
        throw new IllegalArgumentException(
            "L'entretien technique doit être lié à un type technique.");
      }

      Long typeTechniqueId = typeTechnique.getId();
      if (typeTechniqueId == null) {
        throw new IllegalArgumentException("Le type technique doit déjà être enregistré.");
      }

      TypeTechnique typeTechniqueManaged = em.find(TypeTechnique.class, typeTechniqueId);
      if (typeTechniqueManaged == null) {
        throw new IllegalArgumentException(
            "Le type technique fourni n'existe pas en base (id=" + typeTechniqueId + ").");
      }
      entretienTechnique.setTypeTechnique(typeTechniqueManaged);

      // Si l'entretien technique a déjà un ID, on fait un merge, sinon persist
      if (entretienTechnique.getId() == null) {
        em.persist(entretienTechnique);
      } else {
        entretienTechnique = em.merge(entretienTechnique);
      }

      transaction.commit();
      return entretienTechnique;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de l'entretien technique", e);
    }
  }

  /**
   * Récupère tous les entretiens techniques de la base de données
   *
   * @return la liste de tous les entretiens techniques
   */
  public List<EntretienTechnique> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<EntretienTechnique> query = em.createQuery(
          "SELECT e FROM EntretienTechnique e LEFT JOIN FETCH e.vehicule LEFT JOIN FETCH e.typeTechnique ORDER BY e.date DESC",
          EntretienTechnique.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des entretiens techniques", e);
    }
  }

  /**
   * Récupère un entretien technique par son ID
   *
   * @param id l'identifiant de l'entretien technique
   * @return l'entretien technique trouvé ou null
   */
  public EntretienTechnique findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(EntretienTechnique.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de l'entretien technique", e);
    }
  }

  /**
   * Récupère tous les entretiens techniques d'un véhicule spécifique
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des entretiens techniques de ce véhicule
   */
  public List<EntretienTechnique> findByVehiculeId(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<EntretienTechnique> query =
          em.createQuery(
              "SELECT e FROM EntretienTechnique e LEFT JOIN FETCH e.typeTechnique "
                  + "WHERE e.vehicule.id = :vehiculeId ORDER BY e.date DESC",
              EntretienTechnique.class);
      query.setParameter("vehiculeId", vehiculeId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des entretiens techniques du véhicule " + vehiculeId, e);
    }
  }

  /**
   * Récupère tous les entretiens techniques d'un type technique spécifique
   *
   * @param typeTechniqueId l'identifiant du type technique
   * @return la liste des entretiens techniques de ce type
   */
  public List<EntretienTechnique> findByTypeTechniqueId(Long typeTechniqueId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<EntretienTechnique> query = em.createQuery(
          "SELECT e FROM EntretienTechnique e LEFT JOIN FETCH e.vehicule "
              + "WHERE e.typeTechnique.id = :typeTechniqueId ORDER BY e.date DESC",
          EntretienTechnique.class);
      query.setParameter("typeTechniqueId", typeTechniqueId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des entretiens techniques du type " + typeTechniqueId, e);
    }
  }

  /**
   * Récupère tous les entretiens techniques d'un véhicule pour un type technique spécifique
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param typeTechniqueId l'identifiant du type technique
   * @return la liste des entretiens techniques correspondants
   */
  public List<EntretienTechnique> findByVehiculeIdAndTypeTechniqueId(Long vehiculeId,
      Long typeTechniqueId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<EntretienTechnique> query =
          em.createQuery("SELECT e FROM EntretienTechnique e LEFT JOIN FETCH e.typeTechnique "
              + "WHERE e.vehicule.id = :vehiculeId AND e.typeTechnique.id = :typeTechniqueId "
              + "ORDER BY e.date DESC", EntretienTechnique.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("typeTechniqueId", typeTechniqueId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des entretiens techniques du véhicule " + vehiculeId
              + " et du type " + typeTechniqueId,
          e);
    }
  }

  /**
   * Récupère tous les entretiens techniques à partir d'une date donnée
   *
   * @param date la date à partir de laquelle récupérer les entretiens
   * @return la liste des entretiens techniques à partir de cette date
   */
  public List<EntretienTechnique> findByDateAfter(LocalDate date) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<EntretienTechnique> query = em.createQuery(
          "SELECT e FROM EntretienTechnique e LEFT JOIN FETCH e.vehicule LEFT JOIN FETCH e.typeTechnique "
              + "WHERE e.date >= :date ORDER BY e.date ASC",
          EntretienTechnique.class);
      query.setParameter("date", date);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des entretiens techniques à partir de la date " + date,
          e);
    }
  }

  /**
   * Supprime un entretien technique de la base de données
   *
   * @param id l'identifiant de l'entretien technique à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      EntretienTechnique entretienTechnique = em.find(EntretienTechnique.class, id);
      if (entretienTechnique == null) {
        throw new IllegalArgumentException(
            "Aucun entretien technique trouvé avec l'identifiant " + id);
      }
      em.remove(entretienTechnique);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de l'entretien technique", e);
    }
  }
}
