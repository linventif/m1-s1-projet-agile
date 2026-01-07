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
   * Enregistre ou met à jour une location.
   *
   * @param location la location à sauvegarder
   * @return la location persistée
   */
  public Location sauvegarder(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    return locationRepository.save(location);
  }

  /**
   * Supprime une location existante par son identifiant.
   *
   * @param locationId identifiant de la location
   */
  public void supprimer(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location est obligatoire.");
    }
    locationRepository.delete(locationId);
  }

  // Méthodes selon UML
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

  public void noterVehicule(NoteV note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour le véhicule
  }

  public void noterAgent(NoteA note) {
    // Méthode selon UML - à implémenter selon la logique métier
    // TODO: Enregistrer la note pour l'agent
  }

  public void genererPDF() {
    // Génère un PDF pour la location
    // TODO: Implémenter la génération de PDF
  }

  public void annuler(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    location.setStatut(StatutLocation.ANNULE);
    locationRepository.save(location);
  }

  public void terminer(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("La location ne peut pas être nulle.");
    }
    location.setStatut(StatutLocation.TERMINE);
    locationRepository.save(location);
  }
}
