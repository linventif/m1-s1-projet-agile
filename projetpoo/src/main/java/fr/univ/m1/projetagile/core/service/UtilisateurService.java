package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.core.persistence.UtilisateurRepository;

/**
 * Service de base pour gérer les opérations métier communes aux utilisateurs
 * Cette classe fournit des méthodes réutilisables pour Agent et Loueur
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
   * Valide les informations communes à tous les utilisateurs
   *
   * @param email l'email de l'utilisateur
   * @param motDePasse le mot de passe de l'utilisateur
   */
  protected void validateCommonFields(String email, String motDePasse) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("L'email ne peut pas être vide.");
    }
    if (motDePasse == null || motDePasse.trim().isEmpty()) {
      throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("L'email doit être valide.");
    }
  }
}
