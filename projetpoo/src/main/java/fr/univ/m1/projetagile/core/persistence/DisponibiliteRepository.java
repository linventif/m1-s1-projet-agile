package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des disponibilités
 */
public class DisponibiliteRepository {

  /**
   * Enregistre une disponibilité dans la base de données (création ou mise à jour)
   *
   * @param disponibilite la disponibilité à enregistrer
   * @return la disponibilité enregistrée avec son ID généré
   */
  public Disponibilite save(Disponibilite disponibilite) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      // Vérifier que le véhicule existe
      Vehicule vehicule = disponibilite.getVehicule();
      if (vehicule == null) {
        throw new IllegalArgumentException("La disponibilité doit être liée à un véhicule.");
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
      disponibilite.setVehicule(vehiculeManaged);

      // Si la disponibilité a déjà un ID, on fait un merge, sinon persist
      if (disponibilite.getId() == null) {
        em.persist(disponibilite);
      } else {
        disponibilite = em.merge(disponibilite);
      }

      transaction.commit();
      return disponibilite;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la disponibilité", e);
    }
  }

  /**
   * Récupère toutes les disponibilités de la base de données
   *
   * @return la liste de toutes les disponibilités
   */
  public List<Disponibilite> findAll() {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Disponibilite> query =
          em.createQuery("SELECT d FROM Disponibilite d", Disponibilite.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des disponibilités", e);
    }
  }

  /**
   * Récupère une disponibilité par son ID
   *
   * @param id l'identifiant de la disponibilité
   * @return la disponibilité trouvée ou null
   */
  public Disponibilite findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      return em.find(Disponibilite.class, id);
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération de la disponibilité", e);
    }
  }

  /**
   * Récupère toutes les disponibilités d'un véhicule spécifique
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des disponibilités de ce véhicule
   */
  public List<Disponibilite> findByVehiculeId(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Disponibilite> query = em.createQuery(
          "SELECT d FROM Disponibilite d WHERE d.vehicule.id = :vehiculeId ORDER BY d.dateDebut ASC",
          Disponibilite.class);
      query.setParameter("vehiculeId", vehiculeId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des disponibilités du véhicule " + vehiculeId, e);
    }
  }

  /**
   * Récupère toutes les disponibilités futures d'un véhicule (à partir d'aujourd'hui)
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des disponibilités futures de ce véhicule
   */
  public List<Disponibilite> findFutureByVehiculeId(Long vehiculeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      LocalDate today = LocalDate.now();
      TypedQuery<Disponibilite> query = em.createQuery(
          "SELECT d FROM Disponibilite d WHERE d.vehicule.id = :vehiculeId "
              + "AND d.dateFin >= :today ORDER BY d.dateDebut ASC",
          Disponibilite.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("today", today);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des disponibilités futures du véhicule " + vehiculeId, e);
    }
  }

  /**
   * Supprime une disponibilité de la base de données
   *
   * @param id l'identifiant de la disponibilité à supprimer
   */
  public void delete(Long id) {
    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      Disponibilite disponibilite = em.find(Disponibilite.class, id);
      if (disponibilite == null) {
        throw new IllegalArgumentException("Aucune disponibilité trouvée avec l'identifiant " + id);
      }
      em.remove(disponibilite);

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la disponibilité", e);
    }
  }

  /**
   * Vérifie s'il existe un chevauchement de disponibilités pour un véhicule
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param dateDebut date de début de la période à vérifier
   * @param dateFin date de fin de la période à vérifier
   * @param excludeId ID de la disponibilité à exclure de la vérification (optionnel, pour mise à
   *        jour)
   * @return true s'il y a un chevauchement, false sinon
   */
  public boolean hasOverlap(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin,
      Long excludeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      StringBuilder jpql = new StringBuilder();
      jpql.append("SELECT COUNT(d) FROM Disponibilite d ");
      jpql.append("WHERE d.vehicule.id = :vehiculeId ");
      jpql.append("AND d.dateDebut <= :dateFin ");
      jpql.append("AND d.dateFin >= :dateDebut");

      if (excludeId != null) {
        jpql.append(" AND d.id != :excludeId");
      }

      TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("dateDebut", dateDebut);
      query.setParameter("dateFin", dateFin);

      if (excludeId != null) {
        query.setParameter("excludeId", excludeId);
      }

      Long count = query.getSingleResult();
      return count > 0;

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la vérification des chevauchements de disponibilités", e);
    }
  }

  /**
   * Trouve toutes les disponibilités qui se touchent ou se chevauchent avec une période donnée
   * Cela inclut les disponibilités adjacentes (qui se touchent exactement)
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param dateDebut date de début de la période à vérifier
   * @param dateFin date de fin de la période à vérifier
   * @param excludeId ID de la disponibilité à exclure (optionnel)
   * @return liste des disponibilités qui se touchent ou se chevauchent
   */
  public List<Disponibilite> findOverlappingOrAdjacent(Long vehiculeId, LocalDate dateDebut,
      LocalDate dateFin, Long excludeId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      StringBuilder jpql = new StringBuilder();
      jpql.append("SELECT d FROM Disponibilite d ");
      jpql.append("WHERE d.vehicule.id = :vehiculeId ");
      // Pour détecter les périodes adjacentes, on utilise <= et >= au lieu de < et >
      // Cela permet de trouver les périodes qui se touchent exactement
      // Par exemple: période existante se termine le 20, nouvelle commence le 20
      jpql.append("AND d.dateDebut <= :dateFin ");
      jpql.append("AND d.dateFin >= :dateDebut");

      if (excludeId != null) {
        jpql.append(" AND d.id != :excludeId");
      }

      jpql.append(" ORDER BY d.dateDebut ASC");

      TypedQuery<Disponibilite> query = em.createQuery(jpql.toString(), Disponibilite.class);
      query.setParameter("vehiculeId", vehiculeId);
      query.setParameter("dateDebut", dateDebut);
      query.setParameter("dateFin", dateFin);

      if (excludeId != null) {
        query.setParameter("excludeId", excludeId);
      }

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la recherche des disponibilités qui se chevauchent", e);
    }
  }

  /**
   * Supprime plusieurs disponibilités en une seule transaction
   *
   * @param ids liste des identifiants des disponibilités à supprimer
   */
  public void deleteMultiple(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }

    EntityTransaction transaction = null;
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      transaction = em.getTransaction();
      transaction.begin();

      for (Long id : ids) {
        Disponibilite disponibilite = em.find(Disponibilite.class, id);
        if (disponibilite != null) {
          em.remove(disponibilite);
        }
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression multiple de disponibilités", e);
    }
  }
}
