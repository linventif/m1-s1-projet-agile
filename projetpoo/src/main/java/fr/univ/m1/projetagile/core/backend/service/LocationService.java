package fr.univ.m1.projetagile.core.backend.service;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;

/**
 * Service non-Spring pour la gestion des `Location` (contrats). Utilise l'EntityManager fourni par
 * `DatabaseConnection`.
 */
public class LocationService {

  /**
   * Persiste une `Location` construite à partir des ids fournis. Valide les dates et la
   * disponibilité du véhicule.
   */
  public Location enregistrerContrat(Long vehiculeId, Long loueurId, LocalDateTime dateDebut,
      LocalDateTime dateFin, String lieuDepot) {

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("Dates nulles");
    }
    if (dateDebut.isAfter(dateFin)) {
      throw new IllegalArgumentException("Dates invalides");
    }

    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      em.getTransaction().begin();

      Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
      Loueur loueur = em.find(Loueur.class, loueurId);

      if (vehicule == null || loueur == null) {
        throw new IllegalArgumentException("Vehicule ou Loueur introuvable");
      }

      // Vérification de disponibilité selon UML (Vehicule.csDisponible)
      boolean disponible = true;
      try {
        disponible = vehicule.csDisponible(dateDebut.toLocalDate(), dateFin.toLocalDate());
      } catch (Throwable t) {
        // en cas de problème, considérer non disponible pour sécurité
        disponible = false;
      }
      if (!disponible) {
        throw new IllegalStateException("Vehicule non disponible pour la période demandée");
      }

      Location location = new Location(dateDebut, dateFin, lieuDepot, vehicule, loueur);
      // Lier la location au loueur (bidirectionnel côté Loueur)
      loueur.ajouterLocation(location);
      em.persist(location);

      em.getTransaction().commit();
      return location;
    } catch (Exception e) {
      if (em.getTransaction().isActive())
        em.getTransaction().rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  /**
   * Persiste une `Location` déjà construite (vérifie dates et relations).
   */
  public Location enregistrerContrat(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location null");
    }
    return enregistrerContrat(
        location.getVehicule() != null ? location.getVehicule().getId() : null,
        location.getLoueur() != null ? location.getLoueur().getIdU() : null,
        location.getDateDebut(), location.getDateFin(), location.getLieuDepot());
  }
}
