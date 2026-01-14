package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.VerificationLocation.service.VerificationService;
import fr.univ.m1.projetagile.assurance.entity.Assurance;
import fr.univ.m1.projetagile.assurance.service.AssuranceService;
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
  private final AssuranceService assuranceService;

  // ==================== #100 : règles commission ====================
  private static final double COMMISSION_NORMALE = 0.10; // 10%
  private static final double COMMISSION_LLD = 0.05; // 5% (rabais LLD)

  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
    this.parrainageService = new ParrainageService();
    this.creditService = new CreditService();
    this.assuranceService = new AssuranceService();
  }

  public LocationService(LocationRepository locationRepository, ParrainageService parrainageService,
      CreditService creditService) {
    this.locationRepository = locationRepository;
    this.parrainageService = parrainageService;
    this.creditService = creditService;
    this.assuranceService = new AssuranceService();
  }

  public LocationService(LocationRepository locationRepository, ParrainageService parrainageService,
      CreditService creditService, AssuranceService assuranceService) {
    this.locationRepository = locationRepository;
    this.parrainageService = parrainageService;
    this.creditService = creditService;
    this.assuranceService = assuranceService;
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
    if (!dateFin.isAfter(dateDebut)) {
      throw new IllegalArgumentException(
          "La date de fin doit être strictement postérieure à la date de début.");
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

    // Vérifier si le propriétaire a l'option "Accepter les contrats manuellement"
    Agent proprietaire = vehicule.getProprietaire();
    if (proprietaire == null) {
      throw new IllegalStateException("Le véhicule n'a pas de propriétaire associé.");
    }

    boolean acceptationManuelle = proprietaire.getOptionsActives().stream()
        .anyMatch(so -> so.getOption() != null && so.getOption().getNomOption() != null
            && so.getOption().getNomOption().equals("Accepter les contrats manuellement"));

    // Si le propriétaire a l'option, le statut reste EN_ATTENTE, sinon on l'accepte automatiquement
    if (!acceptationManuelle) {
      location.setStatut(StatutLocation.ACCEPTE);
    }

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
   * Crée et enregistre une nouvelle location avec souscription d'assurance. Cette méthode utilise
   * la méthode creerLocation existante et crée également un objet SouscriptionAssurance lié à la
   * location via AssuranceService.
   *
   * @param dateDebut date et heure de début souhaitées
   * @param dateFin date et heure de fin souhaitées
   * @param lieuDepot lieu de dépôt du véhicule (facultatif, peut être null)
   * @param vehicule véhicule concerné (doit être déjà persisté)
   * @param loueur loueur effectuant la réservation
   * @param assurance l'assurance à souscrire pour cette location
   * @param options liste des options d'assurance (peut être null ou vide)
   * @return la location sauvegardée avec son assurance souscrite
   * @throws IllegalArgumentException si l'assurance est null
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin,
      LieuRestitution lieuDepot, Vehicule vehicule, Loueur loueur, Assurance assurance,
      List<String> options) {

    if (assurance == null) {
      throw new IllegalArgumentException("L'assurance doit être spécifiée.");
    }

    // Créer la location en utilisant la méthode existante
    Location location = creerLocation(dateDebut, dateFin, lieuDepot, vehicule, loueur);

    // Créer et persister la souscription d'assurance via AssuranceService
    assuranceService.souscrire(location, assurance, options);

    return location;
  }

  /**
   * Calcule le prix total d'une location en fonction de la durée et du véhicule. Le prix comprend :
   * - Le prix de base (prix par jour x nombre de jours) - Une commission proportionnelle de 10% sur
   * le prix de base (ou 5% si location de longue durée) - Des frais fixes de 2 EUR par jour - Une
   * promotion de 10% si le lieu de dépôt est un parking
   *
   * le prix de base - Des frais fixes de 2 EUR par jour
   *
   * @param location la location pour laquelle calculer le prix
   * @return le prix total de la location
   */
  public double getPrixLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }

    // Use the centralized method in Location
    int nombreJours = location.getNombreJours();

    // Prix de base
    double prixBase = location.getVehicule().getPrixJ() * nombreJours;

    // Reduced commission if long-term rental
    double tauxCommission = location.estLongueDuree() ? COMMISSION_LLD : COMMISSION_NORMALE;
    double commissionProportionnelle = prixBase * tauxCommission;

    // Frais fixes
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
   * Permet à un agent d'accepter manuellement une location en attente.
   *
   * @param locationId l'identifiant de la location à accepter
   * @param agent l'agent qui accepte la location
   * @throws IllegalArgumentException si l'identifiant de la location ou l'agent est null
   * @throws IllegalStateException si la location n'existe pas, si l'agent n'est pas le propriétaire
   *         du véhicule, si la location n'est pas en attente d'acceptation, ou si le délai
   *         d'acceptation a expiré
   */
  public void accepterLocationManuellement(Long locationId, Agent agent) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit être spécifié et avoir un identifiant.");
    }

    Location location = locationRepository.findById(locationId);
    if (location == null) {
      throw new IllegalStateException("Aucune location trouvée avec l'identifiant " + locationId);
    }

    // Vérifier que l'agent est bien le propriétaire du véhicule
    Agent proprietaire = location.getVehicule().getProprietaire();
    if (proprietaire == null || !proprietaire.getIdU().equals(agent.getIdU())) {
      throw new IllegalStateException(
          "Seul le propriétaire du véhicule peut accepter cette location.");
    }

    // Vérifier que la location est en attente d'acceptation
    if (location.getStatut() != StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT) {
      throw new IllegalStateException(
          "La location ne peut être acceptée que si son statut est EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT. "
              + "Statut actuel : " + location.getStatut());
    }

    // Vérifier que le délai d'acceptation n'a pas expiré
    if (location.delaiAcceptationExpire()) {
      // Le délai a expiré, annuler automatiquement la location
      location.setStatut(StatutLocation.ANNULE);
      locationRepository.save(location);
      throw new IllegalStateException(
          "Le délai d'acceptation de 6 heures a expiré. La location a été automatiquement annulée.");
    }

    // Accepter la location
    location.setStatut(StatutLocation.ACCEPTE);
    locationRepository.save(location);
  }

  /**
   * Permet à un agent de refuser manuellement une location en attente. Si le délai d'acceptation a
   * expiré, la location est automatiquement annulée.
   *
   * @param locationId l'identifiant de la location à refuser
   * @param agent l'agent qui refuse la location
   * @throws IllegalArgumentException si l'identifiant de la location ou l'agent est null
   * @throws IllegalStateException si la location n'existe pas, si l'agent n'est pas le propriétaire
   *         du véhicule, ou si la location n'est pas en attente d'acceptation
   */
  public void refuserLocationManuellement(Long locationId, Agent agent) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("L'agent doit être spécifié et avoir un identifiant.");
    }

    Location location = locationRepository.findById(locationId);
    if (location == null) {
      throw new IllegalStateException("Aucune location trouvée avec l'identifiant " + locationId);
    }

    // Vérifier que l'agent est bien le propriétaire du véhicule
    Agent proprietaire = location.getVehicule().getProprietaire();
    if (proprietaire == null || !proprietaire.getIdU().equals(agent.getIdU())) {
      throw new IllegalStateException(
          "Seul le propriétaire du véhicule peut refuser cette location.");
    }

    // Vérifier que la location est en attente d'acceptation
    if (location.getStatut() != StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT) {
      throw new IllegalStateException(
          "La location ne peut être refusée que si son statut est EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT. "
              + "Statut actuel : " + location.getStatut());
    }

    // Si le délai a expiré, annuler automatiquement (même comportement que
    // accepterLocationManuellement)
    if (location.delaiAcceptationExpire()) {
      location.setStatut(StatutLocation.ANNULE);
      locationRepository.save(location);
      throw new IllegalStateException(
          "Le délai d'acceptation de 6 heures a expiré. La location a été automatiquement annulée.");
    }

    // Refuser la location (annuler)
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
   * Récupère une location par son identifiant
   *
   * @param id l'identifiant de la location
   * @return la location correspondante, ou null si la location n'existe pas
   */
  public Location findLocationById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }

    Location location = locationRepository.findById(id);

    if (location == null) {
      return null;
    }

    return location;
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

  /**
   * Récupère toutes les locations actuelles (non terminées) pour un véhicule donné.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des LocationDTO non terminées pour ce véhicule
   * @throws IllegalArgumentException si l'identifiant du véhicule est null
   */
  public List<LocationDTO> getCurrentLocationsForVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    // Récupérer les locations actuelles depuis le repository avec eager loading
    List<Location> locations = locationRepository.getCurrentLocationsByVehiculeId(vehiculeId);

    List<LocationDTO> currentLocations = new ArrayList<>();

    for (Location location : locations) {
      LocationDTO locationDTO = convertLocationToDTO(location);
      currentLocations.add(locationDTO);
    }

    return currentLocations;
  }

  /**
   * Récupère toutes les locations terminées (historique) pour un véhicule donné.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des LocationDTO terminées pour ce véhicule
   * @throws IllegalArgumentException si l'identifiant du véhicule est null
   */
  public List<LocationDTO> getPreviousLocationsForVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'identifiant du véhicule ne peut pas être nul.");
    }

    // Récupérer les locations terminées depuis le repository avec eager loading
    List<Location> locations = locationRepository.getPreviousLocationsByVehiculeId(vehiculeId);

    List<LocationDTO> previousLocations = new ArrayList<>();

    for (Location location : locations) {
      LocationDTO locationDTO = convertLocationToDTO(location);
      previousLocations.add(locationDTO);
    }

    return previousLocations;
  }

  /**
   * Récupère toutes les locations en attente d'acceptation pour un agent donné. Ces locations
   * concernent les véhicules dont l'agent est propriétaire. Les locations dont le délai
   * d'acceptation a expiré sont automatiquement annulées.
   *
   * @param agentId l'identifiant de l'agent
   * @return la liste des LocationDTO en attente d'acceptation pour cet agent
   * @throws IllegalArgumentException si l'identifiant de l'agent est null
   */
  public List<LocationDTO> getLocationsPendingAcceptanceForAgent(Long agentId) {
    if (agentId == null) {
      throw new IllegalArgumentException("L'identifiant de l'agent ne peut pas être nul.");
    }

    // Récupérer les locations en attente depuis le repository
    List<Location> locations = locationRepository.findPendingLocationsByAgentId(agentId);

    List<LocationDTO> pendingLocations = new ArrayList<>();

    for (Location location : locations) {
      // Vérifier et annuler automatiquement si le délai a expiré
      if (location.delaiAcceptationExpire()) {
        location.setStatut(StatutLocation.ANNULE);
        locationRepository.save(location);
        // Ne pas inclure cette location dans le résultat
        continue;
      }

      LocationDTO locationDTO = convertLocationToDTO(location);
      pendingLocations.add(locationDTO);
    }

    return pendingLocations;
  }

  /**
   * Annule automatiquement toutes les locations en attente d'acceptation dont le délai de 6 heures
   * a expiré. Cette méthode peut être appelée périodiquement pour nettoyer les locations expirées.
   *
   * @return le nombre de locations automatiquement annulées
   */
  public int annulerLocationsExpirees() {
    List<Location> locationsPendantes = locationRepository.findAllPendingLocations();
    int nombreAnnulations = 0;

    for (Location location : locationsPendantes) {
      if (location.delaiAcceptationExpire()) {
        location.setStatut(StatutLocation.ANNULE);
        locationRepository.save(location);
        nombreAnnulations++;
      }
    }

    return nombreAnnulations;
  }

  /**
   * Vérifie si une location spécifique a expiré et l'annule automatiquement si c'est le cas.
   *
   * @param locationId l'identifiant de la location à vérifier
   * @return true si la location a été annulée car expirée, false sinon
   * @throws IllegalArgumentException si l'identifiant de la location est null
   */
  public boolean verifierEtAnnulerSiExpiree(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location ne peut pas être nul.");
    }

    Location location = locationRepository.findById(locationId);
    if (location == null) {
      return false;
    }

    // Vérifier seulement les locations en attente
    if (location.getStatut() != StatutLocation.EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT) {
      return false;
    }

    if (location.delaiAcceptationExpire()) {
      location.setStatut(StatutLocation.ANNULE);
      locationRepository.save(location);
      return true;
    }

    return false;
  }
}
