package fr.univ.m1.projetagile.VerificationLocation.service;

import fr.univ.m1.projetagile.VerificationLocation.entity.Verification;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;

/**
 * Service métier pour la gestion des vérifications de locations. Fournit les opérations CRUD et les
 * fonctionnalités métier liées aux vérifications.
 */
public class VerificationService {

  private final VerificationRepository verificationRepository;
  private final LocationRepository locationRepository;

  public VerificationService(VerificationRepository verificationRepository,
      LocationRepository locationRepository) {
    this.verificationRepository = verificationRepository;
    this.locationRepository = locationRepository;
  }

  /**
   * Crée et enregistre une nouvelle vérification pour une location donnée avec le kilométrage de
   * début. Vérifie qu'il n'existe pas déjà une vérification pour cette location (une seule
   * vérification par location).
   *
   * @param locationId l'identifiant de la location
   * @param kilometrageDebut le kilométrage du véhicule au début de la location
   * @return la vérification sauvegardée
   * @throws IllegalArgumentException si la location n'existe pas ou si une vérification existe déjà
   *         pour cette location
   */
  public Verification creerVerification(Long locationId, Integer kilometrageDebut) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }
    if (kilometrageDebut == null || kilometrageDebut < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de début doit être un entier positif ou nul.");
    }

    // Vérifier que la location existe
    Location location = locationRepository.findById(locationId);
    if (location == null) {
      throw new IllegalArgumentException(
          "Aucune location trouvée avec l'identifiant " + locationId);
    }

    // Vérifier qu'il n'existe pas déjà une vérification pour cette location
    Verification verificationExistante = verificationRepository.findByLocationId(locationId);
    if (verificationExistante != null) {
      throw new IllegalStateException(
          "Une vérification existe déjà pour la location " + locationId + ".");
    }

    Verification verification = new Verification(location, kilometrageDebut);
    return verificationRepository.save(verification);
  }

  /**
   * Enregistre le kilométrage de début pour une location. Crée une vérification si elle n'existe
   * pas encore, ou met à jour le kilométrage de début si la vérification existe déjà. Cette méthode
   * est appelée lorsque le loueur récupère le véhicule.
   *
   * @param locationId l'identifiant de la location
   * @param kilometrageDebut le kilométrage du véhicule au moment de la récupération
   * @return la vérification créée ou mise à jour
   * @throws IllegalArgumentException si la location n'existe pas ou si le kilométrage est invalide
   */
  public Verification enregistrerKilometrageDebut(Long locationId, Integer kilometrageDebut) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }
    if (kilometrageDebut == null || kilometrageDebut < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de début doit être un entier positif ou nul.");
    }

    // Vérifier que la location existe
    Location location = locationRepository.findById(locationId);
    if (location == null) {
      throw new IllegalArgumentException(
          "Aucune location trouvée avec l'identifiant " + locationId);
    }

    // Vérifier si une vérification existe déjà pour cette location
    Verification verificationExistante = verificationRepository.findByLocationId(locationId);
    if (verificationExistante != null) {
      // Mettre à jour le kilométrage de début
      verificationExistante.setKilometrageDebut(kilometrageDebut);
      return verificationRepository.save(verificationExistante);
    } else {
      // Créer une nouvelle vérification
      Verification verification = new Verification(location, kilometrageDebut);
      return verificationRepository.save(verification);
    }
  }

  /**
   * Récupère une vérification par son identifiant.
   *
   * @param id l'identifiant de la vérification
   * @return la vérification trouvée ou null si elle n'existe pas
   * @throws IllegalArgumentException si l'identifiant est nul
   */
  public Verification getVerification(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }
    return verificationRepository.findById(id);
  }

  /**
   * Récupère une vérification à partir de l'identifiant de la location associée.
   *
   * @param locationId l'identifiant de la location
   * @return la vérification trouvée ou null si elle n'existe pas
   * @throws IllegalArgumentException si l'identifiant de la location est nul
   */
  public Verification getVerificationByLocationId(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }
    return verificationRepository.findByLocationId(locationId);
  }

  /**
   * Met à jour le kilométrage de fin d'une vérification existante.
   *
   * @param verificationId l'identifiant de la vérification
   * @param kilometrageFin le kilométrage de fin
   * @return la vérification mise à jour
   * @throws IllegalArgumentException si l'identifiant est nul, si le kilométrage est invalide, ou
   *         si la vérification n'existe pas
   */
  public Verification modifierKilometrageFin(Long verificationId, Integer kilometrageFin) {
    if (verificationId == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }
    if (kilometrageFin == null || kilometrageFin < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de fin doit être un entier positif ou nul.");
    }

    Verification verification = verificationRepository.findById(verificationId);
    if (verification == null) {
      throw new IllegalArgumentException(
          "Aucune vérification trouvée avec l'identifiant " + verificationId);
    }

    verification.setKilometrageFin(kilometrageFin);
    return verificationRepository.save(verification);
  }

  /**
   * Met à jour le kilométrage de début d'une vérification existante.
   *
   * @param verificationId l'identifiant de la vérification
   * @param kilometrageDebut le kilométrage de début
   * @return la vérification mise à jour
   * @throws IllegalArgumentException si l'identifiant est nul, si le kilométrage est invalide, ou
   *         si la vérification n'existe pas
   */
  public Verification modifierKilometrageDebut(Long verificationId, Integer kilometrageDebut) {
    if (verificationId == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }
    if (kilometrageDebut == null || kilometrageDebut < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de début doit être un entier positif ou nul.");
    }

    Verification verification = verificationRepository.findById(verificationId);
    if (verification == null) {
      throw new IllegalArgumentException(
          "Aucune vérification trouvée avec l'identifiant " + verificationId);
    }

    verification.setKilometrageDebut(kilometrageDebut);
    return verificationRepository.save(verification);
  }

  /**
   * Met à jour la photo d'une vérification existante.
   *
   * @param verificationId l'identifiant de la vérification
   * @param photo la nouvelle photo (peut être null pour supprimer la photo)
   * @return la vérification mise à jour
   * @throws IllegalArgumentException si l'identifiant est nul ou si la vérification n'existe pas
   */
  public Verification modifierPhoto(Long verificationId, String photo) {
    if (verificationId == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }

    Verification verification = verificationRepository.findById(verificationId);
    if (verification == null) {
      throw new IllegalArgumentException(
          "Aucune vérification trouvée avec l'identifiant " + verificationId);
    }

    verification.setPhoto(photo);
    return verificationRepository.save(verification);
  }

  /**
   * Vérifie la fin d'une location en enregistrant le kilométrage de fin et la photo. Le kilométrage
   * de fin doit être strictement supérieur au kilométrage de début.
   *
   * @param verificationId l'identifiant de la vérification
   * @param kilometrageFin le kilométrage de fin
   * @param photo la nouvelle photo (peut être null)
   * @return la vérification mise à jour
   * @throws IllegalArgumentException si l'identifiant est nul, si le kilométrage est invalide, si
   *         le kilométrage de fin n'est pas strictement supérieur au kilométrage de début, ou si la
   *         vérification n'existe pas
   */
  public Verification verifierFinLocation(Long verificationId, Integer kilometrageFin,
      String photo) {
    if (verificationId == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }
    if (kilometrageFin == null || kilometrageFin < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de fin doit être un entier positif ou nul.");
    }

    Verification verification = verificationRepository.findById(verificationId);
    if (verification == null) {
      throw new IllegalArgumentException(
          "Aucune vérification trouvée avec l'identifiant " + verificationId);
    }

    // Vérifier que le kilométrage de début existe
    Integer kilometrageDebut = verification.getKilometrageDebut();
    if (kilometrageDebut == null) {
      throw new IllegalStateException(
          "Le kilométrage de début n'est pas défini pour cette vérification.");
    }

    // Vérifier que le kilométrage de fin est strictement supérieur au kilométrage de début
    if (kilometrageFin <= kilometrageDebut) {
      throw new IllegalArgumentException("Le kilométrage de fin (" + kilometrageFin
          + ") doit être strictement supérieur au kilométrage de début (" + kilometrageDebut
          + ").");
    }

    verification.setKilometrageFin(kilometrageFin);
    verification.setPhoto(photo);
    return verificationRepository.save(verification);
  }

  /**
   * Supprime une vérification de la base de données.
   *
   * @param id l'identifiant de la vérification à supprimer
   * @throws IllegalArgumentException si l'identifiant est nul ou si la vérification n'existe pas
   */
  public void supprimerVerification(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant de la vérification ne peut pas être nul.");
    }

    Verification verification = verificationRepository.findById(id);
    if (verification == null) {
      throw new IllegalArgumentException("Aucune vérification trouvée avec l'identifiant " + id);
    }

    verificationRepository.delete(id);
  }
}
