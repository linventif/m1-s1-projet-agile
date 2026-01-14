package fr.univ.m1.projetagile.core.service;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.commentaire.service.CommentaireService;
import fr.univ.m1.projetagile.core.dto.ProfilInfo;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.UtilisateurRepository;
import jakarta.persistence.EntityManager;

/**
 * Service de base pour gérer les opérations métier communes aux utilisateurs Cette classe fournit
 * des méthodes réutilisables pour Agent et Loueur
 *
 * @param <T> le type d'utilisateur (Agent ou Loueur)
 * @param <R> le type de repository correspondant
 */
public abstract class UtilisateurService<T extends Utilisateur, R extends UtilisateurRepository<T>> {

  protected R repository;

  protected UtilisateurService(R repository) {
    this.repository = repository;
  }

  /**
   * Récupère un utilisateur par son ID
   *
   * @param id l'identifiant de l'utilisateur
   * @return l'utilisateur trouvé ou null
   */
  public T findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant ne peut pas être nul.");
    }
    return repository.findById(id);
  }

  /**
   * Récupère un utilisateur par son email
   *
   * @param email l'email de l'utilisateur
   * @return l'utilisateur trouvé ou null
   */
  public T findByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("L'email ne peut pas être vide.");
    }
    return repository.findByEmail(email);
  }

  /**
   * Supprime un utilisateur
   *
   * @param id l'identifiant de l'utilisateur à supprimer
   */
  public void deleteUser(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'identifiant ne peut pas être nul.");
    }
    repository.delete(id);
  }

  /**
   * Sauvegarde ou met à jour un utilisateur
   *
   * @param utilisateur l'utilisateur à sauvegarder
   * @return l'utilisateur sauvegardé
   */
  public T save(T utilisateur) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être nul.");
    }
    return repository.save(utilisateur);
  }



  /**
   * Valide le format d'un email
   *
   * @param email l'email à valider
   * @throws IllegalArgumentException si l'email est invalide
   */
  protected void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("L'email ne peut pas être vide.");
    }

    // Regex pour valider le format email
    // Format accepté: utilisateur@domaine.extension
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    if (!email.matches(emailRegex)) {
      throw new IllegalArgumentException(
          "Le format de l'email est invalide. Format attendu: utilisateur@domaine.extension");
    }
  }

  /**
   * Valide les informations communes à tous les utilisateurs
   *
   * @param email l'email de l'utilisateur
   * @param motDePasse le mot de passe de l'utilisateur
   */
  protected void validateCommonFields(String email, String motDePasse) {
    validateEmail(email);

    if (motDePasse == null || motDePasse.trim().isEmpty()) {
      throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
    }
  }

  /**
   * Connecte un utilisateur en vérifiant ses identifiants
   *
   * @param email l'email de l'utilisateur
   * @param motDePasse le mot de passe de l'utilisateur
   * @return l'utilisateur connecté (Agent ou Loueur selon le service)
   * @throws IllegalArgumentException si les identifiants sont invalides ou incorrects
   */
  public T connect(String email, String motDePasse) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("L'email ne peut pas être vide.");
    }
    if (motDePasse == null || motDePasse.trim().isEmpty()) {
      throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
    }

    if (!repository.verifyPassword(email, motDePasse)) {
      throw new IllegalArgumentException("Email ou mot de passe incorrect.");
    }

    return repository.findByEmail(email);
  }

  /**
   * Change le mot de passe d'un utilisateur
   *
   * @param utilisateur l'utilisateur dont on veut changer le mot de passe
   * @param ancienMotDePasse l'ancien mot de passe pour vérification
   * @param nouveauMotDePasse le nouveau mot de passe
   * @return l'utilisateur avec le mot de passe modifié
   * @throws IllegalArgumentException si les paramètres sont invalides ou si l'ancien mot de passe
   *         est incorrect
   */
  public T changePassword(T utilisateur, String ancienMotDePasse, String nouveauMotDePasse) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être nul.");
    }
    if (ancienMotDePasse == null || ancienMotDePasse.trim().isEmpty()) {
      throw new IllegalArgumentException("L'ancien mot de passe ne peut pas être vide.");
    }
    if (nouveauMotDePasse == null || nouveauMotDePasse.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide.");
    }

    // Utilise la méthode changerMDP de la classe Utilisateur qui vérifie l'ancien mot de passe
    utilisateur.changerMDP(ancienMotDePasse, nouveauMotDePasse);

    // Sauvegarde les modifications en base de données
    return repository.save(utilisateur);
  }

  /**
   * Change l'email d'un utilisateur
   *
   * @param utilisateur l'utilisateur dont on veut changer l'email
   * @param nouvelEmail le nouvel email
   * @return l'utilisateur avec l'email modifié
   * @throws IllegalArgumentException si les paramètres sont invalides ou si l'email est déjà
   *         utilisé
   */
  public T changeEmail(T utilisateur, String nouvelEmail) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être nul.");
    }

    validateEmail(nouvelEmail);

    // Vérifier que le nouvel email n'est pas déjà utilisé par un autre utilisateur
    T existingUser = repository.findByEmail(nouvelEmail);
    if (existingUser != null && !existingUser.getIdU().equals(utilisateur.getIdU())) {
      throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
    }

    // Utilise la méthode changerEmail de la classe Utilisateur
    utilisateur.changerEmail(nouvelEmail);

    // Sauvegarde les modifications en base de données
    return repository.save(utilisateur);
  }

  /**
   * Récupère les informations du profil d'un utilisateur
   *
   * @param utilisateur l'utilisateur dont on veut récupérer le profil
   * @param em l'EntityManager pour les requêtes
   * @return les informations du profil
   * @throws IllegalArgumentException si l'utilisateur est null
   */
  public ProfilInfo getProfil(T utilisateur, EntityManager em) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être nul.");
    }

    ProfilInfo profil = new ProfilInfo();
    profil.setIdUtilisateur(utilisateur.getIdU());
    profil.setNom(utilisateur.getNom());
    profil.setPrenom(utilisateur.getPrenom());
    profil.setEmail(utilisateur.getEmail());
    profil.setAdresse(utilisateur.getAdresse());
    profil.setBio(utilisateur.getBio());

    // Récupérer le nom commercial si c'est un Agent
    if (utilisateur instanceof Agent) {
      Agent agent = (Agent) utilisateur;
      profil.setNomCommercial(agent.getNomCommercial());

      // Récupérer les véhicules disponibles via une requête JPQL pour éviter
      // LazyInitializationException
      if (em != null) {
        try {
          List<Vehicule> vehiculesDisponibles = em.createQuery(
              "SELECT v FROM Vehicule v WHERE v.proprietaire.idU = :agentId AND v.disponible = true",
              Vehicule.class).setParameter("agentId", agent.getIdU()).getResultList();
          profil.setVehiculesDisponibles(vehiculesDisponibles);
        } catch (Exception e) {
          profil.setVehiculesDisponibles(new ArrayList<>());
        }
      }
    }

    // Récupérer les commentaires
    if (em != null) {
      CommentaireService commentaireService = new CommentaireService(em);
      profil.setCommentaires(commentaireService.getCommentairesProfil(utilisateur.getIdU()));
      profil.setMoyenneNotes(commentaireService.getMoyenneNotes(utilisateur.getIdU()));
      profil.setNombreCommentaires(commentaireService.countCommentaires(utilisateur.getIdU()));
    }

    return profil;
  }

  /**
   * Modifie les informations du profil d'un utilisateur
   *
   * @param utilisateur l'utilisateur dont on veut modifier le profil
   * @param nom le nouveau nom (optionnel)
   * @param prenom le nouveau prénom (optionnel)
   * @param adresse la nouvelle adresse (optionnel)
   * @param bio la nouvelle bio (optionnel)
   * @return l'utilisateur modifié et sauvegardé
   * @throws IllegalArgumentException si l'utilisateur est null
   */
  public T modifierProfil(T utilisateur, String nom, String prenom, String adresse, String bio) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être nul.");
    }

    if (nom != null && !nom.trim().isEmpty()) {
      utilisateur.setNom(nom);
    }
    if (prenom != null && !prenom.trim().isEmpty()) {
      utilisateur.setPrenom(prenom);
    }
    if (adresse != null) {
      utilisateur.setAdresse(adresse);
    }
    if (bio != null) {
      utilisateur.setBio(bio);
    }

    // Sauvegarde les modifications en base de données
    return repository.save(utilisateur);
  }
}
