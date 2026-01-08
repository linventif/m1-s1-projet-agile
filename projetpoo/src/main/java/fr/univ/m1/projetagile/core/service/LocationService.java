package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.NoteA;
import fr.univ.m1.projetagile.core.entity.NoteV;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;

/**
 * Service métier pour la gestion des locations de véhicules. Fournit les opérations CRUD et les
 * fonctionnalités métier liées aux locations.
 */
public class LocationService {

  private final LocationRepository locationRepository;

  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  /**
   * Crée et enregistre une nouvelle location après validation métier basique.
   *
   * @param dateDebut date et heure de début souhaitées
   * @param dateFin date et heure de fin souhaitées
   * @param lieuDepot lieu de dépôt du véhicule
   * @param vehicule véhicule concerné (doit être déjà persisté)
   * @param loueur loueur effectuant la réservation
   * @return la location sauvegardée
   */
  public Location creerLocation(LocalDateTime dateDebut, LocalDateTime dateFin, String lieuDepot,
      Vehicule vehicule, Loueur loueur) {

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
   * Calcule le prix total d'une location en fonction de la durée et du véhicule. Le prix comprend :
   * - Le prix de base (prix par jour × nombre de jours)
   * - Une commission proportionnelle de 10% sur le prix de base
   * - Des frais fixes de 2€ par jour
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
    return prixBase + commissionProportionnelle + fraisFixes;
  }

  /**
   * Enregistre une note pour un véhicule suite à une location. Permet aux utilisateurs de noter
   * leur satisfaction concernant le véhicule loué.
   *
   * @param note la note du véhicule à enregistrer
   */
  public void noterVehicule(NoteV note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour le véhicule
  }

  /**
   * Enregistre une note pour un agent suite à une location. Permet aux utilisateurs de noter leur
   * satisfaction concernant le service fourni par l'agent.
   *
   * @param note la note de l'agent à enregistrer
   */
  public void noterAgent(NoteA note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour l'agent
  }

  /**
   * Génère un document PDF contenant les détails de la location. Le PDF inclut les informations de
   * la location, du véhicule, du loueur, ainsi que les conditions générales et le contrat de
   * location.
   */
  public void genererPDF() {
    // Génère un PDF pour la location
    // TODO: Implémenter la génération de PDF
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
   * Termine une location en cours. Change le statut de la location à TERMINE et la sauvegarde en
   * base de données.
   *
   * @param location la location à terminer
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
}
