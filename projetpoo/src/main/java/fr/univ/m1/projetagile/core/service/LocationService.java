package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.parking.entity.Parking;

/**
 * Service métier pour la gestion des locations de véhicules. Fournit les opérations CRUD et les
 * fonctionnalités métier liées aux locations.
 */
public class LocationService {

  private final LocationRepository locationRepository;

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
    if (dateFin.isBefore(dateDebut)) {
      throw new IllegalArgumentException(
          "La date de fin doit être postérieure à la date de début.");
    }
    if (vehicule == null || vehicule.getId() == null) {
      throw new IllegalArgumentException("Le véhicule doit être spécifié et enregistré.");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur doit être spécifié.");
    }

    // Vérifier si le lieu de dépôt est un Parking
    if (lieuDepot instanceof Parking) {
      Agent agent = vehicule.getProprietaire();
      if (agent == null) {
        throw new IllegalStateException("Le véhicule n'a pas de propriétaire associé.");
      }

      // Vérifier si l'agent a une souscription à l'option Parking (ID 5)
      boolean aOptionParking =
          agent.getOptionsActives().stream().anyMatch(so -> so.getOption() != null
              && so.getOption().getId() != null && so.getOption().getId().equals(5L));

      if (!aOptionParking) {
        throw new IllegalStateException(
            "L'agent n'a pas activé l'option pour permettre de déposer ce véhicule dans un parking");
      }
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
   * le prix de base - Des frais fixes de 2€ par jour
   *
   * @param location la location pour laquelle calculer le prix
   * @return le prix total de la location
   */
  public double getPrixLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    // Calcul du nombre de jours de location
    long nombreJours = ChronoUnit.DAYS.between(location.getDateDebut(), location.getDateFin());

    // Prix de base = prix par jour × nombre de jours
    double prixBase = location.getVehicule().getPrixJ() * nombreJours;

    // Commission de 10% sur le prix de base
    double commissionProportionnelle = prixBase * 0.1;

    // Frais fixes de 2€ par jour
    double fraisFixes = 2.0 * nombreJours;

    // Prix total = prix de base + commission + frais fixes
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
