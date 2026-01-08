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

  /**
   * Récupère tous les véhicules appartenant à un agent spécifique
   *
   * @param agent l'agent propriétaire des véhicules
   * @return Liste de VehiculeDTO des véhicules de cet agent avec leurs informations enrichies
   */
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
    if (proprietaire.getIdU() == null) {
      throw new IllegalArgumentException(
          "Le propriétaire doit être déjà enregistré (identifiant manquant).");
    }

    Vehicule vehicule = new Vehicule(type, marque, modele, couleur, ville, prixJ, proprietaire);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Permet à un agent de supprimer un de ses véhicules
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule à supprimer
   * @throws IllegalArgumentException si le véhicule n'existe pas ou n'appartient pas à l'agent
   */
  public void deleteVehiculeForAgent(Agent agent, Long vehiculeId) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit être déjà enregistré.");
    }
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    // Récupérer le véhicule pour vérifier qu'il existe
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    // Vérifier que le véhicule appartient bien à l'agent
    if (vehicule.getProprietaire() == null
        || !vehicule.getProprietaire().getIdU().equals(agent.getIdU())) {
      throw new IllegalArgumentException(
          "Ce véhicule n'appartient pas à l'agent spécifié. Seul le propriétaire peut supprimer un véhicule.");
    }

    // Supprimer le véhicule
    vehiculeRepository.delete(vehiculeId);
  }

  /**
   * Modifie le type d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouveauType le nouveau type de véhicule
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeType(Agent agent, Long vehiculeId, TypeV nouveauType) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouveauType == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être nul.");
    }

    vehicule.setType(nouveauType);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie la marque d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouvelleMarque la nouvelle marque
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeMarque(Agent agent, Long vehiculeId, String nouvelleMarque) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouvelleMarque == null || nouvelleMarque.trim().isEmpty()) {
      throw new IllegalArgumentException("La marque ne peut pas être vide.");
    }

    vehicule.setMarque(nouvelleMarque);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie le modèle d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouveauModele le nouveau modèle
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeModele(Agent agent, Long vehiculeId, String nouveauModele) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouveauModele == null || nouveauModele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle ne peut pas être vide.");
    }

    vehicule.setModele(nouveauModele);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie la couleur d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouvelleCouleur la nouvelle couleur
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeCouleur(Agent agent, Long vehiculeId, String nouvelleCouleur) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouvelleCouleur == null || nouvelleCouleur.trim().isEmpty()) {
      throw new IllegalArgumentException("La couleur ne peut pas être vide.");
    }

    vehicule.setCouleur(nouvelleCouleur);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie la ville d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouvelleVille la nouvelle ville
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeVille(Agent agent, Long vehiculeId, String nouvelleVille) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouvelleVille == null || nouvelleVille.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville ne peut pas être vide.");
    }

    vehicule.setVille(nouvelleVille);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie le prix journalier d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouveauPrixJ le nouveau prix journalier
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculePrixJ(Agent agent, Long vehiculeId, Double nouveauPrixJ) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (nouveauPrixJ == null || nouveauPrixJ.doubleValue() <= 0.0) {
      throw new IllegalArgumentException("Le prix journalier doit être strictement positif.");
    }

    vehicule.setPrixJ(nouveauPrixJ);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Modifie la disponibilité d'un véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param disponible true si le véhicule est disponible, false sinon
   * @return le véhicule modifié
   */
  public Vehicule updateVehiculeDisponibilite(Agent agent, Long vehiculeId, boolean disponible) {
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    vehicule.setDisponible(disponible);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Méthode utilitaire pour vérifier la propriété et récupérer le véhicule
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @return le véhicule si les vérifications sont valides
   * @throws IllegalArgumentException si les validations échouent
   */
  private Vehicule verifyOwnershipAndGetVehicule(Agent agent, Long vehiculeId) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit être déjà enregistré.");
    }
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    // Vérifier que le véhicule appartient bien à l'agent
    if (vehicule.getProprietaire() == null
        || !vehicule.getProprietaire().getIdU().equals(agent.getIdU())) {
      throw new IllegalArgumentException(
          "Ce véhicule n'appartient pas à l'agent spécifié. Seul le propriétaire peut modifier un véhicule.");
    }

    return vehicule;
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
