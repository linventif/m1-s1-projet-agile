package fr.univ.m1.projetagile.core.persistence;

import fr.univ.m1.projetagile.core.entity.Loueur;

/**
 * Repository pour gérer la persistance des loueurs
 * Hérite des méthodes communes de UtilisateurRepository
 */
public class LoueurRepository extends UtilisateurRepository<Loueur> {

  public LoueurRepository() {
    super(Loueur.class);
  }
}
