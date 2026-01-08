package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;

/**
 * Service métier pour la gestion des loueurs
 * Fournit des méthodes pour créer, récupérer et gérer les loueurs
 */
public class LoueurService extends UtilisateurService<Loueur, LoueurRepository> {

  public LoueurService(LoueurRepository loueurRepository) {
    super(loueurRepository);
  }

  /**
   * Crée un nouveau loueur
   *
   * @param nom le nom du loueur
   * @param prenom le prénom du loueur
   * @param email l'email du loueur
   * @param motDePasse le mot de passe du loueur
   * @return le loueur créé et enregistré
   */
  public Loueur createLoueur(String nom, String prenom, String email, String motDePasse) {

    validateCommonFields(email, motDePasse);

    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom ne peut pas être vide.");
    }
    if (prenom == null || prenom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
    }

    // Vérifier que l'email n'est pas déjà utilisé
    if (repository.findByEmail(email) != null) {
      throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
    }

    Loueur loueur = new Loueur(nom, prenom, email, motDePasse);
    return repository.save(loueur);
  }
}
