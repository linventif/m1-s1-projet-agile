package fr.univ.m1.projetagile.parrainage.service;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.parrainage.entity.Parrainage;
import fr.univ.m1.projetagile.parrainage.persistence.ParrainageRepository;

/**
 * Service pour gérer les relations de parrainage entre utilisateurs.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus du repository pour gérer les parrainages. Il
 * s'occupe de la validation des parrainages, de leur création et de la récupération des relations
 * de parrainage.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Création de parrainages avec validation automatique</li>
 * <li>Sauvegarde automatique en base de données</li>
 * <li>Récupération du parrain d'un utilisateur</li>
 * <li>Récupération de tous les parrainés d'un utilisateur</li>
 * <li>Vérification de l'existence de parrainages</li>
 * <li>Suppression de parrainages</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * ParrainageService service = new ParrainageService();
 *
 * // Créer un parrainage (validation et sauvegarde automatiques)
 * Parrainage parrainage = service.parrainer(parrain, parraine);
 *
 * // Récupérer le parrain d'un utilisateur
 * Utilisateur parrain = service.getParrainFromParraineId(parraine.getIdU());
 *
 * // Récupérer tous les parrainés d'un utilisateur
 * List<Utilisateur> parraines = service.getParrainesFromParrainId(parrain.getIdU());
 * }</pre>
 *
 * @see Parrainage
 * @see ParrainageRepository
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 *
 * @author Projet Agile M1
 * @version 1.0
 */
public class ParrainageService {

  private final ParrainageRepository parrainageRepository;

  /**
   * Constructeur par défaut. Initialise le repository de parrainages.
   */
  public ParrainageService() {
    this.parrainageRepository = new ParrainageRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param parrainageRepository le repository de parrainages à utiliser
   */
  public ParrainageService(ParrainageRepository parrainageRepository) {
    this.parrainageRepository = parrainageRepository;
  }

  /**
   * Crée un parrainage entre un utilisateur parrain et un utilisateur parrainé avec validation et
   * sauvegarde automatiques.
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Vérifie que le parrain et le parrainé sont valides</li>
   * <li>Vérifie qu'ils ont des IDs (sont enregistrés en base)</li>
   * <li>Vérifie qu'un utilisateur ne peut pas se parrainer lui-même</li>
   * <li>Vérifie qu'un parrainage n'existe pas déjà entre ces deux utilisateurs</li>
   * <li>Vérifie que le parrainé n'a pas déjà un parrain</li>
   * <li>Crée le parrainage</li>
   * <li>Sauvegarde le parrainage en base de données</li>
   * <li>Retourne le parrainage sauvegardé avec son ID généré</li>
   * </ol>
   *
   * @param parrain l'utilisateur qui parraine (Agent ou Loueur)
   * @param parraine l'utilisateur qui est parrainé (Agent ou Loueur)
   * @return le parrainage sauvegardé avec son ID généré
   * @throws IllegalArgumentException si le parrain ou le parrainé est null
   * @throws IllegalArgumentException si le parrain ou le parrainé n'a pas d'ID
   * @throws IllegalArgumentException si un utilisateur essaie de se parrainer lui-même
   * @throws IllegalStateException si un parrainage existe déjà entre ces deux utilisateurs
   * @throws IllegalStateException si le parrainé a déjà un parrain
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Parrainage parrainer(Utilisateur parrain, Utilisateur parraine) {
    // Validation des paramètres
    if (parrain == null) {
      throw new IllegalArgumentException("Le parrain ne peut pas être null");
    }
    if (parraine == null) {
      throw new IllegalArgumentException("Le parrainé ne peut pas être null");
    }
    if (parrain.getIdU() == null) {
      throw new IllegalArgumentException("Le parrain doit être enregistré en base de données");
    }
    if (parraine.getIdU() == null) {
      throw new IllegalArgumentException("Le parrainé doit être enregistré en base de données");
    }

    // Vérifier qu'un utilisateur ne peut pas se parrainer lui-même
    if (parrain.getIdU().equals(parraine.getIdU())) {
      throw new IllegalArgumentException("Un utilisateur ne peut pas se parrainer lui-même");
    }

    // Vérifier qu'un parrainage n'existe pas déjà entre ces deux utilisateurs
    if (parrainageRepository.existsParrainageBetween(parrain.getIdU(), parraine.getIdU())) {
      throw new IllegalStateException(
          "Un parrainage existe déjà entre ces deux utilisateurs");
    }

    // Vérifier que le parrainé n'a pas déjà un parrain
    if (parrainageRepository.hasParrain(parraine.getIdU())) {
      throw new IllegalStateException("Le parrainé a déjà un parrain");
    }

    // Création du parrainage
    Parrainage parrainage = new Parrainage(parrain, parraine);

    // Sauvegarde automatique
    return parrainageRepository.save(parrainage);
  }

  /**
   * Récupère le parrain d'un utilisateur parrainé à partir de l'ID du parrainé.
   *
   * @param parraineId l'ID de l'utilisateur parrainé
   * @return l'utilisateur parrain, ou null s'il n'a pas de parrain
   * @throws IllegalArgumentException si l'ID est null
   */
  public Utilisateur getParrainFromParraineId(Long parraineId) {
    if (parraineId == null) {
      throw new IllegalArgumentException("L'ID du parrainé ne peut pas être null");
    }

    Parrainage parrainage = parrainageRepository.findByParraineId(parraineId);
    if (parrainage == null) {
      return null;
    }

    return parrainage.getParrain();
  }

  /**
   * Récupère tous les utilisateurs parrainés par un utilisateur à partir de l'ID du parrain.
   *
   * @param parrainId l'ID de l'utilisateur parrain
   * @return la liste des utilisateurs parrainés (peut être vide)
   * @throws IllegalArgumentException si l'ID est null
   */
  public List<Utilisateur> getParrainesFromParrainId(Long parrainId) {
    if (parrainId == null) {
      throw new IllegalArgumentException("L'ID du parrain ne peut pas être null");
    }

    List<Parrainage> parrainages = parrainageRepository.findByParrainId(parrainId);
    List<Utilisateur> parraines = new ArrayList<>();

    for (Parrainage parrainage : parrainages) {
      Utilisateur parraine = parrainage.getParraine();
      if (parraine != null) {
        parraines.add(parraine);
      }
    }

    return parraines;
  }

  /**
   * Récupère un parrainage par son identifiant.
   *
   * @param parrainageId l'identifiant du parrainage
   * @return le parrainage trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public Parrainage getParrainageById(Long parrainageId) {
    if (parrainageId == null) {
      throw new IllegalArgumentException("L'ID du parrainage ne peut pas être null");
    }
    return parrainageRepository.findById(parrainageId);
  }

  /**
   * Récupère tous les parrainages de la base de données.
   *
   * @return la liste de tous les parrainages
   */
  public List<Parrainage> getAllParrainages() {
    return parrainageRepository.findAll();
  }

  /**
   * Vérifie si un utilisateur a un parrain.
   *
   * @param utilisateurId l'ID de l'utilisateur à vérifier
   * @return true si l'utilisateur a un parrain, false sinon
   * @throws IllegalArgumentException si l'ID est null
   */
  public boolean hasParrain(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    return parrainageRepository.hasParrain(utilisateurId);
  }

  /**
   * Vérifie si un parrainage existe entre deux utilisateurs.
   *
   * @param user1Id l'ID du premier utilisateur
   * @param user2Id l'ID du deuxième utilisateur
   * @return true si un parrainage existe entre ces deux utilisateurs, false sinon
   * @throws IllegalArgumentException si l'un des IDs est null
   */
  public boolean existsParrainageBetween(Long user1Id, Long user2Id) {
    if (user1Id == null || user2Id == null) {
      throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent pas être null");
    }
    return parrainageRepository.existsParrainageBetween(user1Id, user2Id);
  }

  /**
   * Compte le nombre de parrainés d'un utilisateur.
   *
   * @param parrainId l'ID de l'utilisateur parrain
   * @return le nombre de parrainés
   * @throws IllegalArgumentException si l'ID est null
   */
  public int compterParraines(Long parrainId) {
    if (parrainId == null) {
      throw new IllegalArgumentException("L'ID du parrain ne peut pas être null");
    }
    return getParrainesFromParrainId(parrainId).size();
  }

  /**
   * Supprime un parrainage.
   *
   * <p>
   * Note : Cette méthode supprime définitivement le parrainage. Il serait préférable d'implémenter
   * un système de "soft delete" pour permettre la récupération.
   * </p>
   *
   * @param parrainageId l'identifiant du parrainage à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerParrainage(Long parrainageId) {
    if (parrainageId == null) {
      throw new IllegalArgumentException("L'ID du parrainage ne peut pas être null");
    }
    parrainageRepository.delete(parrainageId);
  }

  /**
   * Récupère le parrainage entre un parrain et un parrainé spécifiques.
   *
   * @param parrainId l'ID du parrain
   * @param parraineId l'ID du parrainé
   * @return le parrainage trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si l'un des IDs est null
   */
  public Parrainage getParrainageBetween(Long parrainId, Long parraineId) {
    if (parrainId == null || parraineId == null) {
      throw new IllegalArgumentException("Les IDs ne peuvent pas être null");
    }

    // Chercher le parrainage où parrainId est le parrain et parraineId est le parrainé
    Parrainage parrainage = parrainageRepository.findByParraineId(parraineId);
    if (parrainage != null && parrainage.getParrainId().equals(parrainId)) {
      return parrainage;
    }

    return null;
  }
}
