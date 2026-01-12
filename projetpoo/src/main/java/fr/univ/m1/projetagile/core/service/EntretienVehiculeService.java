package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.entity.Entretien;
import fr.univ.m1.projetagile.core.entity.EntretienVehicule;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.EntretienVehiculeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class EntretienVehiculeService {

  private final EntityManager em;
  private final EntretienVehiculeRepository repository;

  public EntretienVehiculeService(EntityManager em) {
    this.em = em;
    this.repository = new EntretienVehiculeRepository(em);
  }

  /**
   * Enregistre un nouvel entretien pour un véhicule.
   *
   * @param vehiculeId id du véhicule
   * @param entretienId id du type d'entretien (Entretien)
   * @param dateEntretien date à laquelle l'entretien a été effectué
   * @param automatique true si déclenché automatiquement par la plateforme
   */
  public EntretienVehicule enregistrerEntretien(Long vehiculeId, Long entretienId,
      LocalDate dateEntretien, boolean automatique) {
    if (vehiculeId == null || entretienId == null || dateEntretien == null) {
      throw new IllegalArgumentException("Véhicule, entretien et date sont obligatoires");
    }

    Vehicule vehicule = repository.findVehiculeById(vehiculeId);
    Entretien entretien = repository.findEntretienById(entretienId);

    if (vehicule == null || entretien == null) {
      throw new IllegalArgumentException("Véhicule ou entretien introuvable");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      EntretienVehicule ev = new EntretienVehicule(automatique, vehicule, entretien, dateEntretien);

      repository.save(ev);

      tx.commit();
      return ev;

    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /**
   * Retourne le dernier entretien enregistré pour un véhicule, ou null si aucun.
   */
  public EntretienVehicule obtenirDernierEntretien(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("Id véhicule obligatoire");
    }
    return repository.findDernierEntretienByVehicule(vehiculeId);
  }

  /**
   * Retourne la date du dernier entretien, ou null si aucun.
   */
  public LocalDate obtenirDateDernierEntretien(Long vehiculeId) {
    EntretienVehicule dernier = obtenirDernierEntretien(vehiculeId);
    return (dernier != null) ? dernier.getDateEntretien() : null;
  }

  /**
   * Vérifie si l'entretien est "à jour" à une date donnée, selon une durée de validité (ex: 12
   * mois).
   *
   * @param vehiculeId id du véhicule
   * @param validiteMois nombre de mois pendant lesquels l'entretien est considéré valide
   * @param dateRef date de référence (ex: date de début de location)
   */
  public boolean isEntretienAJour(Long vehiculeId, int validiteMois, LocalDate dateRef) {
    if (vehiculeId == null || dateRef == null) {
      throw new IllegalArgumentException("Véhicule et date de référence obligatoires");
    }
    if (validiteMois <= 0) {
      throw new IllegalArgumentException("validiteMois doit être > 0");
    }

    EntretienVehicule dernier = obtenirDernierEntretien(vehiculeId);
    if (dernier == null) {
      return false; // jamais entretenu -> pas à jour
    }

    LocalDate dateLimite = dernier.getDateEntretien().plusMonths(validiteMois);
    // à jour si la date limite n'est pas avant la date de référence
    return !dateLimite.isBefore(dateRef);
  }
}
