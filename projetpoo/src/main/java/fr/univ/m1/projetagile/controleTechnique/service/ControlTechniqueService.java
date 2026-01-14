package fr.univ.m1.projetagile.controleTechnique.service;


import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.controleTechnique.entity.ControleTechnique;
import fr.univ.m1.projetagile.controleTechnique.persistence.ControleTechniqueRepository;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;

/**
 * Service pour la gestion des contrôles techniques des véhicules Correspond aux US : - US.A.8 :
 * Renseigner les informations sur le contrôle technique - US.A.9 : Rappel sur la nécessité de
 * repasser le contrôle technique - US.A.11 : Recommandations d'entretien par kilométrage
 */

public class ControlTechniqueService {

  private final VehiculeRepository vehiculeRepository;
  private final ControleTechniqueRepository controleTechniqueRepository;

  public ControlTechniqueService(VehiculeRepository vehiculeRepository) {
    this.vehiculeRepository = vehiculeRepository;
    this.controleTechniqueRepository = new ControleTechniqueRepository();
  }

  public ControlTechniqueService(VehiculeRepository vehiculeRepository,
      ControleTechniqueRepository controleTechniqueRepository) {
    this.vehiculeRepository = vehiculeRepository;
    this.controleTechniqueRepository = controleTechniqueRepository;
  }

  // ==================== MÉTHODES PRINCIPALES ====================

  /**
   * Obtient le contrôle technique d'un véhicule (ou null si inexistant)
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return le contrôle technique du véhicule ou null
   */
  public ControleTechnique getControleTechniqueByVehiculeId(Long vehiculeId) {
    return controleTechniqueRepository.findByVehiculeId(vehiculeId);
  }

  /**
   * Obtient ou crée un contrôle technique pour un véhicule
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return le contrôle technique du véhicule
   */
  public ControleTechnique getOrCreateControleTechnique(Long vehiculeId) {
    ControleTechnique ct = controleTechniqueRepository.findByVehiculeId(vehiculeId);
    if (ct == null) {
      Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
      if (vehicule == null) {
        throw new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + vehiculeId);
      }
      ct = new ControleTechnique(vehicule);
      ct = controleTechniqueRepository.save(ct);
    }
    return ct;
  }

  /**
   * Met à jour les informations du contrôle technique d'un véhicule
   */
  public ControleTechnique updateControleTechnique(Long vehiculeId, LocalDate dateMiseEnCirculation,
      LocalDate dateDernierControle, Integer kilometrageActuel, Integer kilometrageDernierControle,
      LocalDate dateProchainControle, LocalDate dateDernierEntretien, String resultat) {
    ControleTechnique ct = getOrCreateControleTechnique(vehiculeId);

    if (dateMiseEnCirculation != null) {
      ct.setDateMiseEnCirculation(dateMiseEnCirculation);
    }
    if (dateDernierControle != null) {
      ct.setDate(dateDernierControle);
    }
    if (kilometrageActuel != null) {
      ct.setKilometrageActuel(kilometrageActuel);
    }
    if (kilometrageDernierControle != null) {
      ct.setKilometrageDernierControle(kilometrageDernierControle);
    }
    if (dateProchainControle != null) {
      ct.setDateLimite(dateProchainControle);
    }
    if (dateDernierEntretien != null) {
      ct.setDateDernierEntretien(dateDernierEntretien);
    }
    if (resultat != null) {
      ct.setResultat(resultat);
    }

    return controleTechniqueRepository.save(ct);
  }

  /**
   * Calcule l'âge d'un véhicule en années
   */
  public int getAgeVehicule(Long vehiculeId) {
    ControleTechnique ct = getControleTechniqueByVehiculeId(vehiculeId);
    if (ct == null || ct.getDateMiseEnCirculation() == null) {
      return 0;
    }
    return Period.between(ct.getDateMiseEnCirculation(), LocalDate.now()).getYears();
  }

  /**
   * Verifies if a vehicle needs a technical control soon.
   *
   * @param vehicule the vehicle to check
   * @return true if control is needed, false otherwise
   */
  public boolean doitFaireControleProchainement(Vehicule vehicule) {
    if (vehicule == null) {
      return false;
    }

    // obtenir la date du prochain contrôle
    LocalDate dateProchainControle = calculerDateProchainControle(vehicule);

    // Si la date n'a pas encore été calculée, on la calcule et on l'enregistre
    if (dateProchainControle == null) {
      dateProchainControle = calculerEtEnregistrerProchainControle(vehicule);
    }

    // verifier si le contrôle est proche (dans les 30 jours)
    return estProchainControleProche(dateProchainControle, 30);
  }

  /**
   * Calculates and returns the date of the next technical control (without saving). Follows French
   * regulations: - New vehicle (0-4 years): first control at 4th anniversary - 4-10 years: control
   * every 2 years - Over 10 years: annual control
   *
   * @param vehicule the vehicle to check
   * @return the date of the next control, or null if cannot be calculated
   */
  public LocalDate calculerDateProchainControle(Vehicule vehicule) {
    if (vehicule == null) {
      return null;
    }

    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());
    if (ct == null) {
      return null;
    }

    LocalDate dateDernierControle = ct.getDate();
    LocalDate dateMiseEnCirculation = ct.getDateMiseEnCirculation();

    if (dateMiseEnCirculation == null) {
      // si la date de mise en circulation est inconnue, on ne peut pas calculer
      return null;
    }

    LocalDate aujourdhui = LocalDate.now();
    int ageVehicule = Period.between(dateMiseEnCirculation, aujourdhui).getYears();


    if (dateDernierControle == null) {
      return dateMiseEnCirculation.plusYears(4);
    }


    if (ageVehicule < 10) {
      return dateDernierControle.plusYears(2);
    }


    return dateDernierControle.plusYears(1);
  }

  /**
   * Calculates and saves the date of the next technical control.
   *
   * @param vehicule the vehicle to check
   * @return the calculated next control date
   */
  public LocalDate calculerEtEnregistrerProchainControle(Vehicule vehicule) {
    LocalDate dateProchainControle = calculerDateProchainControle(vehicule);

    if (dateProchainControle != null) {
      ControleTechnique ct = getOrCreateControleTechnique(vehicule.getId());
      ct.setDateLimite(dateProchainControle);
      controleTechniqueRepository.save(ct);
    }

    return dateProchainControle;
  }

  /**
   * Checks if the next technical control is approaching within a certain number of days.
   *
   * @param dateProchainControle the date of the next control
   * @param joursAlerte the number of days for alert threshold
   * @return true if reminder is needed, false otherwise
   */
  private boolean estProchainControleProche(LocalDate dateProchainControle, int joursAlerte) {
    if (dateProchainControle == null) {
      return false;
    }

    LocalDate aujourdhui = LocalDate.now();

    // If already past due
    if (dateProchainControle.isBefore(aujourdhui)) {
      return true;
    }

    // Calculate remaining days
    long joursRestants = ChronoUnit.DAYS.between(aujourdhui, dateProchainControle);

    return joursRestants <= joursAlerte;
  }

  // ==================== INFORMATION METHODS ====================

  /**
   * Returns the detailed status of a vehicle's technical control.
   *
   * @param vehicule the vehicle to check
   * @return a status string with control details
   */
  public String getStatutControleDetaille(Vehicule vehicule) {
    if (vehicule == null) {
      return "Véhicule non spécifié";
    }

    // Always recalculate to ensure fresh data (don't trust cached dateLimite)
    LocalDate dateProchainControle = calculerDateProchainControle(vehicule);

    if (dateProchainControle == null) {
      return "manque des informations pour calculer la date du prochain contrôle";
    }

    LocalDate aujourdhui = LocalDate.now();

    // If already past due
    if (dateProchainControle.isBefore(aujourdhui)) {
      long joursDepasses = ChronoUnit.DAYS.between(dateProchainControle, aujourdhui);
      return String.format("URGENT : contrôle technique en retard de %d jours", joursDepasses);
    }

    // Calculate remaining days
    long joursRestants = ChronoUnit.DAYS.between(aujourdhui, dateProchainControle);

    if (joursRestants <= 7) {
      return String.format("URGENT : contrôle technique dans %d jours (%s)", joursRestants,
          dateProchainControle);
    } else if (joursRestants <= 30) {
      return String.format("PRÉVU : contrôle technique dans %d jours (%s)", joursRestants,
          dateProchainControle);
    } else if (joursRestants <= 90) {
      return String.format("À VENIR : contrôle technique dans %d jours (%s)", joursRestants,
          dateProchainControle);
    } else {
      return String.format("OK : contrôle technique dans %d jours (%s)", joursRestants,
          dateProchainControle);
    }
  }

  /**
   * Returns the status of all vehicles.
   *
   * @param vehicules the list of vehicles to check
   * @return a list of status strings
   */
  public List<String> getTousStatutsVehicules(List<Vehicule> vehicules) {
    List<String> statuts = new ArrayList<>();

    for (Vehicule vehicule : vehicules) {
      String statut = getStatutControleDetaille(vehicule);
      String ligne = String.format("%s %s: %s", vehicule.getMarque(), vehicule.getModele(), statut);
      statuts.add(ligne);
    }

    return statuts;
  }

  // ==================== REPORT METHODS ====================

  /**
   * Generates a detailed report for a given vehicle.
   *
   * @param vehicule the vehicle to report on
   * @return a formatted report string
   */
  public String genererRapportControle(Vehicule vehicule) {
    StringBuilder rapport = new StringBuilder();

    rapport.append("TECHNICAL CONTROL REPORT\n");
    rapport.append("=".repeat(50)).append("\n");
    rapport.append("Véhicule: ").append(vehicule.getMarque()).append(" ")
        .append(vehicule.getModele()).append("\n");
    rapport.append("Immatriculation: ").append(vehicule.getId()).append("\n\n");

    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());

    // Basic information
    rapport.append("BASIC INFORMATION\n");
    rapport.append("-".repeat(30)).append("\n");
    if (ct != null && ct.getDateMiseEnCirculation() != null) {
      rapport.append("Date mise en circulation: ").append(ct.getDateMiseEnCirculation())
          .append("\n");
      rapport.append("Âge du véhicule: ").append(getAgeVehicule(vehicule.getId())).append(" ans\n");
    }
    if (ct != null && ct.getKilometrageActuel() != null) {
      rapport.append("Kilométrage actuel: ").append(ct.getKilometrageActuel()).append(" km\n");
    }
    rapport.append("\n");

    // Control history
    rapport.append("CONTROL HISTORY\n");
    rapport.append("-".repeat(30)).append("\n");
    if (ct != null && ct.getDate() != null) {
      rapport.append("Dernier contrôle: ").append(ct.getDate()).append("\n");
    }
    if (ct != null && ct.getKilometrageDernierControle() != null) {
      rapport.append("Kilométrage dernier contrôle: ").append(ct.getKilometrageDernierControle())
          .append(" km\n");
    }
    if (ct != null && ct.getDateLimite() != null) {
      rapport.append("Prochain contrôle calculé: ").append(ct.getDateLimite()).append("\n");
    }
    rapport.append("\n");

    // Current status
    rapport.append("CURRENT STATUS\n");
    rapport.append("-".repeat(30)).append("\n");
    rapport.append(getStatutControleDetaille(vehicule)).append("\n\n");

    // Maintenance recommendations
    List<String> recommandations = getRecommandationsEntretienParKilometrage(vehicule);
    if (!recommandations.isEmpty()) {
      rapport.append("MAINTENANCE RECOMMENDATIONS\n");
      rapport.append("-".repeat(30)).append("\n");
      for (String recommandation : recommandations) {
        rapport.append("- ").append(recommandation).append("\n");
      }
    }

    rapport.append("=".repeat(50));
    return rapport.toString();
  }

  /**
   * Generates a detailed report for all vehicles of a given agent.
   *
   * @param vehicules the list of vehicles to report on
   * @return a formatted report string
   */
  public String genererRapportPourAgent(List<Vehicule> vehicules) {
    if (vehicules == null || vehicules.isEmpty()) {
      return "Aucun véhicule à analyser";
    }

    StringBuilder rapport = new StringBuilder();
    rapport.append("TECHNICAL CONTROL REPORT - ALL VEHICLES\n");
    rapport.append("=".repeat(60)).append("\n");
    rapport.append("Nombre total de véhicules: ").append(vehicules.size()).append("\n\n");

    int urgents = 0;
    int prochains = 0;
    int normaux = 0;

    for (Vehicule vehicule : vehicules) {
      boolean doitControle = doitFaireControleProchainement(vehicule);
      String statut = getStatutControleDetaille(vehicule);

      if (statut.contains("URGENT")) {
        urgents++;
      } else if (doitControle) {
        prochains++;
      } else {
        normaux++;
      }

      rapport.append("> ").append(vehicule.getMarque()).append(" ").append(vehicule.getModele())
          .append("\n");
      rapport.append("  ").append(statut).append("\n\n");
    }

    // Summary
    rapport.append("SUMMARY\n");
    rapport.append("=".repeat(30)).append("\n");
    rapport.append("Vehicles requiring URGENT control: ").append(urgents).append("\n");
    rapport.append("Vehicles with upcoming control: ").append(prochains).append("\n");
    rapport.append("Vehicles without imminent control: ").append(normaux).append("\n");
    rapport.append("=".repeat(60));

    return rapport.toString();
  }

  // ==================== MAINTENANCE METHODS ====================

  /**
   * For US.A.11: maintenance recommendations based on mileage.
   *
   * @param vehicule the vehicle to check
   * @return a list of maintenance recommendations
   */
  public List<String> getRecommandationsEntretienParKilometrage(Vehicule vehicule) {
    List<String> recommandations = new ArrayList<>();

    if (vehicule == null) {
      return recommandations;
    }

    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());
    if (ct == null || ct.getKilometrageActuel() == null
        || ct.getKilometrageDernierControle() == null) {
      return recommandations;
    }

    int kilometrageActuel = ct.getKilometrageActuel();
    int kilometrageDernierControle = ct.getKilometrageDernierControle();
    int kilometresParcourus = kilometrageActuel - kilometrageDernierControle;

    if (kilometresParcourus <= 0) {
      return recommandations;
    }


    if (kilometresParcourus >= 15000) {
      recommandations.add("changer l'huile et le filtre (tous les 15 000 km)");
    }

    if (kilometresParcourus >= 30000) {
      recommandations.add("vérifier les plaques de frein et les disques (tous les 30 000 km)");
    }

    if (kilometresParcourus >= 60000) {
      recommandations.add("changer la courroie d'entraînement (tous les 60 000 km)");
    }

    if (kilometresParcourus >= 80000) {
      recommandations.add("changer les bougies d'allumage (tous les 80 000 km)");
    }

    if (kilometresParcourus >= 100000) {
      recommandations.add("vérifier l'état général du véhicule (tous les 100 000 km)");
    }

    return recommandations;
  }

  /**
   * Registers a new technical control. Called when the vehicle completes a technical control.
   *
   * @param vehiculeId the vehicle identifier
   * @param dateControle the control date
   * @param kilometrage the mileage
   * @param resultat the control result
   * @param commentaires additional comments
   */
  public void enregistrerNouveauControle(Long vehiculeId, LocalDate dateControle,
      Integer kilometrage, String resultat, String commentaires) {
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);

    if (vehicule == null) {
      throw new RuntimeException("Véhicule non trouvé");
    }

    ControleTechnique ct = getOrCreateControleTechnique(vehiculeId);

    // Update control information
    ct.setDate(dateControle);
    ct.setKilometrageDernierControle(kilometrage);
    ct.setResultat(resultat);

    // Recalculate next control date
    LocalDate nouveauProchainControle = calculerDateProchainControle(vehicule);
    ct.setDateLimite(nouveauProchainControle);

    // Save changes
    controleTechniqueRepository.save(ct);
  }

  // ==================== UTILITY METHODS ====================

  /**
   * Calculates the remaining days before the next technical control.
   *
   * @param vehicule the vehicle to check
   * @return the number of days remaining, or -1 if cannot be calculated
   */
  public long calculerJoursRestants(Vehicule vehicule) {
    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());
    LocalDate dateProchainControle = ct != null ? ct.getDateLimite() : null;
    if (dateProchainControle == null) {
      dateProchainControle = calculerDateProchainControle(vehicule);
    }

    if (dateProchainControle == null) {
      return -1;
    }

    LocalDate aujourdhui = LocalDate.now();
    return ChronoUnit.DAYS.between(aujourdhui, dateProchainControle);
  }

  /**
   * Checks if the technical control is overdue.
   *
   * @param vehicule the vehicle to check
   * @return true if control is overdue, false otherwise
   */
  public boolean estControleDepasse(Vehicule vehicule) {
    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());
    LocalDate dateProchainControle = ct != null ? ct.getDateLimite() : null;
    if (dateProchainControle == null) {
      return false;
    }

    return dateProchainControle.isBefore(LocalDate.now());
  }
}
