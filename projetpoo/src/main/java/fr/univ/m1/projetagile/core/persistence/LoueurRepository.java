package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.enums.StatutLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des loueurs
 * Hérite des méthodes communes de UtilisateurRepository
 */
public class LoueurRepository extends UtilisateurRepository<Loueur> {

  public LoueurRepository() {
    super(Loueur.class);
  }

  /**
   * Récupère toutes les locations d'un loueur avec eager loading pour éviter LazyInitializationException
   * Exclut les locations annulées et trie par date de début décroissante
   *
   * @param loueurId l'identifiant du loueur
   * @return la liste des locations non annulées, triées par date décroissante
   */
  public List<Location> findLocationsByLoueurId(Long loueurId) {
    try (EntityManager em = DatabaseConnection.getEntityManager()) {
      TypedQuery<Location> query = em.createQuery(
          "SELECT l FROM Location l " +
          "LEFT JOIN FETCH l.vehicule v " +
          "LEFT JOIN FETCH v.proprietaire " +
          "WHERE l.loueur.idU = :loueurId " +
          "AND l.statut != :statutAnnule " +
          "ORDER BY l.dateDebut DESC",
          Location.class);
      
      query.setParameter("loueurId", loueurId);
      query.setParameter("statutAnnule", StatutLocation.ANNULE);
      
      return query.getResultList();

    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la récupération des locations du loueur " + loueurId, e);
    }
  }
}
