package fr.univ.m1.projetagile.parrainage.service;

import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.parrainage.entity.Crédit;
import fr.univ.m1.projetagile.parrainage.persistence.CreditRepository;

/**
 * Service pour gérer les crédits des utilisateurs.
 *
 * <p>
 * Ce service fournit une couche métier au-dessus du repository pour gérer les crédits. Il s'occupe
 * de la création de soldes, de la gestion des entrées et sorties de crédit, et de la validation des
 * opérations.
 * </p>
 *
 * <h2>Fonctionnalités</h2>
 * <ul>
 * <li>Création d'un solde initial pour un utilisateur</li>
 * <li>Ajout de crédit (entrées)</li>
 * <li>Retrait de crédit (sorties)</li>
 * <li>Vérification du solde disponible</li>
 * <li>Récupération du crédit d'un utilisateur</li>
 * <li>Suppression de crédit</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 *
 * <pre>{@code
 * CreditService service = new CreditService();
 *
 * // Créer un solde initial pour un utilisateur
 * Crédit credit = service.creerSolde(utilisateur, 100.0);
 *
 * // Ajouter du crédit
 * service.ajouterCredit(utilisateur.getIdU(), 50.0);
 *
 * // Retirer du crédit
 * service.retirerCredit(utilisateur.getIdU(), 30.0);
 *
 * // Vérifier le solde
 * Double solde = service.getSolde(utilisateur.getIdU());
 * }</pre>
 *
 * @see Crédit
 * @see CreditRepository
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 *
 * @author Projet Agile M1
 * @version 1.0
 */
public class CreditService {

  private final CreditRepository creditRepository;

  /**
   * Constructeur par défaut. Initialise le repository de crédits.
   */
  public CreditService() {
    this.creditRepository = new CreditRepository();
  }

  /**
   * Constructeur avec injection de dépendance (pour les tests).
   *
   * @param creditRepository le repository de crédits à utiliser
   */
  public CreditService(CreditRepository creditRepository) {
    this.creditRepository = creditRepository;
  }

  /**
   * Crée un solde initial pour un utilisateur avec un montant donné.
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Vérifie que l'utilisateur est valide</li>
   * <li>Vérifie que l'utilisateur n'a pas déjà un crédit</li>
   * <li>Crée un nouveau crédit avec le montant initial</li>
   * <li>Sauvegarde le crédit en base de données</li>
   * <li>Retourne le crédit créé avec son ID généré</li>
   * </ol>
   *
   * @param utilisateur l'utilisateur pour lequel créer le solde
   * @param montantInitial le montant initial du crédit (doit être positif ou zéro)
   * @return le crédit créé et sauvegardé avec son ID généré
   * @throws IllegalArgumentException si l'utilisateur est null ou n'a pas d'ID
   * @throws IllegalArgumentException si le montant initial est null ou négatif
   * @throws IllegalStateException si l'utilisateur a déjà un crédit
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Crédit creerSolde(Utilisateur utilisateur, Double montantInitial) {
    // Validation des paramètres
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    if (utilisateur.getIdU() == null) {
      throw new IllegalArgumentException("L'utilisateur doit être enregistré en base de données");
    }
    if (montantInitial == null) {
      throw new IllegalArgumentException("Le montant initial ne peut pas être null");
    }
    if (montantInitial < 0) {
      throw new IllegalArgumentException("Le montant initial ne peut pas être négatif");
    }

    // Vérifier que l'utilisateur n'a pas déjà un crédit
    if (creditRepository.hasCredit(utilisateur.getIdU())) {
      throw new IllegalStateException("L'utilisateur a déjà un crédit enregistré");
    }

    // Création du crédit
    Crédit credit = new Crédit(utilisateur, montantInitial);

    // Sauvegarde automatique
    return creditRepository.save(credit);
  }

  /**
   * Ajoute du crédit au solde d'un utilisateur (entrée).
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Récupère le crédit de l'utilisateur</li>
   * <li>Ajoute le montant au crédit existant</li>
   * <li>Sauvegarde les modifications</li>
   * </ol>
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @param montant le montant à ajouter (doit être positif)
   * @return le crédit mis à jour
   * @throws IllegalArgumentException si l'ID ou le montant est null
   * @throws IllegalArgumentException si le montant est négatif
   * @throws IllegalStateException si l'utilisateur n'a pas de crédit (doit créer un solde d'abord)
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Crédit ajouterCredit(Long utilisateurId, Double montant) {
    // Validation des paramètres
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    if (montant == null) {
      throw new IllegalArgumentException("Le montant ne peut pas être null");
    }
    if (montant <= 0) {
      throw new IllegalArgumentException(
          "Le montant à ajouter doit être strictement positif (supérieur à zéro)");
    }

    // Récupérer le crédit de l'utilisateur
    Crédit credit = creditRepository.findByUtilisateurId(utilisateurId);
    if (credit == null) {
      throw new IllegalStateException(
          "L'utilisateur n'a pas de crédit. Veuillez créer un solde d'abord.");
    }

    // Ajouter le montant
    credit.ajouterCredit(montant);

    // Sauvegarder les modifications
    return creditRepository.save(credit);
  }

  /**
   * Retire du crédit du solde d'un utilisateur (sortie).
   *
   * <p>
   * Cette méthode :
   * </p>
   * <ol>
   * <li>Récupère le crédit de l'utilisateur</li>
   * <li>Vérifie que le crédit est suffisant</li>
   * <li>Retire le montant du crédit existant</li>
   * <li>Sauvegarde les modifications</li>
   * </ol>
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @param montant le montant à retirer (doit être positif)
   * @return le crédit mis à jour
   * @throws IllegalArgumentException si l'ID ou le montant est null
   * @throws IllegalArgumentException si le montant est négatif
   * @throws IllegalStateException si l'utilisateur n'a pas de crédit (doit créer un solde d'abord)
   * @throws IllegalStateException si le crédit est insuffisant
   * @throws RuntimeException si une erreur survient lors de la sauvegarde
   */
  public Crédit retirerCredit(Long utilisateurId, Double montant) {
    // Validation des paramètres
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    if (montant == null) {
      throw new IllegalArgumentException("Le montant ne peut pas être null");
    }
    if (montant < 0) {
      throw new IllegalArgumentException("Le montant à retirer ne peut pas être négatif");
    }

    // Récupérer le crédit de l'utilisateur
    Crédit credit = creditRepository.findByUtilisateurId(utilisateurId);
    if (credit == null) {
      throw new IllegalStateException(
          "L'utilisateur n'a pas de crédit. Veuillez créer un solde d'abord.");
    }

    // Retirer le montant (la méthode retirerCredit vérifie aussi le solde suffisant)
    credit.retirerCredit(montant);

    // Sauvegarder les modifications
    return creditRepository.save(credit);
  }

  /**
   * Récupère le solde (montant) du crédit d'un utilisateur.
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @return le montant du crédit, ou null si l'utilisateur n'a pas de crédit
   * @throws IllegalArgumentException si l'ID est null
   */
  public Double getSolde(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }

    Crédit credit = creditRepository.findByUtilisateurId(utilisateurId);
    if (credit == null) {
      return null;
    }

    return credit.getCredit();
  }

  /**
   * Récupère le crédit complet d'un utilisateur.
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @return le crédit trouvé, ou null si l'utilisateur n'a pas de crédit
   * @throws IllegalArgumentException si l'ID est null
   */
  public Crédit getCredit(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }

    return creditRepository.findByUtilisateurId(utilisateurId);
  }

  /**
   * Récupère un crédit par son identifiant.
   *
   * @param creditId l'identifiant du crédit
   * @return le crédit trouvé, ou null s'il n'existe pas
   * @throws IllegalArgumentException si l'ID est null
   */
  public Crédit getCreditById(Long creditId) {
    if (creditId == null) {
      throw new IllegalArgumentException("L'ID du crédit ne peut pas être null");
    }
    return creditRepository.findById(creditId);
  }

  /**
   * Vérifie si un utilisateur a un crédit enregistré.
   *
   * @param utilisateurId l'ID de l'utilisateur à vérifier
   * @return true si l'utilisateur a un crédit, false sinon
   * @throws IllegalArgumentException si l'ID est null
   */
  public boolean hasCredit(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    return creditRepository.hasCredit(utilisateurId);
  }

  /**
   * Vérifie si le crédit d'un utilisateur est suffisant pour un montant donné.
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @param montant le montant à vérifier
   * @return true si le crédit est suffisant, false sinon (ou si l'utilisateur n'a pas de crédit)
   * @throws IllegalArgumentException si l'ID ou le montant est null
   */
  public boolean creditSuffisant(Long utilisateurId, Double montant) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    if (montant == null) {
      throw new IllegalArgumentException("Le montant ne peut pas être null");
    }

    Crédit credit = creditRepository.findByUtilisateurId(utilisateurId);
    if (credit == null) {
      return false;
    }

    return credit.creditSuffisant(montant);
  }

  /**
   * Récupère tous les crédits de la base de données.
   *
   * @return la liste de tous les crédits
   */
  public java.util.List<Crédit> getAllCredits() {
    return creditRepository.findAll();
  }

  /**
   * Supprime le crédit d'un utilisateur.
   *
   * <p>
   * Note : Cette méthode supprime définitivement le crédit. Il serait préférable d'implémenter un
   * système de "soft delete" pour permettre la récupération.
   * </p>
   *
   * @param utilisateurId l'ID de l'utilisateur dont le crédit doit être supprimé
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerCredit(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    creditRepository.deleteByUtilisateurId(utilisateurId);
  }

  /**
   * Supprime un crédit par son identifiant.
   *
   * @param creditId l'identifiant du crédit à supprimer
   * @throws IllegalArgumentException si l'ID est null
   */
  public void supprimerCreditById(Long creditId) {
    if (creditId == null) {
      throw new IllegalArgumentException("L'ID du crédit ne peut pas être null");
    }
    creditRepository.delete(creditId);
  }
}
