package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Entretien;
import fr.univ.m1.projetagile.core.persistence.EntretienRepository;

/**
 * Service layer for managing Entretien (maintenance company) operations. Extends
 * UtilisateurService to inherit common user operations such as authentication, email lookup,
 * and password management for maintenance company accounts.
 */
public class EntretienService extends UtilisateurService<Entretien, EntretienRepository> {

  public EntretienService() {
    super(new EntretienRepository());
  }

  /**
   * Creates a new maintenance company account.
   *
   * @param email the company email
   * @param motDePasse the password
   * @param nomEntreprise the company name
   * @return the created Entretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public Entretien createEntretien(String email, String motDePasse, String nomEntreprise) {
    // Validate common fields (email, password)
    validateCommonFields(email, motDePasse);

    // Validate company name
    if (nomEntreprise == null || nomEntreprise.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'entreprise ne peut pas être vide");
    }

    // Check if company name already exists
    if (repository.existsByNomEntreprise(nomEntreprise)) {
      throw new IllegalArgumentException(
          "Une entreprise avec ce nom existe déjà: " + nomEntreprise);
    }

    // Create and save the Entretien entity
    Entretien entretien = new Entretien(email, motDePasse, nomEntreprise);
    return repository.save(entretien);
  }

  /**
   * Finds an Entretien by company name.
   *
   * @param nomEntreprise the company name
   * @return the Entretien entity, or null if not found
   */
  public Entretien findByNomEntreprise(String nomEntreprise) {
    if (nomEntreprise == null || nomEntreprise.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'entreprise ne peut pas être vide");
    }
    return repository.findByNomEntreprise(nomEntreprise);
  }

  /**
   * Updates the company name of an Entretien.
   *
   * @param entretien the maintenance company to update
   * @param nouveauNom the new company name
   * @return the updated Entretien entity
   * @throws IllegalArgumentException if validation fails
   */
  public Entretien updateNomEntreprise(Entretien entretien, String nouveauNom) {
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }

    if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nouveau nom ne peut pas être vide");
    }

    // Check if new name is different from current name
    if (nouveauNom.equals(entretien.getNomEntreprise())) {
      return entretien; // No change needed
    }

    // Check if new name already exists
    if (repository.existsByNomEntreprise(nouveauNom)) {
      throw new IllegalArgumentException(
          "Une entreprise avec ce nom existe déjà: " + nouveauNom);
    }

    entretien.setNomEntreprise(nouveauNom);
    return repository.save(entretien);
  }

  /**
   * Deletes a maintenance company account by ID.
   *
   * @param id the ID of the company to delete
   * @throws IllegalArgumentException if id is null
   */
  public void deleteEntretien(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id ne peut pas être null");
    }

    Entretien entretien = repository.findById(id);
    if (entretien == null) {
      throw new IllegalArgumentException("Aucune entreprise trouvée avec l'id: " + id);
    }

    repository.delete(id);
  }

  /**
   * Authenticates a maintenance company and returns the entity if successful.
   *
   * @param email the company email
   * @param motDePasse the password
   * @return the authenticated Entretien entity
   * @throws IllegalArgumentException if authentication fails
   */
  @Override
  public Entretien connect(String email, String motDePasse) {
    return super.connect(email, motDePasse);
  }
}
