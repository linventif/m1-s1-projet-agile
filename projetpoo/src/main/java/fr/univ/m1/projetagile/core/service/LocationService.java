package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.VerificationLocation.service.VerificationService;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.interfaces.LieuRestitution;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.parking.entity.Parking;
import fr.univ.m1.projetagile.parrainage.entity.Parrainage;
import fr.univ.m1.projetagile.parrainage.service.CreditService;
import fr.univ.m1.projetagile.parrainage.service.ParrainageService;

/**
 * Service métier pour la gestion des locations de véhicules. Fournit les opérations CRUD et les
 * fonctionnalités métier liées aux locations.
 */
public class LocationService {

  private final LocationRepository locationRepository;
  private final ParrainageService parrainageService;
  private final CreditService creditService;

  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
    this.parrainageService = new ParrainageService();
    this.creditService = new CreditService();
  }

  public LocationService(LocationRepository locationRepository, ParrainageService parrainageService,
      CreditService creditService) {
    this.locationRepository = locationRepository;
    this.parrainageService = parrainageService;
    this.creditService = creditService;
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

      // Vérifier si l'agent a une souscription à l'option Parking
      boolean aOptionParking = agent.getOptionsActives().stream()
          .anyMatch(so -> so.getOption() != null && so.getOption().getId() != null
              && so.getOption().getId().equals(Parking.PARKING_OPTION_ID));

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
    Location locationSauvegardee = locationRepository.save(location);

    // Vérifier et gérer le parrainage du loueur
    gererParrainage(loueur);
    // Vérifier et gérer le parrainage éventuel de l'agent propriétaire du véhicule
    if (vehicule.getProprietaire() != null) {
      gererParrainage(vehicule.getProprietaire());
    }

    return locationSauvegardee;
  }

  /**
   * Gère le parrainage d'un utilisateur (loueur ou agent) lors de la création d'une location. Si
   * l'utilisateur a été parrainé et que le parrainage n'est pas encore activé, active le parrainage
   * et crédite le compte du parrain.
   *
   * @param utilisateur l'utilisateur pour lequel vérifier le parrainage
   */
  private void gererParrainage(Utilisateur utilisateur) {
    if (utilisateur == null || utilisateur.getIdU() == null) {
      return;
    }

    try {
      // Vérifier si l'utilisateur a un parrain
      Utilisateur parrain = parrainageService.getParrainFromParraineId(utilisateur.getIdU());
      if (parrain == null) {
        // L'utilisateur n'a pas de parrain, rien à faire
        return;
      }

      // Récupérer le parrainage
      Parrainage parrainage =
          parrainageService.getParrainageBetween(parrain.getIdU(), utilisateur.getIdU());
      if (parrainage == null) {
        return;
      }

      // Vérifier si le parrainage est déjà activé
      if (parrainage.isActivated()) {
        // Le parrainage est déjà activé, rien à faire
        return;
      }

      // Activer le parrainage
      parrainageService.activerParrainage(parrainage.getId());

      // Créditer le compte du parrain
      crediterParrain(parrain);
    } catch (Exception e) {
      // En cas d'erreur lors de la gestion du parrainage, on ne bloque pas la création de la
      // location
      // On pourrait logger l'erreur ici
      System.err.println("Erreur lors de la gestion du parrainage pour l'utilisateur "
          + utilisateur.getIdU() + ": " + e.getMessage());
    }
  }

  /**
   * Crédite le compte du parrain du montant défini dans Parrainage.MONTANT_CREDIT_PARRAIN. Crée le
   * crédit si nécessaire.
   *
   * @param parrain le parrain à créditer
   */
  private void crediterParrain(Utilisateur parrain) {
    if (parrain == null || parrain.getIdU() == null) {
      return;
    }

    try {
      // Vérifier si le parrain a déjà un crédit
      if (creditService.hasCredit(parrain.getIdU())) {
        // Ajouter le crédit au compte existant
        creditService.ajouterCredit(parrain.getIdU(), Parrainage.MONTANT_CREDIT_PARRAIN);
      } else {
        // Créer un nouveau crédit avec le montant
        creditService.creerSolde(parrain, Parrainage.MONTANT_CREDIT_PARRAIN);
      }
    } catch (Exception e) {
      // En cas d'erreur lors du crédit, on ne bloque pas la création de la location
      // On pourrait logger l'erreur ici
      System.err
          .println("Erreur lors du crédit du parrain " + parrain.getIdU() + ": " + e.getMessage());
    }
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
   * le prix de base - Des frais fixes de 2€ par jour - Une promotion de 10% si le lieu de dépôt est
   * un parking
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
    double prixTotal = prixBase + commissionProportionnelle + fraisFixes;

    // Promotion de 10% si le lieu de dépôt est un parking
    if (location.getLieuDepot() != null && location.getLieuDepot() instanceof Parking) {
      prixTotal = prixTotal * Parking.DISCOUNT_RATE;
    }

    return prixTotal;
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
   * Termine une location en cours. Met à jour la vérification existante avec le kilométrage de fin
   * et la photo, vérifie que tout est correct, puis change le statut de la location à TERMINE et la
   * sauvegarde en base de données.
   *
   * @param location la location à terminer
   * @param kilometrageFin le kilométrage du véhicule à la fin de la location
   * @param photo la photo du véhicule (peut être null)
   * @throws IllegalArgumentException si la location est nulle, si le kilométrage est invalide, ou
   *         si la vérification n'existe pas
   * @throws IllegalStateException si la location ne peut pas être terminée (statut incorrect ou
   *         vérification échouée)
   */
  public void terminer(Location location, Integer kilometrageFin, String photo) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    if (location.getId() == null) {
      throw new IllegalArgumentException("La location doit avoir un identifiant.");
    }
    if (kilometrageFin == null || kilometrageFin < 0) {
      throw new IllegalArgumentException(
          "Le kilométrage de fin doit être un entier positif ou nul.");
    }

    StatutLocation statutActuel = location.getStatut();
    if (statutActuel != StatutLocation.ACCEPTE) {
      throw new IllegalStateException(
          "Terminaison impossible : la location ne peut être terminée que si son statut est ACCEPTE.");
    }

    // Récupérer et mettre à jour la vérification existante
    VerificationRepository verificationRepository = new VerificationRepository();
    VerificationService verificationService =
        new VerificationService(verificationRepository, locationRepository);

    // Vérifier que la vérification existe
    fr.univ.m1.projetagile.VerificationLocation.entity.Verification verification =
        verificationService.getVerificationByLocationId(location.getId());
    if (verification == null) {
      throw new IllegalStateException(
          "Impossible de terminer la location : aucune vérification trouvée pour cette location.");
    }

    try {
      verificationService.verifierFinLocation(verification.getId(), kilometrageFin, photo);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Impossible de terminer la location : la vérification n'a pas pu être mise à jour. "
              + e.getMessage(),
          e);
    }

    // Si la vérification a été mise à jour avec succès, terminer la location
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
