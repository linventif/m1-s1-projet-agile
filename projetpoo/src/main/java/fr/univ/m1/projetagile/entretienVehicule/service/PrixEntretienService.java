package fr.univ.m1.projetagile.entretienVehicule.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.entretienVehicule.entity.Entretien;
import fr.univ.m1.projetagile.entretienVehicule.entity.PrixEntretien;
import fr.univ.m1.projetagile.entretienVehicule.persistence.PrixEntretienRepository;
import fr.univ.m1.projetagile.enums.TypeV;

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
   * @param entretien the Entretien entity representing the maintenance company
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
   * Importe des prix d'entretien depuis un fichier CSV et les ajoute pour une société d'entretien.
   * Le format CSV attendu est : type,modele,prix (avec ou sans ligne d'entête).
   *
   * @param entretien la société d'entretien pour laquelle ajouter les prix
   * @param cheminFichier le chemin vers le fichier CSV
   * @throws IOException si une erreur se produit lors de la lecture du fichier
   * @throws IllegalArgumentException si le format du CSV est invalide ou si les données sont
   *         incorrectes
   */
  public void createPrixEntretienDepuisCSV(Entretien entretien, String cheminFichier)
      throws IOException {
    if (entretien == null) {
      throw new IllegalArgumentException("L'entretien ne peut pas être null");
    }
    if (cheminFichier == null || cheminFichier.isBlank()) {
      throw new IllegalArgumentException("cheminFichier vide");
    }

    try (Stream<String> lignes = Files.lines(Paths.get(cheminFichier))) {
      // On saute la première ligne si c'est une entête (header)
      List<String[]> donnees =
          lignes.skip(1).map(ligne -> ligne.split(",")).collect(Collectors.toList());

      for (String[] colonnes : donnees) {
        if (colonnes.length < 3) {
          throw new IllegalArgumentException(
              "Format CSV invalide: au moins 3 colonnes requises (type,modele,prix)");
        }

        String typeStr = colonnes[0].trim();
        String modele = colonnes[1].trim();
        String prixStr = colonnes[2].trim();

        // Convertir le type en enum TypeV
        TypeV type;
        try {
          type = TypeV.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Type de véhicule invalide: " + typeStr
              + ". Les valeurs acceptées sont: voiture, camion, moto");
        }

        // Convertir le prix en double
        double prix;
        try {
          prix = Double.parseDouble(prixStr);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Prix invalide: " + prixStr);
        }

        // Ajouter le prix d'entretien
        createPrixEntretien(entretien, type, modele, prix);
      }
    }
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

    // Check if the new combination already exists (excluding the current entry)
    PrixEntretien existing = repository.findByEntretienAndVehiculeTypeAndModel(
        prixEntretien.getEntretien(), nouveauType, prixEntretien.getModeleVehi());
    if (existing != null && !existing.getId().equals(prixEntretienId)) {
      throw new IllegalArgumentException(
          "Un prix existe déjà pour ce type de véhicule et ce modèle");
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

    // Check if the new combination already exists (excluding the current entry)
    PrixEntretien existing = repository.findByEntretienAndVehiculeTypeAndModel(
        prixEntretien.getEntretien(), prixEntretien.getTypeVehi(), nouveauModele);
    if (existing != null && !existing.getId().equals(prixEntretienId)) {
      throw new IllegalArgumentException(
          "Un prix existe déjà pour ce type de véhicule et ce modèle");
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
