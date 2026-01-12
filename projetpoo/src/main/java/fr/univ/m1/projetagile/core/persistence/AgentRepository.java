package fr.univ.m1.projetagile.core.persistence;

import fr.univ.m1.projetagile.core.entity.Agent;

/**
 * Repository pour gérer la persistance des agents (particuliers et professionnels)
 * Hérite des méthodes communes de UtilisateurRepository
 */
public class AgentRepository extends UtilisateurRepository<Agent> {

  public AgentRepository() {
    super(Agent.class);
  }
}
