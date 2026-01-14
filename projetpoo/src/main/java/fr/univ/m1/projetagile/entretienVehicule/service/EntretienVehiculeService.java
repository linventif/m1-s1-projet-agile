package fr.univ.m1.projetagile.entretienVehicule.service;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.entretienVehicule.entity.Entretien;
import fr.univ.m1.projetagile.entretienVehicule.entity.EntretienVehicule;
import fr.univ.m1.projetagile.entretienVehicule.persistence.EntretienVehiculeRepository;
import fr.univ.m1.projetagile.enums.StatutEntretien;

/**
 * Service layer for managing EntretienVehicule (vehicle maintenance record) operations. Provides
 * business logic for scheduling, tracking, and managing vehicle maintenance.
 */
public class EntretienVehiculeService {

  private final EntretienVehiculeRepository repository;

  public EntretienVehiculeService() {
    this.repository = new EntretienVehiculeRepository();
  }

  /**
   * Creates a new vehicle maintenance record.
   *
   * @param vehicule the vehicle to maintain
   * @param entretien the Entretien entity representing the maintenance company
   * @param automatique whether the maintenance is automatic or manual
   * @return the created EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule createEntretienVehicule(Vehicule vehicule, Entretien entretien,
      boolean automatique) {
    // Validate inputs
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }

    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }

    // Create and save the maintenance record
    EntretienVehicule entretienVehicule = new EntretienVehicule(automatique, vehicule, entretien);
    return repository.save(entretienVehicule);
  }

  /**
   * Schedules a maintenance for a specific date. This method can be called at any time to schedule
   * or reschedule a maintenance.
   *
   * @param entretienVehiculeId the ID of the maintenance record
   * @param datePlanification the date to schedule the maintenance
   * @return the updated EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule planifierEntretien(Long entretienVehiculeId,
      LocalDate datePlanification) {
    if (entretienVehiculeId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(entretienVehiculeId);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun entretien véhicule trouvé avec l'id: " + entretienVehiculeId);
    }

    // Use the entity method to schedule
    entretienVehicule.planifierEntretien(datePlanification);
    return repository.save(entretienVehicule);
  }

  /**
   * Schedules a new maintenance directly for a vehicle and maintenance company.
   *
   * @param vehicule the vehicle to maintain
   * @param entretien the maintenance company
   * @param automatique whether the maintenance is automatic
   * @param datePlanification the date to schedule the maintenance
   * @return the created and scheduled EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule planifierNouvelEntretien(Vehicule vehicule, Entretien entretien,
      boolean automatique, LocalDate datePlanification) {
    // Create the maintenance record
    EntretienVehicule entretienVehicule = createEntretienVehicule(vehicule, entretien, automatique);

    // Schedule it
    if (datePlanification != null) {
      entretienVehicule.planifierEntretien(datePlanification);
      entretienVehicule = repository.save(entretienVehicule);
    }

    return entretienVehicule;
  }

  /**
   * Marks a maintenance as completed.
   *
   * @param entretienVehiculeId the ID of the maintenance record
   * @return the updated EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule marquerRealise(Long entretienVehiculeId) {
    if (entretienVehiculeId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(entretienVehiculeId);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun entretien véhicule trouvé avec l'id: " + entretienVehiculeId);
    }

    entretienVehicule.marquerRealise();
    return repository.save(entretienVehicule);
  }

  /**
   * Cancels a scheduled maintenance.
   *
   * @param entretienVehiculeId the ID of the maintenance record
   * @return the updated EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule annulerEntretien(Long entretienVehiculeId) {
    if (entretienVehiculeId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(entretienVehiculeId);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun entretien véhicule trouvé avec l'id: " + entretienVehiculeId);
    }

    entretienVehicule.annuler();
    return repository.save(entretienVehicule);
  }

  /**
   * Updates the automatic flag of a maintenance record.
   *
   * @param entretienVehiculeId the ID of the maintenance record
   * @param automatique the new automatic value
   * @return the updated EntretienVehicule entity
   * @throws IllegalArgumentException if validation fails
   */
  public EntretienVehicule updateAutomatique(Long entretienVehiculeId, boolean automatique) {
    if (entretienVehiculeId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(entretienVehiculeId);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun entretien véhicule trouvé avec l'id: " + entretienVehiculeId);
    }

    entretienVehicule.setAutomatique(automatique);
    return repository.save(entretienVehicule);
  }

  /**
   * Deletes a maintenance record.
   *
   * @param entretienVehiculeId the ID of the maintenance record to delete
   * @throws IllegalArgumentException if validation fails
   */
  public void deleteEntretienVehicule(Long entretienVehiculeId) {
    if (entretienVehiculeId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(entretienVehiculeId);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException(
          "Aucun entretien véhicule trouvé avec l'id: " + entretienVehiculeId);
    }

    repository.delete(entretienVehiculeId);
  }

  /**
   * Retrieves all maintenance records for a vehicle.
   *
   * @param vehicule the vehicle
   * @return list of maintenance records
   */
  public List<EntretienVehicule> getEntretiensByVehicule(Vehicule vehicule) {
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }
    return repository.findByVehicule(vehicule);
  }

  /**
   * Retrieves all maintenance records for a maintenance company.
   *
   * @param entretien the maintenance company
   * @return list of maintenance records
   */
  public List<EntretienVehicule> getEntretiensByEntretien(Entretien entretien) {
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }
    return repository.findByEntretien(entretien);
  }

  /**
   * Retrieves all automatic maintenance records.
   *
   * @return list of automatic maintenance records
   */
  public List<EntretienVehicule> getEntretiensAutomatiques() {
    return repository.findByAutomatique();
  }

  /**
   * Retrieves all manual maintenance records.
   *
   * @return list of manual maintenance records
   */
  public List<EntretienVehicule> getEntretiensManuels() {
    return repository.findByManuel();
  }

  /**
   * Retrieves all maintenance records by status.
   *
   * @param statut the status (EN_ATTENTE, PLANIFIE, REALISE, ANNULE)
   * @return list of maintenance records with the given status
   */
  public List<EntretienVehicule> getEntretiensByStatut(StatutEntretien statut) {
    if (statut == null) {
      throw new IllegalArgumentException("Le statut ne peut pas être null");
    }
    return repository.findByStatut(statut);
  }

  /**
   * Retrieves a maintenance record by its ID.
   *
   * @param id the ID of the maintenance record
   * @return the EntretienVehicule entity
   * @throws IllegalArgumentException if not found
   */
  public EntretienVehicule findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    EntretienVehicule entretienVehicule = repository.findById(id);
    if (entretienVehicule == null) {
      throw new IllegalArgumentException("Aucun entretien véhicule trouvé avec l'id: " + id);
    }

    return entretienVehicule;
  }

  /**
   * Retrieves all maintenance records.
   *
   * @return list of all maintenance records
   */
  public List<EntretienVehicule> getAllEntretiens() {
    return repository.findAll();
  }
}
