package fr.univ.m1.projetagile.parking.service;

import java.util.List;
import fr.univ.m1.projetagile.parking.entity.Parking;
import fr.univ.m1.projetagile.parking.persistence.ParkingRepository;

/**
 * Service métier responsable de la gestion des parkings.
 * <p>
 * Cette classe constitue la couche de service autour du {@link ParkingRepository} et des entités
 * {@link Parking}. Elle fournit des opérations pour :
 * <ul>
 * <li>Créer de nouveaux parkings à partir des informations fournies ;</li>
 * <li>Récupérer des parkings par leur identifiant ou par ville ;</li>
 * <li>Supprimer des parkings.</li>
 * </ul>
 * L'objectif est d'encapsuler la logique métier liée aux parkings et de séparer les préoccupations
 * entre la persistance, la logique métier et la présentation.
 */
public class ParkingService {

  private ParkingRepository parkingRepository;

  public ParkingService(ParkingRepository parkingRepository) {
    this.parkingRepository = parkingRepository;
  }

  /**
   * Crée un nouveau parking avec validation des données d'entrée
   *
   * @param nom le nom du parking
   * @param rue la rue où se trouve le parking
   * @param ville la ville où se trouve le parking
   * @param cp le code postal
   * @param prix le prix du parking (doit être positif)
   * @return le parking créé et sauvegardé en base de données
   * @throws IllegalArgumentException si un paramètre est invalide (null, vide, ou négatif)
   */
  public Parking createParking(String nom, String rue, String ville, String cp, Double prix) {
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du parking ne peut pas être vide.");
    }
    if (rue == null || rue.trim().isEmpty()) {
      throw new IllegalArgumentException("La rue du parking ne peut pas être vide.");
    }
    if (ville == null || ville.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville du parking ne peut pas être vide.");
    }
    if (cp == null || cp.trim().isEmpty()) {
      throw new IllegalArgumentException("Le code postal du parking ne peut pas être vide.");
    }
    if (prix == null || prix.doubleValue() < 0.0) {
      throw new IllegalArgumentException("Le prix du parking ne peut pas être négatif.");
    }

    Parking parking = new Parking(nom, rue, ville, cp, prix);
    return parkingRepository.save(parking);
  }

  /**
   * Récupère un parking par son identifiant
   *
   * @param id l'identifiant du parking
   * @return le parking trouvé ou null si aucun parking n'existe avec cet identifiant
   * @throws IllegalArgumentException si l'identifiant est nul
   */
  public Parking getParkingById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant du parking ne peut pas être nul.");
    }
    return parkingRepository.findById(id);
  }

  /**
   * Supprime un parking par son identifiant
   *
   * @param parkingId l'identifiant du parking à supprimer
   * @throws IllegalArgumentException si le parking n'existe pas ou si l'identifiant est nul
   */
  public void deleteParking(Long parkingId) {
    if (parkingId == null) {
      throw new IllegalArgumentException("L'identifiant du parking ne peut pas être nul.");
    }

    Parking parking = parkingRepository.findById(parkingId);
    if (parking == null) {
      throw new IllegalArgumentException("Aucun parking trouvé avec l'identifiant " + parkingId);
    }

    parkingRepository.delete(parkingId);
  }

  /**
   * Récupère tous les parkings d'une ville donnée
   *
   * @param ville la ville de recherche
   * @return la liste des parkings trouvés dans cette ville
   * @throws IllegalArgumentException si la ville est nulle ou vide
   */
  public List<Parking> getParkingsByVille(String ville) {
    if (ville == null || ville.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville ne peut pas être vide.");
    }
    return parkingRepository.findByVille(ville);
  }

  /**
   * Récupère le coût d'un parking par son identifiant
   *
   * @param id l'identifiant du parking
   * @return le coût du parking
   * @throws IllegalArgumentException si l'identifiant est nul ou si le parking n'existe pas
   */
  public Double getCoutParking(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant du parking ne peut pas être nul.");
    }

    Parking parking = parkingRepository.findById(id);
    if (parking == null) {
      throw new IllegalArgumentException("Aucun parking trouvé avec l'identifiant " + id);
    }

    return parking.getCoutSupp();
  }
}
