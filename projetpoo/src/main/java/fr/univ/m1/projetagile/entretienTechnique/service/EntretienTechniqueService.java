package fr.univ.m1.projetagile.entretienTechnique.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique;
import fr.univ.m1.projetagile.entretienTechnique.entity.TypeTechnique;
import fr.univ.m1.projetagile.entretienTechnique.persistence.EntretienTechniqueRepository;
import fr.univ.m1.projetagile.entretienTechnique.persistence.TypeTechniqueRepository;

/**
 * Service pour gérer les entretiens techniques et les types techniques.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus des repositories pour gérer les types techniques
 * et les entretiens techniques. Il s'occupe de la validation, de la création et de la gestion des
 * liens entre véhicules et types techniques.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Création de types techniques</li>
 * <li>Création de liens entre véhicules et types techniques (entretiens techniques)</li>
 * <li>Récupération des types techniques et entretiens techniques</li>
 * <li>Suppression de types techniques et entretiens techniques</li>
 * <li>Recherche par véhicule, par type technique, par date</li>
 * </ul>
 */
public class EntretienTechniqueService {

  private final TypeTechniqueRepository typeTechniqueRepository;
  private final EntretienTechniqueRepository entretienTechniqueRepository;
  private final VehiculeRepository vehiculeRepository;

  /**
   * Constructeur par défaut. Initialise les repositories.
   */
  public EntretienTechniqueService() {
    this.typeTechniqueRepository = new TypeTechniqueRepository();
    this.entretienTechniqueRepository = new EntretienTechniqueRepository();
    this.vehiculeRepository = new VehiculeRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param typeTechniqueRepository le repository des types techniques à utiliser
   * @param entretienTechniqueRepository le repository des entretiens techniques à utiliser
   * @param vehiculeRepository le repository des véhicules à utiliser
   */
  public EntretienTechniqueService(TypeTechniqueRepository typeTechniqueRepository,
      EntretienTechniqueRepository entretienTechniqueRepository,
      VehiculeRepository vehiculeRepository) {
    this.typeTechniqueRepository = typeTechniqueRepository;
    this.entretienTechniqueRepository = entretienTechniqueRepository;
    this.vehiculeRepository = vehiculeRepository;
  }

  // ==================== GESTION DES TYPES TECHNIQUES ====================

  /**
   * Crée un nouveau type technique avec validation et sauvegarde automatiques.
   *
   * @param nom le nom du type technique
   * @param kmRecommandee le nombre de kilomètres recommandés pour ce type d'entretien
   * @return le type technique créé et sauvegardé avec son ID généré
   * @throws IllegalArgumentException si le nom est null ou vide
   * @throws IllegalArgumentException si kmRecommandee est null ou négatif
   * @throws IllegalArgumentException si un type technique avec ce nom existe déjà
   */
  public TypeTechnique creerTypeTechnique(String nom, Integer kmRecommandee) {
    // Validation des paramètres
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du type technique ne peut pas être vide.");
    }
    if (kmRecommandee == null || kmRecommandee < 0) {
      throw new IllegalArgumentException(
          "Le nombre de kilomètres recommandés doit être positif ou nul.");
    }

    // Vérifier qu'un type technique avec ce nom n'existe pas déjà
    TypeTechnique existant = typeTechniqueRepository.findByNom(nom.trim());
    if (existant != null) {
      throw new IllegalArgumentException(
          "Un type technique avec le nom \"" + nom + "\" existe déjà.");
    }

    // Création et sauvegarde
    TypeTechnique typeTechnique = new TypeTechnique(nom.trim(), kmRecommandee);
    return typeTechniqueRepository.save(typeTechnique);
  }

  /**
   * Récupère tous les types techniques.
   *
   * @return la liste de tous les types techniques
   */
  public List<TypeTechnique> getAllTypesTechniques() {
    return typeTechniqueRepository.findAll();
  }

  /**
   * Récupère un type technique par son identifiant.
   *
   * @param id l'identifiant du type technique
   * @return le type technique trouvé, ou null si il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public TypeTechnique getTypeTechniqueById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID du type technique ne peut pas être null.");
    }
    return typeTechniqueRepository.findById(id);
  }

  /**
   * Récupère un type technique par son nom.
   *
   * @param nom le nom du type technique
   * @return le type technique trouvé, ou null si il n'existe pas
   * @throws IllegalArgumentException si le nom est null ou vide
   */
  public TypeTechnique getTypeTechniqueByNom(String nom) {
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du type technique ne peut pas être vide.");
    }
    return typeTechniqueRepository.findByNom(nom.trim());
  }

  /**
   * Met à jour un type technique existant.
   *
   * @param typeTechnique le type technique à mettre à jour
   * @return le type technique mis à jour
   * @throws IllegalArgumentException si le type technique est null ou n'a pas d'ID
   */
  public TypeTechnique updateTypeTechnique(TypeTechnique typeTechnique) {
    if (typeTechnique == null) {
      throw new IllegalArgumentException("Le type technique ne peut pas être null.");
    }
    if (typeTechnique.getId() == null) {
      throw new IllegalArgumentException(
          "Le type technique doit avoir un ID pour être mis à jour.");
    }

    // Validation des champs
    if (typeTechnique.getNom() == null || typeTechnique.getNom().trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du type technique ne peut pas être vide.");
    }
    if (typeTechnique.getKmRecommandee() == null || typeTechnique.getKmRecommandee() < 0) {
      throw new IllegalArgumentException(
          "Le nombre de kilomètres recommandés doit être positif ou nul.");
    }

    // Normaliser le nom
    String nomNormalise = typeTechnique.getNom().trim();
    typeTechnique.setNom(nomNormalise);

    // Vérifier l'unicité du nom : aucun autre type technique ne doit avoir ce nom
    TypeTechnique existant = typeTechniqueRepository.findByNom(nomNormalise);
    if (existant != null && !existant.getId().equals(typeTechnique.getId())) {
      throw new IllegalArgumentException(
          "Un type technique avec ce nom existe déjà. Le nom doit être unique.");
    }
    return typeTechniqueRepository.save(typeTechnique);
  }

  /**
   * Supprime un type technique.
   *
   * @param id l'identifiant du type technique à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerTypeTechnique(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID du type technique ne peut pas être null.");
    }

    // Vérifier qu'il n'y a pas d'entretiens techniques liés à ce type
    List<EntretienTechnique> entretiens = entretienTechniqueRepository.findByTypeTechniqueId(id);
    if (!entretiens.isEmpty()) {
      throw new IllegalStateException(
          "Impossible de supprimer le type technique car il est utilisé par " + entretiens.size()
              + " entretien(s) technique(s). Supprimez d'abord les entretiens associés.");
    }

    typeTechniqueRepository.delete(id);
  }

  // ==================== GESTION DES ENTRETIENS TECHNIQUES ====================

  /**
   * Crée un lien entre un véhicule et un type technique (crée un entretien technique).
   *
   * @param vehicule le véhicule concerné
   * @param typeTechnique le type technique concerné
   * @param date la date de l'entretien technique
   * @return l'entretien technique créé et sauvegardé avec son ID généré
   * @throws IllegalArgumentException si le véhicule est null ou n'a pas d'ID
   * @throws IllegalArgumentException si le type technique est null ou n'a pas d'ID
   * @throws IllegalArgumentException si la date est null
   */
  public EntretienTechnique creerEntretienTechnique(Vehicule vehicule, TypeTechnique typeTechnique,
      LocalDate date) {
    // Validation des paramètres
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null.");
    }
    if (vehicule.getId() == null) {
      throw new IllegalArgumentException("Le véhicule doit avoir un ID.");
    }
    if (typeTechnique == null) {
      throw new IllegalArgumentException("Le type technique ne peut pas être null.");
    }
    if (typeTechnique.getId() == null) {
      throw new IllegalArgumentException("Le type technique doit avoir un ID.");
    }
    if (date == null) {
      throw new IllegalArgumentException("La date ne peut pas être null.");
    }

    // Création et sauvegarde
    EntretienTechnique entretienTechnique = new EntretienTechnique(vehicule, typeTechnique, date);
    return entretienTechniqueRepository.save(entretienTechnique);
  }

  /**
   * Crée un entretien technique en utilisant les IDs du véhicule et du type technique.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param typeTechniqueId l'identifiant du type technique
   * @param date la date de l'entretien technique
   * @return l'entretien technique créé et sauvegardé avec son ID généré
   * @throws IllegalArgumentException si les IDs sont null
   * @throws IllegalArgumentException si le véhicule ou le type technique n'existent pas
   */
  public EntretienTechnique creerEntretienTechnique(Long vehiculeId, Long typeTechniqueId,
      LocalDate date) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }
    if (typeTechniqueId == null) {
      throw new IllegalArgumentException("L'ID du type technique ne peut pas être null.");
    }
    if (date == null) {
      throw new IllegalArgumentException("La date ne peut pas être null.");
    }

    // Récupérer le véhicule et le type technique depuis les repositories
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    TypeTechnique typeTechnique = typeTechniqueRepository.findById(typeTechniqueId);
    if (typeTechnique == null) {
      throw new IllegalArgumentException(
          "Aucun type technique trouvé avec l'identifiant " + typeTechniqueId);
    }

    return creerEntretienTechnique(vehicule, typeTechnique, date);
  }

  /**
   * Récupère tous les entretiens techniques.
   *
   * @return la liste de tous les entretiens techniques
   */
  public List<EntretienTechnique> getAllEntretiensTechniques() {
    return entretienTechniqueRepository.findAll();
  }

  /**
   * Récupère un entretien technique par son identifiant.
   *
   * @param id l'identifiant de l'entretien technique
   * @return l'entretien technique trouvé, ou null si il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public EntretienTechnique getEntretienTechniqueById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID de l'entretien technique ne peut pas être null.");
    }
    return entretienTechniqueRepository.findById(id);
  }

  /**
   * Récupère tous les entretiens techniques d'un véhicule.
   *
   * @param vehicule le véhicule concerné
   * @return la liste des entretiens techniques de ce véhicule
   * @throws IllegalArgumentException si le véhicule est null ou n'a pas d'ID
   */
  public List<EntretienTechnique> getEntretiensTechniquesByVehicule(Vehicule vehicule) {
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null.");
    }
    if (vehicule.getId() == null) {
      throw new IllegalArgumentException("Le véhicule doit avoir un ID.");
    }
    return entretienTechniqueRepository.findByVehiculeId(vehicule.getId());
  }

  /**
   * Récupère tous les entretiens techniques d'un véhicule par son ID.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return la liste des entretiens techniques de ce véhicule
   * @throws IllegalArgumentException si l'ID est null
   */
  public List<EntretienTechnique> getEntretiensTechniquesByVehiculeId(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }
    return entretienTechniqueRepository.findByVehiculeId(vehiculeId);
  }

  /**
   * Récupère tous les entretiens techniques d'un type technique.
   *
   * @param typeTechnique le type technique concerné
   * @return la liste des entretiens techniques de ce type
   * @throws IllegalArgumentException si le type technique est null ou n'a pas d'ID
   */
  public List<EntretienTechnique> getEntretiensTechniquesByTypeTechnique(
      TypeTechnique typeTechnique) {
    if (typeTechnique == null) {
      throw new IllegalArgumentException("Le type technique ne peut pas être null.");
    }
    if (typeTechnique.getId() == null) {
      throw new IllegalArgumentException("Le type technique doit avoir un ID.");
    }
    return entretienTechniqueRepository.findByTypeTechniqueId(typeTechnique.getId());
  }

  /**
   * Récupère tous les entretiens techniques d'un véhicule pour un type technique spécifique.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param typeTechniqueId l'identifiant du type technique
   * @return la liste des entretiens techniques correspondants
   * @throws IllegalArgumentException si les IDs sont null
   */
  public List<EntretienTechnique> getEntretiensTechniquesByVehiculeAndType(Long vehiculeId,
      Long typeTechniqueId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }
    if (typeTechniqueId == null) {
      throw new IllegalArgumentException("L'ID du type technique ne peut pas être null.");
    }
    return entretienTechniqueRepository.findByVehiculeIdAndTypeTechniqueId(vehiculeId,
        typeTechniqueId);
  }

  /**
   * Récupère tous les entretiens techniques à partir d'une date donnée.
   *
   * @param date la date à partir de laquelle récupérer les entretiens
   * @return la liste des entretiens techniques à partir de cette date
   * @throws IllegalArgumentException si la date est null
   */
  public List<EntretienTechnique> getEntretiensTechniquesByDateAfter(LocalDate date) {
    if (date == null) {
      throw new IllegalArgumentException("La date ne peut pas être null.");
    }
    return entretienTechniqueRepository.findByDateAfter(date);
  }

  /**
   * Récupère le dernier entretien technique d'un véhicule (le plus récent).
   *
   * @param vehiculeId l'identifiant du véhicule
   * @return le dernier entretien technique du véhicule, ou null si aucun entretien n'existe
   * @throws IllegalArgumentException si l'ID est null
   */
  public EntretienTechnique getDernierEntretienTechnique(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }
    List<EntretienTechnique> entretiens = entretienTechniqueRepository.findByVehiculeId(vehiculeId);
    if (entretiens.isEmpty()) {
      return null;
    }
    // Les résultats sont déjà triés par date DESC, donc le premier est le plus récent
    return entretiens.get(0);
  }

  /**
   * Récupère le dernier entretien technique d'un véhicule pour un type technique spécifique.
   *
   * @param vehiculeId l'identifiant du véhicule
   * @param typeTechniqueId l'identifiant du type technique
   * @return le dernier entretien technique du type spécifié, ou null si aucun entretien n'existe
   * @throws IllegalArgumentException si les IDs sont null
   */
  public EntretienTechnique getDernierEntretienTechniqueByType(Long vehiculeId,
      Long typeTechniqueId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }
    if (typeTechniqueId == null) {
      throw new IllegalArgumentException("L'ID du type technique ne peut pas être null.");
    }
    List<EntretienTechnique> entretiens =
        entretienTechniqueRepository.findByVehiculeIdAndTypeTechniqueId(vehiculeId,
            typeTechniqueId);
    if (entretiens.isEmpty()) {
      return null;
    }
    // Les résultats sont déjà triés par date DESC, donc le premier est le plus récent
    return entretiens.get(0);
  }

  /**
   * Met à jour un entretien technique existant.
   *
   * @param entretienTechnique l'entretien technique à mettre à jour
   * @return l'entretien technique mis à jour
   * @throws IllegalArgumentException si l'entretien technique est null ou n'a pas d'ID
   */
  public EntretienTechnique updateEntretienTechnique(EntretienTechnique entretienTechnique) {
    if (entretienTechnique == null) {
      throw new IllegalArgumentException("L'entretien technique ne peut pas être null.");
    }
    if (entretienTechnique.getId() == null) {
      throw new IllegalArgumentException(
          "L'entretien technique doit avoir un ID pour être mis à jour.");
    }

    return entretienTechniqueRepository.save(entretienTechnique);
  }

  /**
   * Supprime un entretien technique.
   *
   * @param id l'identifiant de l'entretien technique à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerEntretienTechnique(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID de l'entretien technique ne peut pas être null.");
    }
    entretienTechniqueRepository.delete(id);
  }

  // ==================== RECOMMANDATIONS D'ENTRETIEN ====================

  /**
   * Génère les recommandations d'entretien technique pour un véhicule donné. Cette méthode récupère
   * le dernier kilométrage disponible du véhicule depuis les vérifications de location, puis
   * compare ce kilométrage avec les kilométrages recommandés de chaque type technique. Si le
   * kilométrage dépasse le kilométrage recommandé d'un type technique et qu'aucun entretien
   * technique n'a encore été effectué pour ce type sur ce véhicule, le nom du type technique est
   * ajouté à la liste.
   *
   * @param vehiculeId l'identifiant du véhicule pour lequel générer les recommandations
   * @return la liste des noms des types techniques pour lesquels un entretien est recommandé
   * @throws IllegalArgumentException si l'ID du véhicule est null
   * @throws IllegalArgumentException si le véhicule n'existe pas
   */
  public List<String> genererRecommandationsEntretien(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'ID du véhicule ne peut pas être null.");
    }

    // Vérifier que le véhicule existe
    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'identifiant " + vehiculeId);
    }

    // Récupérer le dernier kilométrage disponible depuis les vérifications de location
    VerificationRepository verificationRepository = new VerificationRepository();
    Integer dernierKilometrage = verificationRepository.getDernierKilometrage(vehiculeId);

    // Si aucun kilométrage n'est disponible, retourner une liste vide
    if (dernierKilometrage == null) {
      return new ArrayList<>();
    }

    // Récupérer tous les types techniques
    List<TypeTechnique> tousLesTypes = typeTechniqueRepository.findAll();

    // Récupérer tous les entretiens techniques existants pour ce véhicule
    List<EntretienTechnique> entretiensExistants =
        entretienTechniqueRepository.findByVehiculeId(vehiculeId);

    // Créer la liste des noms de types techniques recommandés
    List<String> recommandations = new ArrayList<>();

    // Pour chaque type technique, vérifier si une recommandation est nécessaire
    for (TypeTechnique typeTechnique : tousLesTypes) {
      Integer kmRecommandee = typeTechnique.getKmRecommandee();

      // Si le kilométrage recommandé est null ou 0, on ignore ce type
      if (kmRecommandee == null || kmRecommandee <= 0) {
        continue;
      }

      // Vérifier si le kilométrage actuel dépasse le kilométrage recommandé
      if (dernierKilometrage >= kmRecommandee) {
        // Vérifier s'il existe déjà un entretien technique pour ce véhicule et ce type
        boolean entretienExistant = false;
        for (EntretienTechnique entretien : entretiensExistants) {
          if (entretien.getTypeTechnique().getId().equals(typeTechnique.getId())) {
            entretienExistant = true;
            break;
          }
        }

        // Si aucun entretien n'existe pour ce type, ajouter le nom du type technique
        if (!entretienExistant) {
          recommandations.add(typeTechnique.getNom());
        }
      }
    }

    return recommandations;
  }
}
