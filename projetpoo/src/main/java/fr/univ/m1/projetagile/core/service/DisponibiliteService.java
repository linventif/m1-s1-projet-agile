package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.DisponibiliteRepository;

/**
 * Service pour gérer les disponibilités des véhicules.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus du repository pour gérer les disponibilités. Il
 * s'occupe de la validation des périodes, de leur sauvegarde automatique et de la récupération des
 * disponibilités.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Création de disponibilités avec validation automatique</li>
 * <li>Sauvegarde automatique en base de données</li>
 * <li>Récupération des disponibilités par véhicule</li>
 * <li>Vérification de la disponibilité d'un véhicule sur une période</li>
 * <li>Suppression de disponibilités</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * DisponibiliteService service = new DisponibiliteService();
 *
 * // Créer une disponibilité (validation et sauvegarde automatiques)
 * Disponibilite dispo =
 *     service.creerDisponibilite(vehicule, LocalDate.now(), LocalDate.now().plusMonths(6));
 *
 * // Récupérer toutes les disponibilités d'un véhicule
 * List<Disponibilite> disponibilites = service.getDisponibilitesVehicule(vehicule);
 *
 * // Vérifier si un véhicule est disponible
 * boolean estDispo = service.estDisponible(vehicule, dateDebut, dateFin);
 * }</pre>
 *
 * @see Disponibilite
 * @see DisponibiliteRepository
 * @see fr.univ.m1.projetagile.core.entity.Vehicule
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
public class DisponibiliteService {

  private final DisponibiliteRepository disponibiliteRepository;

  /**
   * Constructeur par défaut. Initialise le repository de disponibilités.
   */
  public DisponibiliteService() {
    this.disponibiliteRepository = new DisponibiliteRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param disponibiliteRepository le repository de disponibilités à utiliser
   */
  public DisponibiliteService(DisponibiliteRepository disponibiliteRepository) {
    this.disponibiliteRepository = disponibiliteRepository;
  }

  /**
   * Crée une nouvelle disponibilité pour un véhicule avec validation et sauvegarde automatiques.
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Vérifie que le véhicule et les dates sont valides</li>
   * <li>Crée la disponibilité</li>
   * <li>Sauvegarde la disponibilité en base de données</li>
   * <li>Retourne la disponibilité sauvegardée avec son ID généré</li>
   * </ol>
   *
   * @param vehicule le véhicule concerné
   * @param dateDebut la date de début de disponibilité
   * @param dateFin la date de fin de disponibilité
   * @return la disponibilité sauvegardée avec son ID généré
   * @throws IllegalArgumentException si le véhicule est null
   * @throws IllegalArgumentException si les dates sont invalides
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Disponibilite creerDisponibilite(Vehicule vehicule, LocalDate dateDebut,
      LocalDate dateFin) {
    // Validation des paramètres
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }
    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("Les dates ne peuvent pas être null");
    }
    if (dateFin.isBefore(dateDebut)) {
      throw new IllegalArgumentException("La date de fin doit être après la date de début");
    }

    // Création de la disponibilité
    Disponibilite disponibilite = new Disponibilite(vehicule, dateDebut, dateFin);

    // Sauvegarde automatique
    return disponibiliteRepository.save(disponibilite);
  }

  /**
   * Récupère toutes les disponibilités d'un véhicule.
   *
   * @param vehicule le véhicule concerné
   * @return la liste des disponibilités du véhicule
   * @throws IllegalArgumentException si le véhicule est null
   */
  public List<Disponibilite> getDisponibilitesVehicule(Vehicule vehicule) {
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }
    if (vehicule.getId() == null) {
      throw new IllegalArgumentException("Le véhicule doit avoir un ID");
    }
    return disponibiliteRepository.findByVehiculeId(vehicule.getId());
  }

  /**
   * Récupère une disponibilité par son identifiant.
   *
   * @param disponibiliteId l'identifiant de la disponibilité
   * @return la disponibilité trouvée, ou null si elle n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public Disponibilite getDisponibiliteById(Long disponibiliteId) {
    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'ID de la disponibilité ne peut pas être null");
    }
    return disponibiliteRepository.findById(disponibiliteId);
  }

  /**
   * Récupère toutes les disponibilités de la base de données.
   *
   * @return la liste de toutes les disponibilités
   */
  public List<Disponibilite> getAllDisponibilites() {
    return disponibiliteRepository.findAll();
  }

  /**
   * Vérifie si un véhicule est disponible sur une période donnée.
   *
   * @param vehicule le véhicule à vérifier
   * @param dateDebut la date de début de la période
   * @param dateFin la date de fin de la période
   * @return true si le véhicule est disponible sur toute la période, false sinon
   * @throws IllegalArgumentException si les paramètres sont invalides
   */
  public boolean estDisponible(Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }
    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("Les dates ne peuvent pas être null");
    }
    if (dateFin.isBefore(dateDebut)) {
      throw new IllegalArgumentException("La date de fin doit être après la date de début");
    }

    List<Disponibilite> disponibilites = getDisponibilitesVehicule(vehicule);

    // Vérifier si au moins une disponibilité couvre toute la période demandée
    for (Disponibilite dispo : disponibilites) {
      if (!dispo.getDateDebut().isAfter(dateDebut) && !dispo.getDateFin().isBefore(dateFin)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Supprime une disponibilité.
   *
   * @param disponibiliteId l'identifiant de la disponibilité à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerDisponibilite(Long disponibiliteId) {
    if (disponibiliteId == null) {
      throw new IllegalArgumentException("L'ID de la disponibilité ne peut pas être null");
    }
    disponibiliteRepository.delete(disponibiliteId);
  }

  /**
   * Met à jour une disponibilité existante.
   *
   * @param disponibilite la disponibilité à mettre à jour
   * @return la disponibilité mise à jour
   * @throws IllegalArgumentException si la disponibilité est null ou n'a pas d'ID
   */
  public Disponibilite updateDisponibilite(Disponibilite disponibilite) {
    if (disponibilite == null) {
      throw new IllegalArgumentException("La disponibilité ne peut pas être null");
    }
    if (disponibilite.getId() == null) {
      throw new IllegalArgumentException("La disponibilité doit avoir un ID pour être mise à jour");
    }

    return disponibiliteRepository.save(disponibilite);
  }
}
