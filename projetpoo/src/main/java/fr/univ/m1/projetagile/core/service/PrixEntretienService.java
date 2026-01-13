package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Entretien;
import fr.univ.m1.projetagile.core.entity.PrixEntretien;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.PrixEntretienRepository;
import fr.univ.m1.projetagile.enums.TypeV;
import java.util.List;

/**
 * Service layer for managing PrixEntretien (maintenance pricing) operations. Provides business
 * logic for creating, updating, and querying maintenance prices.
 */
public class PrixEntretienService {

  private final PrixEntretienRepository repository;

  public PrixEntretienService() {
    this.repository = new PrixEntretienRepository();
  }

  /**
   * Creates a new maintenance price entry.
   *
   * @param entretien the maintenance company
   * @param typeVehi the vehicle type
   * @param modeleVehi the vehicle model
   * @param prix the price
   * @return the created PrixEntretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public PrixEntretien createPrixEntretien(Entretien entretien, TypeV typeVehi, String modeleVehi,
      Double prix) {
    // Validate inputs
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }

    if (typeVehi == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être null");
    }

    if (modeleVehi == null || modeleVehi.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle de véhicule ne peut pas être vide");
    }

    if (prix == null || prix < 0) {
      throw new IllegalArgumentException("Le prix doit être un nombre positif");
    }

    // Check if price already exists for this combination
    PrixEntretien existing =
        repository.findByEntretienAndVehiculeTypeAndModel(entretien, typeVehi, modeleVehi);
    if (existing != null) {
      throw new IllegalArgumentException(
          "Un prix existe déjà pour ce type de véhicule et ce modèle");
    }

    // Create and save the price
    PrixEntretien prixEntretien = new PrixEntretien(typeVehi, modeleVehi, prix, entretien);
    return repository.save(prixEntretien);
  }

  /**
   * Updates an existing maintenance price.
   *
   * @param prixEntretienId the ID of the price to update
   * @param nouveauPrix the new price
   * @return the updated PrixEntretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public PrixEntretien updatePrix(Long prixEntretienId, Double nouveauPrix) {
    if (prixEntretienId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    if (nouveauPrix == null || nouveauPrix < 0) {
      throw new IllegalArgumentException("Le prix doit être un nombre positif");
    }

    PrixEntretien prixEntretien = repository.findById(prixEntretienId);
    if (prixEntretien == null) {
      throw new IllegalArgumentException("Aucun prix trouvé avec l'id: " + prixEntretienId);
    }

    prixEntretien.setPrix(nouveauPrix);
    return repository.save(prixEntretien);
  }

  /**
   * Updates the vehicle type of a price entry.
   *
   * @param prixEntretienId the ID of the price to update
   * @param nouveauType the new vehicle type
   * @return the updated PrixEntretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public PrixEntretien updateTypeVehicule(Long prixEntretienId, TypeV nouveauType) {
    if (prixEntretienId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    if (nouveauType == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être null");
    }

    PrixEntretien prixEntretien = repository.findById(prixEntretienId);
    if (prixEntretien == null) {
      throw new IllegalArgumentException("Aucun prix trouvé avec l'id: " + prixEntretienId);
    }

    prixEntretien.setTypeVehi(nouveauType);
    return repository.save(prixEntretien);
  }

  /**
   * Updates the vehicle model of a price entry.
   *
   * @param prixEntretienId the ID of the price to update
   * @param nouveauModele the new vehicle model
   * @return the updated PrixEntretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public PrixEntretien updateModeleVehicule(Long prixEntretienId, String nouveauModele) {
    if (prixEntretienId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    if (nouveauModele == null || nouveauModele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle de véhicule ne peut pas être vide");
    }

    PrixEntretien prixEntretien = repository.findById(prixEntretienId);
    if (prixEntretien == null) {
      throw new IllegalArgumentException("Aucun prix trouvé avec l'id: " + prixEntretienId);
    }

    prixEntretien.setModeleVehi(nouveauModele);
    return repository.save(prixEntretien);
  }

  /**
   * Deletes a maintenance price entry.
   *
   * @param prixEntretienId the ID of the price to delete
   * @throws IllegalArgumentException if validation fails
   */
  public void deletePrixEntretien(Long prixEntretienId) {
    if (prixEntretienId == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    PrixEntretien prixEntretien = repository.findById(prixEntretienId);
    if (prixEntretien == null) {
      throw new IllegalArgumentException("Aucun prix trouvé avec l'id: " + prixEntretienId);
    }

    repository.delete(prixEntretienId);
  }

  /**
   * Retrieves all prices for a maintenance company.
   *
   * @param entretien the maintenance company
   * @return list of prices
   */
  public List<PrixEntretien> getPrixByEntretien(Entretien entretien) {
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }
    return repository.findByEntretien(entretien);
  }

  /**
   * Retrieves all prices for a specific vehicle type.
   *
   * @param typeVehi the vehicle type
   * @return list of prices
   */
  public List<PrixEntretien> getPrixByTypeVehicule(TypeV typeVehi) {
    if (typeVehi == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être null");
    }
    return repository.findByTypeVehicule(typeVehi);
  }

  /**
   * Finds a specific price for a vehicle type and model from a maintenance company.
   *
   * @param entretien the maintenance company
   * @param typeVehi the vehicle type
   * @param modeleVehi the vehicle model
   * @return the PrixEntretien if found, null otherwise
   */
  public PrixEntretien findPrix(Entretien entretien, TypeV typeVehi, String modeleVehi) {
    if (entretien == null || typeVehi == null || modeleVehi == null
        || modeleVehi.trim().isEmpty()) {
      throw new IllegalArgumentException("Tous les paramètres sont requis");
    }
    return repository.findByEntretienAndVehiculeTypeAndModel(entretien, typeVehi, modeleVehi);
  }

  /**
   * Calculates the maintenance price for a specific vehicle from a maintenance company.
   *
   * @param entretien the maintenance company
   * @param vehicule the vehicle
   * @return the price if found, null otherwise
   */
  public Double calculerPrixPourVehicule(Entretien entretien, Vehicule vehicule) {
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }

    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }

    PrixEntretien prix = repository.findByEntretienAndVehiculeTypeAndModel(entretien,
        vehicule.getType(), vehicule.getModele());

    return (prix != null) ? prix.getPrix() : null;
  }

  /**
   * Retrieves a price by its ID.
   *
   * @param id the ID of the price
   * @return the PrixEntretien entity
   * @throws IllegalArgumentException if not found
   */
  public PrixEntretien findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    PrixEntretien prixEntretien = repository.findById(id);
    if (prixEntretien == null) {
      throw new IllegalArgumentException("Aucun prix trouvé avec l'id: " + id);
    }

    return prixEntretien;
  }

  /**
   * Retrieves all maintenance prices.
   *
   * @return list of all prices
   */
  public List<PrixEntretien> getAllPrix() {
    return repository.findAll();
  }
}
