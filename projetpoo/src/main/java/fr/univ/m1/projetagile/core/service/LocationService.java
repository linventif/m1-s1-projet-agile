package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;

/**
 * Service métier pour la gestion des locations de véhicules. Fournit les opérations CRUD et les
 * fonctionnalités métier liées aux locations.
 */
public class LocationService {

  private final LocationRepository locationRepository;

  // ==================== #100 : règles commission ====================
  private static final double COMMISSION_NORMALE = 0.10; // 10%
  private static final double COMMISSION_LLD = 0.05; // 5% (rabais LLD)

  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  /**
   * Crée et enregistre une nouvelle location après validation métier basique.
   *
   * @param dateDebut date et heure de début souhaitées
   * @param dateFin date et heure de fin souhaitées
   * @param lieuDepot lieu de dépôt du véhicule (facultatif, peut être null)
   * @param vehicule véhicule concerné (doit être déjà persisté)
   * @param loueur loueur effectuant la réservation
   * @return la location sauvegardée
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin,
      LieuRestitution lieuDepot, Vehicule vehicule, Loueur loueur) {

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
    }
    if (!dateFin.isAfter(dateDebut)) {
      throw new IllegalArgumentException(
          "La date de fin doit être strictement postérieure à la date de début.");
    }
    if (vehicule == null || vehicule.getId() == null) {
      throw new IllegalArgumentException("Le véhicule doit être spécifié et enregistré.");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur doit être spécifié.");
    }

    LocalDate debutJour = dateDebut.toLocalDate();
    LocalDate finJour = dateFin.toLocalDate();
    boolean disponible =
        locationRepository.isVehicleAvailable(vehicule.getId(), debutJour, finJour);
    if (!disponible) {
      throw new IllegalStateException("Le véhicule n'est pas disponible pour cette période.");
    }

    Location location = new Location(dateDebut, dateFin, lieuDepot, vehicule, loueur);
    return locationRepository.save(location);
  }

  /**
   * Crée et enregistre une nouvelle location sans lieu de dépôt spécifié.
   *
   * @param dateDebut date et heure de début souhaitées
   * @param dateFin date et heure de fin souhaitées
   * @param vehicule véhicule concerné (doit être déjà persisté)
   * @param loueur loueur effectuant la réservation
   * @return la location sauvegardée
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin, Vehicule vehicule,
      Loueur loueur) {
    return creerLocation(dateDebut, dateFin, null, vehicule, loueur);
  }

  /**
   * Calcule le prix total d'une location en fonction de la durée et du véhicule. Le prix comprend :
   * - Le prix de base (prix par jour × nombre de jours) - Une commission proportionnelle de 10% sur
   * le prix de base (ou 5% si location de longue durée) - Des frais fixes de 2€ par jour
   *
   * ✅ #99 : durée calculée par location.getNombreJours() ✅ #100 : rabais LLD sur la commission
   * (part variable)
   *
   * @param location la location pour laquelle calculer le prix
   * @return le prix total de la location
   */
  public double getPrixLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }

    // ✅ #99 : on utilise la méthode centralisée dans Location
    int nombreJours = location.getNombreJours();

    // Prix de base
    double prixBase = location.getVehicule().getPrixJ() * nombreJours;

    // ✅ #100 : commission réduite si LLD
    double tauxCommission = location.estLongueDuree() ? COMMISSION_LLD : COMMISSION_NORMALE;
    double commissionProportionnelle = prixBase * tauxCommission;

    // Frais fixes
    double fraisFixes = 2.0 * nombreJours;

    return prixBase + commissionProportionnelle + fraisFixes;
  }

  /**
   * Annule une location en cours. Change le statut de la location à ANNULER et la sauvegarde en
   * base de données.
   *
   * @param location la location à annuler
   */
  public void annuler(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    StatutLocation statutActuel = location.getStatut();
    if (statutActuel != StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT
        && statutActuel != StatutLocation.ACCEPTE) {
      throw new IllegalStateException(
          "Annulation impossible : la location ne peut être annulée que si son statut est "
              + "EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT ou ACCEPTE.");
    }
    location.setStatut(StatutLocation.ANNULE);
    locationRepository.save(location);
  }

  /**
   * Termine une location en cours. Change le statut de la location à TERMINE et la sauvegarde en
   * base de données.
   *
   * @param location la location à terminer
   */
  public void terminer(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    StatutLocation statutActuel = location.getStatut();
    if (statutActuel != StatutLocation.ACCEPTE) {
      throw new IllegalStateException(
          "Terminaison impossible : la location ne peut être terminée que si son statut est ACCEPTE.");
    }
    location.setStatut(StatutLocation.TERMINE);
    locationRepository.save(location);
  }

  /**
   * Récupère une location par son identifiant et la convertit en LocationDTO
   *
   * @param id l'identifiant de la location
   * @return le LocationDTO correspondant avec le prix total calculé, ou null si la location
   *         n'existe pas
   */
  public LocationDTO getLocation(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }

    Location location = locationRepository.findById(id);
    if (location == null) {
      return null;
    }

    return convertLocationToDTO(location);
  }

  /**
   * Convertit une entité Location en LocationDTO
   *
   * @param location l'entité Location à convertir
   * @return le DTO correspondant avec toutes les informations incluant le prix total
   */
  private LocationDTO convertLocationToDTO(Location location) {
    LocationDTO dto = new LocationDTO();

    dto.setId(location.getId());
    dto.setDateDebut(location.getDateDebut());
    dto.setDateFin(location.getDateFin());
    dto.setLieuDepot(location.getLieuDepot());
    dto.setStatut(location.getStatut());

    // Calculer et définir le prix total
    dto.setPrixTotal(getPrixLocation(location));

    // Convertir le véhicule en VehiculeDTO
    if (location.getVehicule() != null) {
      Vehicule vehicule = location.getVehicule();
      VehiculeDTO vehiculeDTO = new VehiculeDTO();
      vehiculeDTO.setId(vehicule.getId());
      vehiculeDTO.setType(vehicule.getType());
      vehiculeDTO.setMarque(vehicule.getMarque());
      vehiculeDTO.setModele(vehicule.getModele());
      vehiculeDTO.setCouleur(vehicule.getCouleur());
      vehiculeDTO.setVille(vehicule.getVille());
      vehiculeDTO.setPrixJ(vehicule.getPrixJ());
      vehiculeDTO.setDisponible(vehicule.isDisponible());
      vehiculeDTO.setNoteMoyenne(vehicule.calculerNote());

      dto.setVehicule(vehiculeDTO);
    }

    return dto;
  }
}
