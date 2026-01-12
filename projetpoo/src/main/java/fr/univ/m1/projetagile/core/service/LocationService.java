package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
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
   * Crée et enregistre une nouvelle location après validation métier.
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin, String lieuDepot,
      Vehicule vehicule, Loueur loueur) {

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
    }
    if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
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
   * Calcule le prix total d'une location : - prix de base = prix/jour × nb jours - commission
   * proportionnelle = 10% du prix de base - frais fixes = 2€ × nb jours
   *
   * ✅ Modif #99 : le nombre de jours vient de location.getNombreJours()
   */
  public double getPrixLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }

    // ✅ centralisé dans Location (LLD #99)
    int nombreJours = location.getNombreJours();

    double prixBase = location.getVehicule().getPrixJ() * nombreJours;
    double commissionProportionnelle = prixBase * 0.1;
    double fraisFixes = 2.0 * nombreJours;

    return prixBase + commissionProportionnelle + fraisFixes;
  }

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
