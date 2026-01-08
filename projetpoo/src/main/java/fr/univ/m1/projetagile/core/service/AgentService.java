package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;

/**
 * Service métier pour la gestion des agents (particuliers et professionnels)
 * Fournit des méthodes pour créer, récupérer et gérer les agents
 */
public class AgentService extends UtilisateurService<Agent, AgentRepository> {

  public AgentService(AgentRepository agentRepository) {
    super(agentRepository);
  }

  /**
   * Crée un nouvel agent particulier
   *
   * @param nom le nom de l'agent
   * @param prenom le prénom de l'agent
   * @param email l'email de l'agent
   * @param motDePasse le mot de passe de l'agent
   * @param telephone le numéro de téléphone de l'agent
   * @return l'agent créé et enregistré
   */
  public AgentParticulier createAgentParticulier(String nom, String prenom, String email,
      String motDePasse, String telephone) {

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

    AgentParticulier agent =
        new AgentParticulier(nom, prenom, email, motDePasse, telephone);
    return (AgentParticulier) repository.save(agent);
  }

  /**
   * Crée un nouvel agent professionnel
   *
   * @param email l'email de l'agent
   * @param motDePasse le mot de passe de l'agent
   * @param siret le numéro SIRET de l'entreprise
   * @param nom le nom de l'entreprise
   * @return l'agent créé et enregistré
   */
  public AgentProfessionnel createAgentProfessionnel(String email, String motDePasse, String siret,
      String nom) {

    validateCommonFields(email, motDePasse);

    if (siret == null || siret.trim().isEmpty()) {
      throw new IllegalArgumentException("Le SIRET ne peut pas être vide.");
    }
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'entreprise ne peut pas être vide.");
    }

    // Vérifier que l'email n'est pas déjà utilisé
    if (repository.findByEmail(email) != null) {
      throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
    }

    AgentProfessionnel agent = new AgentProfessionnel(email, motDePasse, siret, nom);
    return (AgentProfessionnel) repository.save(agent);
  }
}
