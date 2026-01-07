package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.enums.TypeV;

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
    if (proprietaire == null) {
      throw new IllegalArgumentException("Le propriétaire du véhicule ne peut pas être nul.");
    }
    Vehicule vehicule = new Vehicule(type, marque, modele, couleur, ville, prixJ, proprietaire);
    return vehiculeRepository.save(vehicule);
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
    dto.setId(vehicule.getId());
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

    // Dates de disponibilités filtrées selon les réservations existantes
    List<LocalDate[]> disponibilitesFiltrees =
        filtrerDisponibilitesAvecReservations(vehicule.getDatesDispo(), vehicule.getId());
    dto.setDatesDispo(disponibilitesFiltrees);

    return dto;
  }

  /**
   * Filtre les disponibilités en tenant compte des réservations existantes Coupe les périodes de
   * disponibilité où il y a déjà des réservations
   *
   * @param disponibilitesOriginales Liste des disponibilités brutes du véhicule
   * @param vehiculeId ID du véhicule pour récupérer les réservations
   * @return Liste des disponibilités filtrées (paires date début / date fin)
   */
  private List<LocalDate[]> filtrerDisponibilitesAvecReservations(
      List<LocalDate[]> disponibilitesOriginales, Long vehiculeId) {

    // Récupérer les dates de réservations actives
    List<Object[]> reservations = vehiculeRepository.getDatesLocationsActives(vehiculeId);

    List<LocalDate[]> resultats = new ArrayList<>();

    // Trier les réservations par date de début pour faciliter le traitement
    List<Object[]> reservationsTriees =
        reservations.stream().sorted((r1, r2) -> ((LocalDateTime) r1[0]).toLocalDate()
            .compareTo(((LocalDateTime) r2[0]).toLocalDate())).collect(Collectors.toList());

    // Pour chaque disponibilité originale
    for (LocalDate[] dispo : disponibilitesOriginales) {
      if (dispo == null || dispo.length < 2 || dispo[0] == null || dispo[1] == null) {
        continue;
      }

      LocalDate dispoDebut = dispo[0];
      LocalDate dispoFin = dispo[1];

      // Si pas de réservations, garder la disponibilité entière
      if (reservations.isEmpty()) {
        resultats.add(new LocalDate[] {dispoDebut, dispoFin});
        continue;
      }

      LocalDate currentDebut = dispoDebut;

      for (Object[] reservation : reservationsTriees) {
        LocalDate reservationDebut = ((LocalDateTime) reservation[0]).toLocalDate();
        LocalDate reservationFin = ((LocalDateTime) reservation[1]).toLocalDate();

        // Si la réservation commence après la fin de la disponibilité courante, ignorer
        if (reservationDebut.isAfter(dispoFin) || reservationFin.isBefore(currentDebut)) {
          continue;
        }

        // Si il y a une période disponible avant la réservation
        if (currentDebut.isBefore(reservationDebut)) {
          resultats.add(new LocalDate[] {currentDebut, reservationDebut.minusDays(1)});
        }

        // Avancer le curseur après la réservation
        if (reservationFin.plusDays(1).isAfter(currentDebut)) {
          currentDebut = reservationFin.plusDays(1);
        }

        // Si on dépasse la fin de la disponibilité, arrêter
        if (currentDebut.isAfter(dispoFin)) {
          break;
        }
      }

      // Ajouter la période restante après la dernière réservation
      if (currentDebut.isBefore(dispoFin) || currentDebut.isEqual(dispoFin)) {
        resultats.add(new LocalDate[] {currentDebut, dispoFin});
      }
    }

    return resultats;
  }

}
