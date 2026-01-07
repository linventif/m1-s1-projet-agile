package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.EntityManager;

/**
 * Service métier responsable de la gestion des véhicules.
 * <p>
 * Cette classe constitue la couche de service autour du {@link VehiculeRepository} et des entités
 * {@link Vehicule}. Elle fournit des opérations pour :
 * <ul>
 * <li>Créer de nouveaux véhicules à partir des informations fournies par les couches supérieures
 * (par exemple contrôleurs ou interface utilisateur) ;</li>
 * <li>Récupérer la liste des véhicules et les exposer sous forme de {@link VehiculeDTO} enrichis,
 * incluant les propriétés métier, la note moyenne et les disponibilités ;</li>
 * <li>Filtrer et transformer les disponibilités pour tenir compte des réservations existantes.</li>
 * </ul>
 * L'objectif est d'encapsuler la logique métier liée aux véhicules et de séparer les préoccupations
 * entre la persistance, la logique métier et la présentation.
 */
public class VehiculeService {

  private VehiculeRepository vehiculeRepository;

  public VehiculeService(VehiculeRepository vehiculeRepository) {
    this.vehiculeRepository = vehiculeRepository;
  }

  /**
   * Récupère tout les véhicules avec leurs informations enrichies
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
    Agent proprietaireExistant = verifierProprietaire(proprietaire);
    Vehicule vehicule =
        new Vehicule(type, marque, modele, couleur, ville, prixJ, proprietaireExistant);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Vérifie que le propriétaire fourni correspond à un Agent existant en base.
   *
   * @param proprietaire Agent supposément déjà persisté
   * @return l'Agent rechargé depuis la base pour garantir son existence
   */
  private Agent verifierProprietaire(Agent proprietaire) {
    if (proprietaire == null) {
      throw new IllegalArgumentException("Le propriétaire du véhicule ne peut pas être nul.");
    }
    if (proprietaire.getIdU() == null) {
      throw new IllegalArgumentException(
          "Le propriétaire doit être déjà enregistré (identifiant manquant).");
    }

    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      Agent proprietaireEnBase = em.find(Agent.class, proprietaire.getIdU());
      if (proprietaireEnBase == null) {
        throw new IllegalArgumentException(
            "Le propriétaire spécifié n'existe pas dans la base de données.");
      }
      return proprietaireEnBase;
    } finally {
      em.close();
    }
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
