package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;

/**
 * Service métier pour la gestion des locations de véhicules.
 */
public class LocationService {

  private final LocationRepository locationRepository;

  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  /**
   * Crée et enregistre une nouvelle location après validation métier basique.
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
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin, Vehicule vehicule,
      Loueur loueur) {
    return creerLocation(dateDebut, dateFin, null, vehicule, loueur);
  }

  /**
   * ✅ Utilitaire : nombre de jours d'une location. (Tu peux l'utiliser dans MainDemo pour afficher
   * "Jours: X")
   */
  public long getNombreJours(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    long jours = ChronoUnit.DAYS.between(location.getDateDebut(), location.getDateFin());
    return jours;
  }

  /**
   * ✅ #42 - Historique des locations d’un véhicule (du plus récent au plus ancien).
   */
  public List<Location> getHistoriqueLocationsVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }
    return locationRepository.getHistoriqueLocations(vehiculeId);
  }

  /**
   * ✅ #42 - Historique des locations d’un véhicule en DTO (pratique pour affichage).
   */
  public List<LocationDTO> getHistoriqueLocationsVehiculeDTO(Long vehiculeId) {
    return getHistoriqueLocationsVehicule(vehiculeId).stream().map(this::convertLocationToDTO)
        .collect(Collectors.toList());
  }

  /**
   * Calcule le prix total d'une location. Base = prix/jour × jours + commission 10% + 2€ par jour
   */
  public double getPrixLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }

    long nombreJours = ChronoUnit.DAYS.between(location.getDateDebut(), location.getDateFin());

    double prixBase = location.getVehicule().getPrixJ() * nombreJours;
    double commissionProportionnelle = prixBase * 0.1;
    double fraisFixes = 2.0 * nombreJours;

    return prixBase + commissionProportionnelle + fraisFixes;
  }

  /**
   * Annule une location en cours.
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
   * Termine une location en cours.
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
   * Récupère une location par son identifiant et la convertit en LocationDTO.
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
   * Convertit une entité Location en LocationDTO.
   */
  private LocationDTO convertLocationToDTO(Location location) {
    LocationDTO dto = new LocationDTO();

    dto.setId(location.getId());
    dto.setDateDebut(location.getDateDebut());
    dto.setDateFin(location.getDateFin());
    dto.setLieuDepot(location.getLieuDepot());
    dto.setStatut(location.getStatut());
    dto.setPrixTotal(getPrixLocation(location));

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
