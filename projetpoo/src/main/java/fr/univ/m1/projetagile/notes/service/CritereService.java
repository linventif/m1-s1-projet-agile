package fr.univ.m1.projetagile.notes.service;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.persistence.CritereRepository;

/**
 * Service pour gérer les critères d'évaluation.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus du repository pour gérer les critères. Il s'occupe
 * de la création, récupération et réutilisation des critères pour éviter les duplications.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Création de critères avec validation automatique</li>
 * <li>Récupération ou création de critères (évite les doublons)</li>
 * <li>Sauvegarde automatique en base de données</li>
 * <li>Récupération de tous les critères existants</li>
 * <li>Suppression de critères</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * CritereService service = new CritereService();
 *
 * // Créer ou récupérer un critère (évite les doublons)
 * Critere ponctualite = service.getOrCreateCritere("Ponctualité", 8.5);
 *
 * // Créer une liste de critères
 * List<Critere> criteres = service.getOrCreateCriteres(
 *     new String[] {"Ponctualité", "Communication", "Professionnalisme"},
 *     new Double[] {8.5, 9.0, 8.0});
 *
 * // Récupérer tous les critères existants
 * List<Critere> tousCriteres = service.getAllCriteres();
 * }</pre>
 *
 * @see Critere
 * @see CritereRepository
 *
 * @author Projet Agile M1
 * @version 1.0
 * @since 1.0
 */
public class CritereService {

  private final CritereRepository critereRepository;

  /**
   * Constructeur par défaut. Initialise le repository de critères.
   */
  public CritereService() {
    this.critereRepository = new CritereRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param critereRepository le repository de critères à utiliser
   */
  public CritereService(CritereRepository critereRepository) {
    this.critereRepository = critereRepository;
  }

  /**
   * Crée un nouveau critère avec validation et sauvegarde automatiques.
   *
   * @param nom le nom du critère
   * @param note la note du critère (entre 0 et 10)
   * @return le critère sauvegardé avec son ID généré
   * @throws IllegalArgumentException si le nom ou la note est invalide
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Critere creerCritere(String nom, Double note) {
    Critere critere = new Critere(nom, note);
    return critereRepository.save(critere);
  }

  /**
   * Récupère un critère existant par son nom, ou en crée un nouveau s'il n'existe pas.
   *
   * <p>
   * Cette méthode évite les duplications de critères en vérifiant d'abord si un critère avec ce nom
   * existe déjà. Si oui, elle met à jour sa note. Sinon, elle en crée un nouveau.
   * </p>
   *
   * @param nom le nom du critère
   * @param note la note du critère (entre 0 et 10)
   * @return le critère existant ou nouvellement créé
   * @throws IllegalArgumentException si le nom ou la note est invalide
   */
  public Critere getOrCreateCritere(String nom, Double note) {
    // Vérifier si le critère existe déjà
    Critere existant = critereRepository.findByNom(nom);

    if (existant != null) {
      // Mettre à jour la note si elle a changé
      if (!existant.getNote().equals(note)) {
        existant.setNote(note);
        return critereRepository.save(existant);
      }
      return existant;
    }

    // Créer un nouveau critère
    return creerCritere(nom, note);
  }

  /**
   * Récupère ou crée une liste de critères à partir de tableaux de noms et notes.
   *
   * <p>
   * Cette méthode est pratique pour créer plusieurs critères en une seule fois. Elle vérifie
   * l'existence de chaque critère et réutilise ceux qui existent déjà.
   * </p>
   *
   * @param noms les noms des critères
   * @param notes les notes correspondantes (doit avoir la même taille que noms)
   * @return la liste des critères créés ou récupérés
   * @throws IllegalArgumentException si les tableaux sont null, vides ou de tailles différentes
   */
  public List<Critere> getOrCreateCriteres(String[] noms, Double[] notes) {
    if (noms == null || notes == null) {
      throw new IllegalArgumentException("Les tableaux de noms et notes ne peuvent pas être null");
    }
    if (noms.length == 0) {
      throw new IllegalArgumentException("Les tableaux ne peuvent pas être vides");
    }
    if (noms.length != notes.length) {
      throw new IllegalArgumentException(
          "Les tableaux de noms et notes doivent avoir la même taille");
    }

    List<Critere> criteres = new ArrayList<>();
    for (int i = 0; i < noms.length; i++) {
      criteres.add(getOrCreateCritere(noms[i], notes[i]));
    }

    return criteres;
  }

  /**
   * Récupère un critère par son identifiant.
   *
   * @param id l'identifiant du critère
   * @return le critère trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public Critere getCritereById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID du critère ne peut pas être null");
    }
    return critereRepository.findById(id);
  }

  /**
   * Récupère un critère par son nom.
   *
   * @param nom le nom du critère
   * @return le critère trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si le nom est null ou vide
   */
  public Critere getCritereByNom(String nom) {
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du critère ne peut pas être vide");
    }
    return critereRepository.findByNom(nom);
  }

  /**
   * Récupère tous les critères de la base de données.
   *
   * @return la liste de tous les critères
   */
  public List<Critere> getAllCriteres() {
    return critereRepository.findAll();
  }

  /**
   * Supprime un critère.
   *
   * @param id l'identifiant du critère à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerCritere(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'ID du critère ne peut pas être null");
    }
    critereRepository.delete(id);
  }

  /**
   * Met à jour un critère existant.
   *
   * @param critere le critère à mettre à jour
   * @return le critère mis à jour
   * @throws IllegalArgumentException si le critère est null ou n'a pas d'ID
   */
  public Critere updateCritere(Critere critere) {
    if (critere == null) {
      throw new IllegalArgumentException("Le critère ne peut pas être null");
    }
    if (critere.getId() == null) {
      throw new IllegalArgumentException("Le critère doit avoir un ID pour être mis à jour");
    }

    return critereRepository.save(critere);
  }

  /**
   * Compte le nombre total de critères dans la base de données.
   *
   * @return le nombre de critères
   */
  public Long countCriteres() {
    return critereRepository.count();
  }
}
