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
  private DisponibiliteRepository disponibiliteRepository;

  public VehiculeService(VehiculeRepository vehiculeRepository) {
    this.vehiculeRepository = vehiculeRepository;
    this.disponibiliteRepository = new DisponibiliteRepository();
  }

  public VehiculeService(VehiculeRepository vehiculeRepository,
      DisponibiliteRepository disponibiliteRepository) {
    this.vehiculeRepository = vehiculeRepository;
    this.disponibiliteRepository = disponibiliteRepository;
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

  /**
   * Récupère un véhicule par son identifiant
   *
   * @param id l'identifiant du véhicule
   * @return le véhicule trouvé ou null si aucun véhicule n'existe avec cet identifiant
   */
  public Vehicule findVehiculeById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }
    return vehiculeRepository.findById(id);
  }

  /**
   * Recherche des véhicules avec des filtres et retourne leurs informations enrichies
   *
   * @param dateDebut date de début de la période souhaitée (optionnel)
   * @param dateFin date de fin de la période souhaitée (optionnel)
   * @param ville ville de recherche (optionnel)
   * @param marque marque du véhicule (optionnel)
   * @param modele modèle du véhicule (optionnel)
   * @param couleur couleur du véhicule (optionnel)
   * @param prixMin prix minimum journalier (optionnel)
   * @param prixMax prix maximum journalier (optionnel)
   * @param type type de véhicule (optionnel)
   * @param hasParkingOption si true, filtre les véhicules dont l'agent a l'option Parking
   *        (optionnel)
   * @return Liste de VehiculeDTO filtrés contenant : - Les propriétés du véhicule - La note moyenne
   *         calculée - Les dates de disponibilités - Le lieu (ville)
   */
  public List<VehiculeDTO> searchVehiculesWithFilters(LocalDate dateDebut, LocalDate dateFin,
      String ville, String marque, String modele, String couleur, Double prixMin, Double prixMax,
      TypeV type, Boolean hasParkingOption) {
    List<Vehicule> vehicules = vehiculeRepository.findWithFilters(dateDebut, dateFin, ville, marque,
        modele, couleur, prixMin, prixMax, type, hasParkingOption);

    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  /**
   * Crée un nouveau véhicule avec validation des données d'entrée
   *
   * @param type le type de véhicule (VOITURE, MOTO, etc.)
   * @param marque la marque du véhicule (ex: Peugeot, Renault)
   * @param modele le modèle du véhicule (ex: 308, Clio)
   * @param couleur la couleur du véhicule
   * @param ville la ville où se trouve le véhicule
   * @param prixJ le prix journalier de location (doit être positif)
   * @param proprietaire l'agent propriétaire du véhicule (doit être enregistré en base)
   * @return le véhicule créé et sauvegardé en base de données
   * @throws IllegalArgumentException si un paramètre est invalide (null, vide, ou négatif)
   */
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
   * Vérifie si un véhicule peut être supprimé (aucune location active ou future)
   */
  private void verifierVehiculeNonLoue(Long vehiculeId) {
    List<Object[]> locationsActives = vehiculeRepository.getDatesLocationsActives(vehiculeId);

    if (locationsActives != null && !locationsActives.isEmpty()) {
      throw new IllegalArgumentException(
          "Impossible de supprimer ce véhicule : il est actuellement loué ou réservé.");
    }
  }

  /**
   * Supprime un véhicule par son identifiant, uniquement s'il n'est pas loué (aucune location en
   * cours ou planifiée).
   *
   * @param vehiculeId l'identifiant du véhicule à supprimer
   * @throws IllegalArgumentException si le véhicule n'existe pas ou s'il est encore loué
   */
  public void deleteVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    // Vérifier qu'aucune location active (non annulée/terminée) n'existe pour ce véhicule
    verifierVehiculeNonLoue(vehiculeId);

    // Supprimer le véhicule
    vehiculeRepository.delete(vehiculeId);
  }

  /**
   * Permet à un agent de supprimer un de ses véhicules
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule à supprimer
   * @throws IllegalArgumentException si le véhicule n'existe pas ou n'appartient pas à l'agent
   */
  public void deleteVehiculeForAgent(Agent agent, Long vehiculeId) {
    // Utiliser la méthode utilitaire pour vérifier la propriété et récupérer le véhicule
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    // Vérifier qu'aucune location active (non annulée/terminée) n'existe pour ce véhicule
    List<Object[]> locationsActives = vehiculeRepository.getDatesLocationsActives(vehiculeId);
    if (locationsActives != null && !locationsActives.isEmpty()) {
      throw new IllegalArgumentException(
          "Impossible de supprimer ce véhicule : des locations sont encore en cours ou planifiées.");
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

    try {
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
      try {
        dto.setNoteMoyenne(vehicule.calculerNote());
      } catch (Exception e) {
        dto.setNoteMoyenne(0.0); // Default value if calculation fails
      }

      // Dates de disponibilités filtrées selon les réservations existantes
      try {
        List<LocalDate[]> disponibilitesFiltrees =
            filtrerDisponibilitesAvecReservations(vehicule.getDatesDispo(), vehicule.getId());
        dto.setDatesDispo(disponibilitesFiltrees);
      } catch (Exception e) {
        // If lazy loading fails, set empty list
        dto.setDatesDispo(new ArrayList<>());
      }

      return dto;
    } catch (Exception e) {
      throw new RuntimeException(
          "Erreur lors de la conversion du véhicule en DTO: " + e.getMessage(), e);
    }
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

  // ========== Gestion des Disponibilités ==========

  /**
   * Crée une nouvelle disponibilité pour un véhicule Si la nouvelle période touche ou chevauche des
   * disponibilités existantes, elles seront automatiquement fusionnées en une seule période
   * continue
   *
   * Exemples de fusion : - Existant: 10-20 oct, Nouveau: 20-30 oct → Résultat: 10-30 oct -
   * Existant: 10-20 oct, Nouveau: 15-25 oct → Résultat: 10-25 oct - Existant: 10-15 oct + 20-25
   * oct, Nouveau: 14-21 oct → Résultat: 10-25 oct
   *
   * @param agent l'agent propriétaire du véhicule
   * @param vehiculeId l'identifiant du véhicule
   * @param dateDebut date de début de la disponibilité
   * @param dateFin date de fin de la disponibilité
   * @return la disponibilité créée ou fusionnée
   * @throws IllegalArgumentException si les validations échouent
   */
  public Disponibilite createDisponibilite(Agent agent, Long vehiculeId, LocalDate dateDebut,
      LocalDate dateFin) {

    // Vérifier que le véhicule existe et appartient à l'agent
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    // Valider les dates
    validateDateRange(dateDebut, dateFin);

    // Chercher les disponibilités qui se touchent ou se chevauchent
    List<Disponibilite> overlapping =
        disponibiliteRepository.findOverlappingOrAdjacent(vehiculeId, dateDebut, dateFin, null);

    if (overlapping.isEmpty()) {
      // Aucun chevauchement : créer simplement une nouvelle disponibilité
      Disponibilite disponibilite = new Disponibilite(vehicule, dateDebut, dateFin);
      return disponibiliteRepository.save(disponibilite);
    } else {
      // Il y a des chevauchements : fusionner toutes les périodes
      return mergeDisponibilites(vehicule, overlapping, dateDebut, dateFin);
    }
  }

  /**
   * Récupère toutes les disponibilités d'un véhicule
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return liste de toutes les disponibilités du véhicule
   */
  public List<Disponibilite> getDisponibilitesByVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    // Vérifier que le véhicule existe
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    return disponibiliteRepository.findByVehiculeId(vehiculeId);
  }

  /**
   * Récupère toutes les disponibilités futures d'un véhicule (à partir d'aujourd'hui)
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return liste des disponibilités futures du véhicule
   */
  public List<Disponibilite> getFutureDisponibilitesByVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    // Vérifier que le véhicule existe
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    return disponibiliteRepository.findFutureByVehiculeId(vehiculeId);
  }

  /**
   * Récupère toutes les disponibilités (tous véhicules confondus)
   *
   * @return liste de toutes les disponibilités
   */
  public List<Disponibilite> getAllDisponibilites() {
    return disponibiliteRepository.findAll();
  }

  /**
   * Récupère une disponibilité par son ID
   *
   * @param disponibiliteId l'identifiant de la disponibilité
   * @return la disponibilité trouvée
   * @throws IllegalArgumentException si la disponibilité n'existe pas
   */
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

  /**
   * Modifie les dates d'une disponibilité existante Si les nouvelles dates touchent ou chevauchent
   * d'autres disponibilités, elles seront automatiquement fusionnées
   *
   * @param agent l'agent propriétaire du véhicule
   * @param disponibiliteId l'identifiant de la disponibilité
   * @param dateDebut nouvelle date de début
   * @param dateFin nouvelle date de fin
   * @return la disponibilité modifiée ou fusionnée
   * @throws IllegalArgumentException si les validations échouent
   */
  public Disponibilite updateDisponibilite(Agent agent, Long disponibiliteId, LocalDate dateDebut,
      LocalDate dateFin) {

    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'identifiant de la disponibilité ne peut pas être nul.");
    }

    // Récupérer la disponibilité
    Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId);
    if (disponibilite == null) {
      throw new IllegalArgumentException(
          "Aucune disponibilité trouvée avec l'identifiant " + disponibiliteId);
    }

    // Vérifier que le véhicule appartient à l'agent
    Long vehiculeId = disponibilite.getVehicule().getId();
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    // Valider les dates
    validateDateRange(dateDebut, dateFin);

    // Chercher les disponibilités qui se touchent ou se chevauchent (en excluant celle-ci)
    List<Disponibilite> overlapping = disponibiliteRepository.findOverlappingOrAdjacent(vehiculeId,
        dateDebut, dateFin, disponibiliteId);

    if (overlapping.isEmpty()) {
      // Aucun chevauchement : simplement mettre à jour les dates
      disponibilite.setDateDebut(dateDebut);
      disponibilite.setDateFin(dateFin);
      return disponibiliteRepository.save(disponibilite);
    } else {
      // Il y a des chevauchements : supprimer cette disponibilité et fusionner avec les autres
      disponibiliteRepository.delete(disponibiliteId);
      return mergeDisponibilites(vehicule, overlapping, dateDebut, dateFin);
    }
  }

  /**
   * Supprime une disponibilité
   *
   * @param agent l'agent propriétaire du véhicule
   * @param disponibiliteId l'identifiant de la disponibilité à supprimer
   * @throws IllegalArgumentException si les validations échouent
   */
  public void deleteDisponibilite(Agent agent, Long disponibiliteId) {

    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'identifiant de la disponibilité ne peut pas être nul.");
    }

    // Récupérer la disponibilité
    Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId);
    if (disponibilite == null) {
      throw new IllegalArgumentException(
          "Aucune disponibilité trouvée avec l'identifiant " + disponibiliteId);
    }

    // Vérifier que le véhicule appartient à l'agent
    Long vehiculeId = disponibilite.getVehicule().getId();
    verifyOwnershipAndGetVehicule(agent, vehiculeId);

    // Supprimer la disponibilité
    disponibiliteRepository.delete(disponibiliteId);
  }

  /**
   * Supprime une plage de dates des disponibilités d'un véhicule. Cette méthode "découpe" les
   * disponibilités existantes en supprimant la plage spécifiée.
   *
   * Exemples: - Disponibilité: 10-20 oct, Supprimer: 13-18 oct → Résultat: 10-12 oct + 19-20 oct -
   * Disponibilité: 10-20 oct, Supprimer: 10-15 oct → Résultat: 16-20 oct - Disponibilité: 10-20
   * oct, Supprimer: 15-20 oct → Résultat: 10-14 oct - Disponibilité: 10-20 oct, Supprimer: 10-20
   * oct → Résultat: (supprimée complètement) - Disponibilité: 10-20 oct, Supprimer: 5-25 oct →
   * Résultat: (supprimée complètement)
   *
   * @param agent l'agent propriétaire du véhicule
   * @param vehiculeId l'identifiant du véhicule
   * @param dateDebut date de début de la plage à supprimer
   * @param dateFin date de fin de la plage à supprimer
   * @return nombre de disponibilités affectées
   * @throws IllegalArgumentException si les validations échouent
   */
  public int deleteDisponibiliteRange(Agent agent, Long vehiculeId, LocalDate dateDebut,
      LocalDate dateFin) {

    // Vérifier que le véhicule existe et appartient à l'agent
    Vehicule vehicule = verifyOwnershipAndGetVehicule(agent, vehiculeId);

    // Valider les dates (sans la vérification du passé car on peut vouloir supprimer des dates
    // passées)
    if (dateDebut == null) {
      throw new IllegalArgumentException("La date de début ne peut pas être nulle.");
    }
    if (dateFin == null) {
      throw new IllegalArgumentException("La date de fin ne peut pas être nulle.");
    }
    if (dateDebut.isAfter(dateFin)) {
      throw new IllegalArgumentException(
          "La date de début doit être antérieure ou égale à la date de fin.");
    }

    // Trouver toutes les disponibilités candidates (chevauchantes ou adjacentes) puis
    // filtrer pour ne conserver que les plages qui chevauchent réellement la plage à supprimer
    List<Disponibilite> candidates =
        disponibiliteRepository.findOverlappingOrAdjacent(vehiculeId, dateDebut, dateFin, null);
    List<Disponibilite> overlapping = candidates.stream()
        .filter(d -> !d.getDateFin().isBefore(dateDebut) && !d.getDateDebut().isAfter(dateFin))
        .collect(Collectors.toList());

    if (overlapping.isEmpty()) {
      // Aucune disponibilité à supprimer dans cette plage
      return 0;
    }

    int affectedCount = 0;
    List<Disponibilite> nouveauxFragments = new ArrayList<>();
    List<Long> idsToDelete = new ArrayList<>();

    for (Disponibilite dispo : overlapping) {
      LocalDate dispoDebut = dispo.getDateDebut();
      LocalDate dispoFin = dispo.getDateFin();

      // Marquer cette disponibilité pour suppression
      idsToDelete.add(dispo.getId());
      affectedCount++;

      // Cas 1: La plage à supprimer englobe complètement la disponibilité
      // Disponibilité: |====|
      // Suppression: |========|
      // Résultat: (rien à créer)
      if (dateDebut.compareTo(dispoDebut) <= 0 && dateFin.compareTo(dispoFin) >= 0) {
        // Disponibilité complètement supprimée, ne rien créer
        continue;
      }

      // Cas 2: La plage à supprimer est au milieu (crée deux fragments)
      // Disponibilité: |==========|
      // Suppression: |==|
      // Résultat: |==| |==|
      if (dateDebut.isAfter(dispoDebut) && dateFin.isBefore(dispoFin)) {
        // Fragment avant la suppression
        Disponibilite fragment1 = new Disponibilite(vehicule, dispoDebut, dateDebut.minusDays(1));
        nouveauxFragments.add(fragment1);

        // Fragment après la suppression
        Disponibilite fragment2 = new Disponibilite(vehicule, dateFin.plusDays(1), dispoFin);
        nouveauxFragments.add(fragment2);
        continue;
      }

      // Cas 3: La plage à supprimer commence au début
      // Disponibilité: |==========|
      // Suppression: |====|
      // Résultat: |====|
      if (dateDebut.compareTo(dispoDebut) <= 0 && dateFin.isBefore(dispoFin)) {
        // Garder la partie après la suppression
        Disponibilite fragment = new Disponibilite(vehicule, dateFin.plusDays(1), dispoFin);
        nouveauxFragments.add(fragment);
        continue;
      }

      // Cas 4: La plage à supprimer termine à la fin
      // Disponibilité: |==========|
      // Suppression: |====|
      // Résultat: |====|
      if (dateDebut.isAfter(dispoDebut) && dateFin.compareTo(dispoFin) >= 0) {
        // Garder la partie avant la suppression
        Disponibilite fragment = new Disponibilite(vehicule, dispoDebut, dateDebut.minusDays(1));
        nouveauxFragments.add(fragment);
        continue;
      }
    }

    // Supprimer les anciennes disponibilités
    disponibiliteRepository.deleteMultiple(idsToDelete);

    // Créer les nouveaux fragments
    for (Disponibilite fragment : nouveauxFragments) {
      disponibiliteRepository.save(fragment);
    }

    return affectedCount;
  }

  /**
   * Valide une plage de dates pour les disponibilités
   *
   * @param dateDebut date de début
   * @param dateFin date de fin
   * @throws IllegalArgumentException si les dates sont invalides
   */
  private void validateDateRange(LocalDate dateDebut, LocalDate dateFin) {
    if (dateDebut == null) {
      throw new IllegalArgumentException("La date de début ne peut pas être nulle.");
    }
    if (dateFin == null) {
      throw new IllegalArgumentException("La date de fin ne peut pas être nulle.");
    }
    if (dateDebut.isAfter(dateFin)) {
      throw new IllegalArgumentException(
          "La date de début doit être antérieure ou égale à la date de fin.");
    }
    LocalDate today = LocalDate.now();
    if (dateDebut.isBefore(today)) {
      throw new IllegalArgumentException(
          "La date de début ne peut pas être dans le passé. Les disponibilités doivent commencer aujourd'hui ou dans le futur.");
    }
    if (dateFin.isBefore(today)) {
      throw new IllegalArgumentException(
          "La date de fin ne peut pas être dans le passé. Les disponibilités passées ne sont pas utiles.");
    }
  }

  /**
   * Fusionne plusieurs disponibilités qui se chevauchent ou se touchent en une seule période
   * continue
   *
   * @param vehicule le véhicule concerné
   * @param overlappingDisponibilites liste des disponibilités existantes qui se chevauchent
   * @param newDateDebut date de début de la nouvelle période
   * @param newDateFin date de fin de la nouvelle période
   * @return la disponibilité fusionnée
   */
  private Disponibilite mergeDisponibilites(Vehicule vehicule,
      List<Disponibilite> overlappingDisponibilites, LocalDate newDateDebut, LocalDate newDateFin) {

    // Calculer la date de début minimale (la plus ancienne)
    LocalDate mergedDateDebut = newDateDebut;
    for (Disponibilite dispo : overlappingDisponibilites) {
      if (dispo.getDateDebut().isBefore(mergedDateDebut)) {
        mergedDateDebut = dispo.getDateDebut();
      }
    }

    // Calculer la date de fin maximale (la plus récente)
    LocalDate mergedDateFin = newDateFin;
    for (Disponibilite dispo : overlappingDisponibilites) {
      if (dispo.getDateFin().isAfter(mergedDateFin)) {
        mergedDateFin = dispo.getDateFin();
      }
    }

    // Supprimer toutes les anciennes disponibilités qui se chevauchent
    List<Long> idsToDelete = new ArrayList<>();
    for (Disponibilite dispo : overlappingDisponibilites) {
      idsToDelete.add(dispo.getId());
    }
    disponibiliteRepository.deleteMultiple(idsToDelete);

    // Créer une nouvelle disponibilité fusionnée
    Disponibilite mergedDisponibilite = new Disponibilite(vehicule, mergedDateDebut, mergedDateFin);
    return disponibiliteRepository.save(mergedDisponibilite);
  }

  public Vehicule findById(Long id) {
    if (id == null) {
      return null;
    }
    return vehiculeRepository.findById(id);
  }

}
