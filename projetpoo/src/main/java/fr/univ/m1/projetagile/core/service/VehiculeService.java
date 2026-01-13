package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.DisponibiliteRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.enums.TypeV;

public class VehiculeService {

  private final VehiculeRepository vehiculeRepository;
  private final DisponibiliteRepository disponibiliteRepository;

  public VehiculeService(VehiculeRepository vehiculeRepository) {
    if (vehiculeRepository == null) {
      throw new IllegalArgumentException("vehiculeRepository ne peut pas être null.");
    }
    this.vehiculeRepository = vehiculeRepository;
    this.disponibiliteRepository = new DisponibiliteRepository();
  }

  public VehiculeService(VehiculeRepository vehiculeRepository,
      DisponibiliteRepository disponibiliteRepository) {
    if (vehiculeRepository == null) {
      throw new IllegalArgumentException("vehiculeRepository ne peut pas être null.");
    }
    this.vehiculeRepository = vehiculeRepository;
    this.disponibiliteRepository =
        (disponibiliteRepository != null) ? disponibiliteRepository : new DisponibiliteRepository();
  }

  // ============================================================================
  // US.L.1 - Consultation / Recherche (DTO)
  // ============================================================================

  public List<VehiculeDTO> getVehicules() {
    List<Vehicule> vehicules = vehiculeRepository.findAll();
    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public VehiculeDTO getVehiculeById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    Vehicule v = vehiculeRepository.findById(id);
    if (v == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'id " + id);
    }
    return convertToDTO(v);
  }

  public List<VehiculeDTO> rechercherVehicules(String ville, TypeV type) {
    return getVehicules().stream()
        .filter(dto -> ville == null || ville.isBlank()
            || (dto.getVille() != null && dto.getVille().equalsIgnoreCase(ville)))
        .filter(dto -> type == null || dto.getType() == type)
        .collect(Collectors.toList());
  }

  public List<VehiculeDTO> rechercherVehiculesDisponibles(String ville, TypeV type,
      LocalDate dateDebut, LocalDate dateFin) {

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("dateDebut et dateFin sont obligatoires.");
    }
    if (dateFin.isBefore(dateDebut)) {
      throw new IllegalArgumentException("dateFin ne peut pas être avant dateDebut.");
    }

    return rechercherVehicules(ville, type).stream()
        .filter(dto -> dto.getDatesDispo() != null && dto.getDatesDispo().stream()
            .anyMatch(d -> d != null && d.length >= 2
                && (d[0].isBefore(dateDebut) || d[0].isEqual(dateDebut))
                && (d[1].isAfter(dateFin) || d[1].isEqual(dateFin))))
        .collect(Collectors.toList());
  }

  // ============================================================================
  // Véhicules (ancienne version conservée)
  // ============================================================================

  public List<VehiculeDTO> getVehiculesByAgent(Agent agent) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit avoir un identifiant.");
    }

    List<Vehicule> vehicules = vehiculeRepository.findByAgentId(agent.getIdU());
    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public Vehicule findVehiculeById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }
    Vehicule v = vehiculeRepository.findById(id);
    if (v == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + id);
    }
    return v;
  }

  public List<VehiculeDTO> searchVehiculesWithFilters(LocalDate dateDebut, LocalDate dateFin,
      String ville, String marque, String modele, String couleur, Double prixMin, Double prixMax,
      TypeV type) {

    List<Vehicule> vehicules = vehiculeRepository.findWithFilters(dateDebut, dateFin, ville, marque,
        modele, couleur, prixMin, prixMax, type);

    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public Vehicule createVehicule(TypeV type, String marque, String modele, String couleur,
      String ville, Double prixJ, Agent proprietaire) {

    if (type == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être nul.");
    }
    if (marque == null || marque.trim().isEmpty()) {
      throw new IllegalArgumentException("La marque du véhicule ne peut pas être vide.");
    }
    if (modele == null || modele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle du véhicule ne peut pas être vide.");
    }
    if (couleur == null || couleur.trim().isEmpty()) {
      throw new IllegalArgumentException("La couleur du véhicule ne peut pas être vide.");
    }
    if (ville == null || ville.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville du véhicule ne peut pas être vide.");
    }
    if (prixJ == null || prixJ.doubleValue() <= 0.0) {
      throw new IllegalArgumentException("Le prix journalier doit être strictement positif.");
    }
    if (proprietaire == null || proprietaire.getIdU() == null) {
      throw new IllegalArgumentException("Le propriétaire doit être déjà enregistré.");
    }

    Vehicule vehicule = new Vehicule(type, marque, modele, couleur, ville, prixJ, proprietaire);
    return vehiculeRepository.save(vehicule);
  }

  public void deleteVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    vehiculeRepository.delete(vehiculeId);
  }

  public void deleteVehiculeForAgent(Agent agent, Long vehiculeId) {
    verifyOwnershipAndGetVehicule(agent, vehiculeId);

    List<Object[]> locationsActives = vehiculeRepository.getDatesLocationsActives(vehiculeId);
    if (locationsActives != null && !locationsActives.isEmpty()) {
      throw new IllegalArgumentException(
          "Impossible de supprimer ce véhicule : des locations sont encore en cours ou planifiées.");
    }

    vehiculeRepository.delete(vehiculeId);
  }

  public Vehicule updateVehiculeType(Agent agent, Long vehiculeId, TypeV nouveauType) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouveauType == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être nul.");
    }
    vehicule.setType(nouveauType);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculeMarque(Agent agent, Long vehiculeId, String nouvelleMarque) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouvelleMarque == null || nouvelleMarque.trim().isEmpty()) {
      throw new IllegalArgumentException("La marque ne peut pas être vide.");
    }
    vehicule.setMarque(nouvelleMarque);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculeModele(Agent agent, Long vehiculeId, String nouveauModele) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouveauModele == null || nouveauModele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle ne peut pas être vide.");
    }
    vehicule.setModele(nouveauModele);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculeCouleur(Agent agent, Long vehiculeId, String nouvelleCouleur) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouvelleCouleur == null || nouvelleCouleur.trim().isEmpty()) {
      throw new IllegalArgumentException("La couleur ne peut pas être vide.");
    }
    vehicule.setCouleur(nouvelleCouleur);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculeVille(Agent agent, Long vehiculeId, String nouvelleVille) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouvelleVille == null || nouvelleVille.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville ne peut pas être vide.");
    }
    vehicule.setVille(nouvelleVille);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculePrixJ(Agent agent, Long vehiculeId, Double nouveauPrixJ) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    if (nouveauPrixJ == null || nouveauPrixJ.doubleValue() <= 0.0) {
      throw new IllegalArgumentException("Le prix journalier doit être strictement positif.");
    }
    vehicule.setPrixJ(nouveauPrixJ);
    return vehiculeRepository.save(vehicule);
  }

  public Vehicule updateVehiculeDisponibilite(Agent agent, Long vehiculeId, boolean disponible) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    vehicule.setDisponible(disponible);
    return vehiculeRepository.save(vehicule);
  }

  // ============================================================================
  // Disponibilités (ancienne version conservée)
  // ============================================================================

  public Disponibilite createDisponibilite(Agent agent, Long vehiculeId, LocalDate dateDebut,
      LocalDate dateFin) {

    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);
    validateDateRange(dateDebut, dateFin);

    List<Disponibilite> overlapping =
        disponibiliteRepository.findOverlappingOrAdjacent(vehiculeId, dateDebut, dateFin, null);

    if (overlapping == null || overlapping.isEmpty()) {
      Disponibilite disponibilite = new Disponibilite(vehicule, dateDebut, dateFin);
      return disponibiliteRepository.save(disponibilite);
    }
    return mergeDisponibilites(vehicule, overlapping, dateDebut, dateFin);
  }

  public List<Disponibilite> getDisponibilitesByVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }
    return disponibiliteRepository.findByVehiculeId(vehiculeId);
  }

  public List<Disponibilite> getFutureDisponibilitesByVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }
    return disponibiliteRepository.findFutureByVehiculeId(vehiculeId);
  }

  public List<Disponibilite> getAllDisponibilites() {
    return disponibiliteRepository.findAll();
  }

  public Disponibilite getDisponibiliteById(Long disponibiliteId) {
    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'identifiant de la disponibilité ne peut pas être nul.");
    }
    Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId);
    if (disponibilite == null) {
      throw new IllegalArgumentException(
          "Aucune disponibilité trouvée avec l'identifiant " + disponibiliteId);
    }
    return disponibilite;
  }

  public Disponibilite updateDisponibilite(Agent agent, Long disponibiliteId, LocalDate dateDebut,
      LocalDate dateFin) {

    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'identifiant de la disponibilité ne peut pas être nul.");
    }

    Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId);
    if (disponibilite == null) {
      throw new IllegalArgumentException(
          "Aucune disponibilité trouvée avec l'identifiant " + disponibiliteId);
    }

    Long vehiculeId = disponibilite.getVehicule().getId();
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    validateDateRange(dateDebut, dateFin);

    List<Disponibilite> overlapping = disponibiliteRepository
        .findOverlappingOrAdjacent(vehiculeId, dateDebut, dateFin, disponibiliteId);

    if (overlapping == null || overlapping.isEmpty()) {
      disponibilite.setDateDebut(dateDebut);
      disponibilite.setDateFin(dateFin);
      return disponibiliteRepository.save(disponibilite);
    }

    disponibiliteRepository.delete(disponibiliteId);
    return mergeDisponibilites(vehicule, overlapping, dateDebut, dateFin);
  }

  public void deleteDisponibilite(Agent agent, Long disponibiliteId) {
    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'identifiant de la disponibilité ne peut pas être nul.");
    }
    Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId);
    if (disponibilite == null) {
      throw new IllegalArgumentException(
          "Aucune disponibilité trouvée avec l'identifiant " + disponibiliteId);
    }

    Long vehiculeId = disponibilite.getVehicule().getId();
    verifyOwnershipAndGetVehicule(agent, vehiculeId);

    disponibiliteRepository.delete(disponibiliteId);
  }

  public int deleteDisponibiliteRange(Agent agent, Long vehiculeId, LocalDate dateDebut,
      LocalDate dateFin) {

    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("dateDebut et dateFin ne peuvent pas être null.");
    }
    if (dateDebut.isAfter(dateFin)) {
      throw new IllegalArgumentException("dateDebut doit être <= dateFin.");
    }

    List<Disponibilite> candidates =
        disponibiliteRepository.findOverlappingOrAdjacent(vehiculeId, dateDebut, dateFin, null);

    if (candidates == null) {
      candidates = new ArrayList<>();
    }

    List<Disponibilite> overlapping = candidates.stream()
        .filter(d -> !d.getDateFin().isBefore(dateDebut) && !d.getDateDebut().isAfter(dateFin))
        .collect(Collectors.toList());

    if (overlapping.isEmpty()) {
      return 0;
    }

    int affectedCount = 0;
    List<Disponibilite> nouveauxFragments = new ArrayList<>();
    List<Long> idsToDelete = new ArrayList<>();

    for (Disponibilite dispo : overlapping) {
      LocalDate dispoDebut = dispo.getDateDebut();
      LocalDate dispoFin = dispo.getDateFin();

      idsToDelete.add(dispo.getId());
      affectedCount++;

      if (dateDebut.compareTo(dispoDebut) <= 0 && dateFin.compareTo(dispoFin) >= 0) {
        continue;
      }

      if (dateDebut.isAfter(dispoDebut) && dateFin.isBefore(dispoFin)) {
        nouveauxFragments.add(new Disponibilite(vehicule, dispoDebut, dateDebut.minusDays(1)));
        nouveauxFragments.add(new Disponibilite(vehicule, dateFin.plusDays(1), dispoFin));
        continue;
      }

      if (dateDebut.compareTo(dispoDebut) <= 0 && dateFin.isBefore(dispoFin)) {
        nouveauxFragments.add(new Disponibilite(vehicule, dateFin.plusDays(1), dispoFin));
        continue;
      }

      if (dateDebut.isAfter(dispoDebut) && dateFin.compareTo(dispoFin) >= 0) {
        nouveauxFragments.add(new Disponibilite(vehicule, dispoDebut, dateDebut.minusDays(1)));
      }
    }

    disponibiliteRepository.deleteMultiple(idsToDelete);
    for (Disponibilite fragment : nouveauxFragments) {
      disponibiliteRepository.save(fragment);
    }
    return affectedCount;
  }

  // ============================================================================
  // Helpers
  // ============================================================================

  private Vehicule verifyOwnershipAndGetVehicule(Agent agent, Long vehiculeId) {
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit être non null et déjà enregistré.");
    }
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    if (vehicule.getProprietaire() == null
        || vehicule.getProprietaire().getIdU() == null
        || !vehicule.getProprietaire().getIdU().equals(agent.getIdU())) {
      throw new IllegalArgumentException(
          "Ce véhicule n'appartient pas à l'agent spécifié. Seul le propriétaire peut modifier un véhicule.");
    }

    return vehicule;
  }

  private VehiculeDTO convertToDTO(Vehicule vehicule) {
    VehiculeDTO dto = new VehiculeDTO();

    dto.setId(vehicule.getId());
    dto.setType(vehicule.getType());
    dto.setMarque(vehicule.getMarque());
    dto.setModele(vehicule.getModele());
    dto.setCouleur(vehicule.getCouleur());
    dto.setVille(vehicule.getVille());
    dto.setPrixJ(vehicule.getPrixJ());
    dto.setDisponible(vehicule.isDisponible());

    try {
      dto.setNoteMoyenne(vehicule.calculerNote());
    } catch (Exception e) {
      dto.setNoteMoyenne(0.0);
    }

    try {
      List<LocalDate[]> disponibilitesFiltrees =
          filtrerDisponibilitesAvecReservations(vehicule.getDatesDispo(), vehicule.getId());
      dto.setDatesDispo(disponibilitesFiltrees);
    } catch (Exception e) {
      dto.setDatesDispo(new ArrayList<>());
    }

    return dto;
  }

  private List<LocalDate[]> filtrerDisponibilitesAvecReservations(
      List<LocalDate[]> disponibilitesOriginales, Long vehiculeId) {

    List<Object[]> reservations = vehiculeRepository.getDatesLocationsActives(vehiculeId);
    List<LocalDate[]> resultats = new ArrayList<>();

    if (disponibilitesOriginales == null || disponibilitesOriginales.isEmpty()) {
      return resultats;
    }
    if (reservations == null) {
      reservations = new ArrayList<>();
    }

    List<Object[]> reservationsTriees =
        reservations.stream()
            .sorted((r1, r2) -> ((LocalDateTime) r1[0]).toLocalDate()
                .compareTo(((LocalDateTime) r2[0]).toLocalDate()))
            .collect(Collectors.toList());

    for (LocalDate[] dispo : disponibilitesOriginales) {
      if (dispo == null || dispo.length < 2 || dispo[0] == null || dispo[1] == null) {
        continue;
      }

      LocalDate dispoDebut = dispo[0];
      LocalDate dispoFin = dispo[1];

      if (reservationsTriees.isEmpty()) {
        resultats.add(new LocalDate[] {dispoDebut, dispoFin});
        continue;
      }

      LocalDate currentDebut = dispoDebut;

      for (Object[] reservation : reservationsTriees) {
        LocalDate reservationDebut = ((LocalDateTime) reservation[0]).toLocalDate();
        LocalDate reservationFin = ((LocalDateTime) reservation[1]).toLocalDate();

        if (reservationDebut.isAfter(dispoFin) || reservationFin.isBefore(currentDebut)) {
          continue;
        }

        if (currentDebut.isBefore(reservationDebut)) {
          resultats.add(new LocalDate[] {currentDebut, reservationDebut.minusDays(1)});
        }

        if (reservationFin.plusDays(1).isAfter(currentDebut)) {
          currentDebut = reservationFin.plusDays(1);
        }

        if (currentDebut.isAfter(dispoFin)) {
          break;
        }
      }

      if (!currentDebut.isAfter(dispoFin)) {
        resultats.add(new LocalDate[] {currentDebut, dispoFin});
      }
    }

    return resultats;
  }

  private void validateDateRange(LocalDate dateDebut, LocalDate dateFin) {
    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("dateDebut et dateFin ne peuvent pas être null.");
    }
    if (dateDebut.isAfter(dateFin)) {
      throw new IllegalArgumentException("La date de début doit être <= date de fin.");
    }

    LocalDate today = LocalDate.now();
    if (dateDebut.isBefore(today)) {
      throw new IllegalArgumentException(
          "La date de début ne peut pas être dans le passé. Les disponibilités commencent aujourd'hui ou plus tard.");
    }
    if (dateFin.isBefore(today)) {
      throw new IllegalArgumentException("La date de fin ne peut pas être dans le passé.");
    }
  }

  private Disponibilite mergeDisponibilites(Vehicule vehicule,
      List<Disponibilite> overlappingDisponibilites, LocalDate newDateDebut, LocalDate newDateFin) {

    LocalDate mergedDateDebut = newDateDebut;
    for (Disponibilite dispo : overlappingDisponibilites) {
      if (dispo.getDateDebut().isBefore(mergedDateDebut)) {
        mergedDateDebut = dispo.getDateDebut();
      }
    }

    LocalDate mergedDateFin = newDateFin;
    for (Disponibilite dispo : overlappingDisponibilites) {
      if (dispo.getDateFin().isAfter(mergedDateFin)) {
        mergedDateFin = dispo.getDateFin();
      }
    }

    List<Long> idsToDelete = new ArrayList<>();
    for (Disponibilite dispo : overlappingDisponibilites) {
      idsToDelete.add(dispo.getId());
    }
    disponibiliteRepository.deleteMultiple(idsToDelete);

    Disponibilite mergedDisponibilite = new Disponibilite(vehicule, mergedDateDebut, mergedDateFin);
    return disponibiliteRepository.save(mergedDisponibilite);
  }
}
