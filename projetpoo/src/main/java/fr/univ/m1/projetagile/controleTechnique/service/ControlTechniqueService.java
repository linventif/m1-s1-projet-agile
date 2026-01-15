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
   * Vérifie si un véhicule a besoin d'un contrôle technique prochainement.
   *
   * @param vehicule le véhicule à vérifier
   * @return true si un contrôle est nécessaire, false sinon
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
   * Calculer et retourner la date du prochain contrôle (sans enregistrer). Suivant les
   * réglementations françaises: - Nouveau véhicule (0-4 ans): premier contrôle à la 4ème année -
   * 4-10 ans: contrôle tous les 2 ans - Plus de 10 ans: contrôle annuel
   *
   * @param vehicule le véhicule à vérifier
   * @return la date du prochain contrôle, ou null si elle ne peut pas être calculée
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
   * Calculer et enregistrer la date du prochain contrôle. Appelé lorsque le véhicule a effectué un
   * contrôle technique.
   *
   * @param vehicule le véhicule à vérifier
   * @return la date du prochain contrôle calculée
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
   * Vérifie si le prochain contrôle technique est proche dans un certain nombre de jours.
   *
   * @param dateProchainControle la date du prochain contrôle
   * @param joursAlerte le nombre de jours pour le seuil d'alerte
   * @return true si un rappel est nécessaire, false sinon
   */
  private boolean estProchainControleProche(LocalDate dateProchainControle, int joursAlerte) {
    if (dateProchainControle == null) {
      return false;
    }

    LocalDate aujourdhui = LocalDate.now();

    // Si le contrôle est déjà en retard
    if (dateProchainControle.isBefore(aujourdhui)) {
      return true;
    }

    // Calculer les jours restants
    long joursRestants = ChronoUnit.DAYS.between(aujourdhui, dateProchainControle);

    return joursRestants <= joursAlerte;
  }

  // ==================== INFORMATION METHODS ====================

  /**
   * Retourne le statut détaillé du contrôle technique d'un véhicule.
   *
   * @param vehicule le véhicule à vérifier
   * @return une chaîne de caractères représentant le statut avec les détails du contrôle
   */
  public String getStatutControleDetaille(Vehicule vehicule) {
    if (vehicule == null) {
      return "Véhicule non spécifié";
    }

    // Toujours recalculer pour s'assurer que les données sont fraîches (ne pas faire confiance à la
    // dateLimite mise en cache)
    LocalDate dateProchainControle = calculerDateProchainControle(vehicule);

    if (dateProchainControle == null) {
      return "manque des informations pour calculer la date du prochain contrôle";
    }

    LocalDate aujourdhui = LocalDate.now();

    // Si le contrôle est déjà en retard
    if (dateProchainControle.isBefore(aujourdhui)) {
      long joursDepasses = ChronoUnit.DAYS.between(dateProchainControle, aujourdhui);
      return String.format("URGENT : contrôle technique en retard de %d jours", joursDepasses);
    }

    // Calculer les jours restants
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
   * Retourne le statut de tous les véhicules.
   *
   * @param vehicules la liste des véhicules à vérifier
   * @return une liste de chaînes de caractères représentant les statuts
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
   * Génère un rapport détaillé pour un véhicule donné.
   *
   * @param vehicule le véhicule à reporter
   * @return un rapport formaté sous forme de chaîne de caractères
   */
  public String genererRapportControle(Vehicule vehicule) {
    StringBuilder rapport = new StringBuilder();

    rapport.append("RAPPORT DE CONTROLE TECHNIQUE\n");
    rapport.append("=".repeat(50)).append("\n");
    rapport.append("Véhicule: ").append(vehicule.getMarque()).append(" ")
        .append(vehicule.getModele()).append("\n");
    rapport.append("Immatriculation: ").append(vehicule.getId()).append("\n\n");

    ControleTechnique ct = getControleTechniqueByVehiculeId(vehicule.getId());

    // Informations basiques
    rapport.append("INFORMATIONS BASIQUES\n");
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

    // Historique des contrôles
    rapport.append("HISTORIQUE DES CONTROLES\n");
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

    // Statut actuel
    rapport.append("STATUT ACTUEL\n");
    rapport.append("-".repeat(30)).append("\n");
    rapport.append(getStatutControleDetaille(vehicule)).append("\n\n");

    // Recommandations d'entretien
    List<String> recommandations = getRecommandationsEntretienParKilometrage(vehicule);
    if (!recommandations.isEmpty()) {
      rapport.append("RECOMMANDATIONS D'ENTRETIEN\n");
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
    rapport.append("RAPPORT DE CONTROLE TECHNIQUE - TOUS LES VEHICULES\n");
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

    // Résumé
    rapport.append("RÉSUMÉ\n");
    rapport.append("=".repeat(30)).append("\n");
    rapport.append("Véhicules nécessitant un contrôle urgent: ").append(urgents).append("\n");
    rapport.append("Véhicules avec un contrôle à venir: ").append(prochains).append("\n");
    rapport.append("Véhicules sans contrôle imminent: ").append(normaux).append("\n");
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
   * Enregistre un nouveau contrôle technique. Appelé lorsque le véhicule a effectué un contrôle
   * technique.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param dateControle la date du contrôle
   * @param kilometrage le kilométrage
   * @param resultat le résultat du contrôle
   * @param commentaires commentaires supplémentaires
   */
  public void enregistrerNouveauControle(Long vehiculeId, LocalDate dateControle,
      Integer kilometrage, String resultat, String commentaires) {
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);

    if (vehicule == null) {
      throw new RuntimeException("Véhicule non trouvé");
    }

    ControleTechnique ct = getOrCreateControleTechnique(vehiculeId);

    // Mettre à jour les informations du contrôle
    ct.setDate(dateControle);
    ct.setKilometrageDernierControle(kilometrage);
    ct.setResultat(resultat);

    // Recalculer la date du prochain contrôle
    LocalDate nouveauProchainControle = calculerDateProchainControle(vehicule);
    ct.setDateLimite(nouveauProchainControle);

    // Enregistrer les changements
    controleTechniqueRepository.save(ct);
  }

  // ==================== UTILITY METHODS ====================

  /**
   * Calculer les jours restants avant le prochain contrôle technique.
   *
   * @param vehicule le véhicule à vérifier
   * @return le nombre de jours restants, ou -1 si il ne peut pas être calculé
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
   * Vérifie si le contrôle technique est en retard.
   *
   * @param vehicule le véhicule à vérifier
   * @return true si le contrôle est en retard, false sinon
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
