package fr.univ.m1.projetagile.core.service;

import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;

/**
 * Service métier pour la gestion des agents (particuliers et professionnels)
 * Fournit des méthodes pour créer, récupérer et gérer les agents
 */
public class AgentService extends UtilisateurService<Agent, AgentRepository> {

  private VehiculeService vehiculeService;

  public AgentService(AgentRepository agentRepository) {
    super(agentRepository);
  }

  public AgentService(AgentRepository agentRepository, VehiculeService vehiculeService) {
    super(agentRepository);
    this.vehiculeService = vehiculeService;
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

  /**
   * Récupère le profil complet d'un agent sous forme de DTO
   *
   * @param agent l'agent dont on veut récupérer le profil
   * @return AgentDTO contenant toutes les informations du profil et les véhicules disponibles
   */
  public AgentDTO getAgentProfile(Agent agent) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (vehiculeService == null) {
      throw new IllegalStateException(
          "VehiculeService n'est pas initialisé. Utilisez le constructeur avec VehiculeService.");
    }

    AgentDTO dto = new AgentDTO();

    // Informations communes
    dto.setIdU(agent.getIdU());
    dto.setEmail(agent.getEmail());
    dto.setTypeAgent(agent.getTypeAgent());
    dto.setNoteMoyenne(agent.calculerNote());

    // Informations spécifiques selon le type d'agent
    if (agent instanceof AgentParticulier) {
      AgentParticulier particulier = (AgentParticulier) agent;
      dto.setNom(particulier.getNom());
      dto.setPrenom(particulier.getPrenom());
      dto.setTelephone(particulier.getTelephone());
    } else if (agent instanceof AgentProfessionnel) {
      AgentProfessionnel professionnel = (AgentProfessionnel) agent;
      dto.setNom(professionnel.getNom());
      dto.setSiret(professionnel.getSiret());
    }

    // Récupérer les véhicules de l'agent et filtrer uniquement ceux qui sont disponibles
    List<VehiculeDTO> tousLesVehicules = vehiculeService.getVehiculesByAgent(agent);
    List<VehiculeDTO> vehiculesDisponibles =
        tousLesVehicules.stream().filter(VehiculeDTO::isDisponible).collect(Collectors.toList());

    dto.setVehicules(vehiculesDisponibles);

    return dto;
  }
}
