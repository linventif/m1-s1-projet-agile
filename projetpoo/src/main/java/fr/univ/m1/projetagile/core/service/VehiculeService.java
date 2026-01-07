package fr.univ.m1.projetagile.core.service;

import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO.DisponibiliteDTO;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;

public class VehiculeService {

  private VehiculeRepository vehiculeRepository;

  public VehiculeService(VehiculeRepository vehiculeRepository) {
    this.vehiculeRepository = vehiculeRepository;
  }

  /**
   * Récupère tous les véhicules avec leurs informations enrichies
   *
   * @return Liste de VehiculeDTO contenant : - Les propriétés du véhicule - La note moyenne
   *         calculée - Les dates de disponibilités - Le lieu (ville)
   */
  public List<VehiculeDTO> getVehicules() {
    List<Vehicule> vehicules = vehiculeRepository.findAll();

    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  /**
   * Convertit une entité Vehicule en VehiculeDTO
   *
   * @param vehicule L'entité à convertir
   * @return Le DTO correspondant avec toutes les informations
   */
  private VehiculeDTO convertToDTO(Vehicule vehicule) {
    VehiculeDTO dto = new VehiculeDTO();

    // Propriétés de base du véhicule
    dto.setIdV(vehicule.getIdV());
    dto.setType(vehicule.getType());
    dto.setMarque(vehicule.getMarque());
    dto.setModele(vehicule.getModele());
    dto.setCouleur(vehicule.getCouleur());
    dto.setVille(vehicule.getVille()); // Lieu du véhicule
    dto.setPrixJ(vehicule.getPrixJ());
    dto.setDisponible(vehicule.isDisponible());

    // Note moyenne calculée
    // TODO : Récupérer toutes les NoteV pour ce véhicule et calculer la moyenne
    // A FAIRE DANS UN PACKAGE NOTES
    dto.setNoteMoyenne(vehicule.calculerNote());

    // Dates de disponibilités
    List<DisponibiliteDTO> disponibilites = vehicule.getDatesDispo().stream()
        .map(this::convertDisponibiliteToDTO).collect(Collectors.toList());
    dto.setDatesDispo(disponibilites);

    return dto;
  }

  /**
   * Convertit une entité Disponibilite en DisponibiliteDTO
   *
   * @param disponibilite L'entité à convertir
   * @return Le DTO correspondant
   */
  private DisponibiliteDTO convertDisponibiliteToDTO(Disponibilite disponibilite) {
    return new DisponibiliteDTO(disponibilite.getId(), disponibilite.getDateDebut(),
        disponibilite.getDateFin());
  }
}
