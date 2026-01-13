package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.parking.entity.Parking;
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
      TypedQuery<Vehicule> query = em.createQuery("SELECT DISTINCT v FROM Vehicule v "
          + "LEFT JOIN FETCH v.datesDispo " + "LEFT JOIN FETCH v.proprietaire", Vehicule.class);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des véhicules", e);
    }
  }

  public List<Vehicule> findWithFilters(LocalDate dateDebut, LocalDate dateFin, String ville,
      String marque, String modele, String couleur, Double prixMin, Double prixMax, TypeV type,
      Boolean hasParkingOption) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      StringBuilder jpql = new StringBuilder();
      jpql.append("SELECT DISTINCT v FROM Vehicule v " + "LEFT JOIN FETCH v.datesDispo "
          + "LEFT JOIN FETCH v.proprietaire");

      List<String> conditions = new ArrayList<>();

      // Filtre par ville
      if (ville != null && !ville.trim().isEmpty()) {
        conditions.add("LOWER(v.ville) LIKE LOWER(:ville)");
      }

      // Filtre par marque
      if (marque != null && !marque.trim().isEmpty()) {
        conditions.add("LOWER(v.marque) LIKE LOWER(:marque)");
      }

      // Filtre par modèle
      if (modele != null && !modele.trim().isEmpty()) {
        conditions.add("LOWER(v.modele) LIKE LOWER(:modele)");
      }

      // Filtre par couleur
      if (couleur != null && !couleur.trim().isEmpty()) {
        conditions.add("LOWER(v.couleur) LIKE LOWER(:couleur)");
      }

      // Filtre par type
      if (type != null) {
        conditions.add("v.type = :type");
      }

      // Filtre par prix minimum
      if (prixMin != null) {
        conditions.add("v.prixJ >= :prixMin");
      }

      // Filtre par prix maximum
      if (prixMax != null) {
        conditions.add("v.prixJ <= :prixMax");
      }

      // Filtre par disponibilité générale du véhicule
      conditions.add("v.disponible = true");

      // Filtre par option Parking (si demandé)
      if (hasParkingOption != null && hasParkingOption) {
        conditions.add(
            "EXISTS (SELECT so FROM SouscriptionOption so WHERE so.agent = v.proprietaire AND so.option.id = :parkingOptionId)");
      }

      // Ajouter les conditions WHERE si elles existent
      if (!conditions.isEmpty()) {
        jpql.append(" WHERE ");
        jpql.append(String.join(" AND ", conditions));
      }

      // Si on a des dates de début et fin, ajouter la vérification des conflits de location
      if (dateDebut != null && dateFin != null) {
        if (conditions.isEmpty()) {
          jpql.append(" WHERE ");
        } else {
          jpql.append(" AND ");
        }
        jpql.append("NOT EXISTS (");
        jpql.append("SELECT l FROM Location l ");
        jpql.append("WHERE l.vehicule = v ");
        jpql.append("AND l.statut != :statutTermine ");
        jpql.append("AND l.statut != :statutAnnule ");
        jpql.append("AND l.dateDebut <= :dateFin ");
        jpql.append("AND l.dateFin >= :dateDebut)");
      }

      TypedQuery<Vehicule> query = em.createQuery(jpql.toString(), Vehicule.class);

      // Définir les paramètres
      if (ville != null && !ville.trim().isEmpty()) {
        query.setParameter("ville", "%" + ville.trim() + "%");
      }
      if (marque != null && !marque.trim().isEmpty()) {
        query.setParameter("marque", "%" + marque.trim() + "%");
      }
      if (modele != null && !modele.trim().isEmpty()) {
        query.setParameter("modele", "%" + modele.trim() + "%");
      }
      if (couleur != null && !couleur.trim().isEmpty()) {
        query.setParameter("couleur", "%" + couleur.trim() + "%");
      }
      if (type != null) {
        query.setParameter("type", type);
      }
      if (prixMin != null) {
        query.setParameter("prixMin", prixMin);
      }
      if (prixMax != null) {
        query.setParameter("prixMax", prixMax);
      }
      if (dateDebut != null && dateFin != null) {
        query.setParameter("statutTermine", StatutLocation.TERMINE);
        query.setParameter("statutAnnule", StatutLocation.ANNULE);
        query.setParameter("dateDebut", dateDebut.atStartOfDay());
        query.setParameter("dateFin", dateFin.atStartOfDay());
      }
      if (hasParkingOption != null && hasParkingOption) {
        query.setParameter("parkingOptionId", Parking.PARKING_OPTION_ID);
      }

      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de la récupération des véhicules avec filtres", e);
    }
  }

  /**
   * Récupère un véhicule par son ID avec son propriétaire
   *
   * @param id l'identifiant du véhicule
   * @return le véhicule trouvé ou null
   */
  public Vehicule findById(Long id) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Vehicule> query = em.createQuery(
          "SELECT v FROM Vehicule v LEFT JOIN FETCH v.proprietaire WHERE v.id = :id",
          Vehicule.class);
      query.setParameter("id", id);
      List<Vehicule> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);
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
      if (transaction != null && transaction.isActive()) {
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

  /**
   * Récupère tous les véhicules appartenant à un agent spécifique
   *
   * @param agentId l'identifiant de l'agent propriétaire
   * @return la liste des véhicules de cet agent
   */
  public List<Vehicule> findByAgentId(Long agentId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Vehicule> query = em.createQuery(
          "SELECT v FROM Vehicule v LEFT JOIN FETCH v.datesDispo WHERE v.proprietaire.idU = :agentId",
          Vehicule.class);
      query.setParameter("agentId", agentId);
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des véhicules de l'agent " + agentId, e);
    }

  }
}
