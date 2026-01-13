package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.ControleTechnique;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;

/**
 * Service métier pour la gestion des agents (particuliers et professionnels) Fournit des méthodes
 * pour créer, récupérer et gérer les agents
 */
public class AgentService extends UtilisateurService<Agent, AgentRepository> {

  private final VehiculeService vehiculeService;
  private final ControlTechniqueService controlTechniqueService;


  public AgentService(AgentRepository agentRepository, VehiculeRepository vehiculeRepository) {
    super(agentRepository);

    if (vehiculeRepository == null) {
      throw new IllegalArgumentException("VehiculeRepository ne peut pas être nul.");
    }

    this.vehiculeService = new VehiculeService(vehiculeRepository);
    this.controlTechniqueService = new ControlTechniqueService(vehiculeRepository);
  }

  public AgentService(AgentRepository agentRepository) {
    this(agentRepository, new VehiculeRepository());
  }

  /**
   * Crée un nouvel agent particulier
   *
   * @param nom le nom de l'agent
   * @param prenom le prénom de l'agent
   * @param email l'email de l'agent
   * @param motDePasse le mot de passe de l'agent
   * @return l'agent créé et enregistré
   */
  public AgentParticulier createAgentParticulier(String nom, String prenom, String email,
      String motDePasse) {

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

    AgentParticulier agent = new AgentParticulier(nom, prenom, email, motDePasse);
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

  /**
   * Modifie le nom d'un agent particulier
   *
   * @param agent l'agent particulier à modifier
   * @param nouveauNom le nouveau nom
   * @return l'agent modifié
   */
  public AgentParticulier updateAgentParticulierNom(AgentParticulier agent, String nouveauNom) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom ne peut pas être vide.");
    }

    agent.setNom(nouveauNom);
    return (AgentParticulier) repository.save(agent);
  }

  /**
   * Modifie le prénom d'un agent particulier
   *
   * @param agent l'agent particulier à modifier
   * @param nouveauPrenom le nouveau prénom
   * @return l'agent modifié
   */
  public AgentParticulier updateAgentParticulierPrenom(AgentParticulier agent,
      String nouveauPrenom) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (nouveauPrenom == null || nouveauPrenom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
    }

    agent.setPrenom(nouveauPrenom);
    return (AgentParticulier) repository.save(agent);
  }

  /**
   * Modifie le nom d'un agent professionnel
   *
   * @param agent l'agent professionnel à modifier
   * @param nouveauNom le nouveau nom de l'entreprise
   * @return l'agent modifié
   */
  public AgentProfessionnel updateAgentProfessionnelNom(AgentProfessionnel agent,
      String nouveauNom) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'entreprise ne peut pas être vide.");
    }

    agent.setNom(nouveauNom);
    return (AgentProfessionnel) repository.save(agent);
  }

  /**
   * Modifie le SIRET d'un agent professionnel
   *
   * @param agent l'agent professionnel à modifier
   * @param nouveauSiret le nouveau numéro SIRET
   * @return l'agent modifié
   */
  public AgentProfessionnel updateAgentProfessionnelSiret(AgentProfessionnel agent,
      String nouveauSiret) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être nul.");
    }
    if (nouveauSiret == null || nouveauSiret.trim().isEmpty()) {
      throw new IllegalArgumentException("Le SIRET ne peut pas être vide.");
    }

    agent.setSiret(nouveauSiret);
    return (AgentProfessionnel) repository.save(agent);
  }

  // ==================== 技术检查相关方法 ====================

  /**
   * Correspond au US.A.9：reminder le contrôle technique pour tous les véhicules d'un agent
   *
   * @param agentId
   * @return
   */
  public String verifierControlesTechniquesAgent(int agentId) {

    Agent agent = findById(agentId);
    if (agent == null) {
      return "❌ Agent non trouvé";
    }


    List<Vehicule> vehicules = getVehiculesEntityByAgent(agent);
    if (vehicules == null || vehicules.isEmpty()) {
      return " Cet agent n'a aucun véhicule";
    }

    return controlTechniqueService.genererRapportPourAgent(vehicules);
  }

  /**
   *
   *
   * @param vehiculeId
   * @return 车
   */
  public String getInfoControleVehicule(Long vehiculeId) {
    // Obtenir le véhicule
    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return "❌ Véhicule non trouvé";
    }

    return controlTechniqueService.genererRapportControle(vehicule);
  }

  /**
   *
   *
   * @param vehiculeId
   * @return
   */
  public String verifierControleVehicule(Long vehiculeId) {

    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return "❌ Véhicule non trouvé";
    }

    boolean doitControle = controlTechniqueService.doitFaireControleProchainement(vehicule);
    String statut = controlTechniqueService.getStatutControleDetaille(vehicule);

    StringBuilder resultat = new StringBuilder();
    resultat.append("🔍 Vérification du contrôle technique\n");
    resultat.append("Véhicule: ").append(vehicule.getMarque()).append(" ")
        .append(vehicule.getModele()).append("\n");
    resultat.append("Résultat: ");

    if (doitControle) {
      resultat.append("⚠️ Il faut faire un contrôle technique\n");
    } else {
      resultat.append("✅ Pas besoin de contrôle technique pour le moment\n");
    }

    resultat.append("Détails: ").append(statut);

    return resultat.toString();
  }

  /**
   * obtenir la liste des véhicules nécessitant un contrôle technique urgent
   *
   * @param agentId
   * @return
   */
  public List<Vehicule> getVehiculesUrgents(int agentId) {
    Agent agent = findById(agentId);
    if (agent == null) {
      return new ArrayList<>();
    }

    List<Vehicule> vehicules = getVehiculesEntityByAgent(agent);
    List<Vehicule> vehiculesUrgents = new ArrayList<>();

    if (vehicules == null || vehicules.isEmpty()) {
      return vehiculesUrgents;
    }

    for (Vehicule vehicule : vehicules) {
      if (controlTechniqueService.doitFaireControleProchainement(vehicule)) {
        vehiculesUrgents.add(vehicule);
      }
    }

    return vehiculesUrgents;
  }

  /**
   * obtenir la liste des véhicules nécessitant un contrôle technique urgent DTO
   *
   * @param agentId
   * @return
   */
  public List<VehiculeDTO> getVehiculesUrgentsDTO(int agentId) {
    List<Vehicule> vehiculesUrgents = getVehiculesUrgents(agentId);
    List<VehiculeDTO> dtos = new ArrayList<>();

    for (Vehicule vehicule : vehiculesUrgents) {
      dtos.add(convertToDTO(vehicule));
    }

    return dtos;
  }

  /**
   * correspond au US.A.8
   *
   * @param vehiculeId
   * @param dateControle
   * @param kilometrage
   * @param resultat
   * @param commentaires
   * @return
   */
  public String enregistrerControleTechnique(Long vehiculeId, LocalDate dateControle,
      Integer kilometrage, String resultat, String commentaires) {
    try {
      controlTechniqueService.enregistrerNouveauControle(vehiculeId, dateControle, kilometrage,
          resultat, commentaires);


      Vehicule vehicule = findVehiculeById(vehiculeId);
      if (vehicule == null) {
        return "❌ Véhicule non trouvé après l'enregistrement";
      }

      ControleTechnique ct = controlTechniqueService.getControleTechniqueByVehiculeId(vehiculeId);
      LocalDate dateProchainControle = ct != null ? ct.getDateLimite() : null;

      return String.format(
          "✅ Contrôle technique enregistré pour %s %s\n" + "Date: %s\n" + "Prochain contrôle: %s",
          vehicule.getMarque(), vehicule.getModele(), dateControle, dateProchainControle);
    } catch (Exception e) {
      return "❌ Erreur lors de l'enregistrement: " + e.getMessage();
    }
  }

  /**
   * obtenir le statut détaillé du contrôle technique d'un véhicule
   *
   * @param vehiculeId
   * @return
   */
  public String getStatutControleVehicule(Long vehiculeId) {
    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return "❌ Véhicule non trouvé";
    }

    return controlTechniqueService.getStatutControleDetaille(vehicule);
  }

  /**
   * calculer la date du prochain contrôle technique Correspond au US.A.10：calcular la fecha del
   * próximo control técnico
   *
   * @param vehiculeId
   * @return
   */
  public LocalDate calculerDateProchainControle(Long vehiculeId) {
    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return null;
    }

    return controlTechniqueService.calculerDateProchainControle(vehicule);
  }

  /**
   * verifier si un véhicule doit faire un contrôle technique prochainement
   *
   * @param vehiculeId
   * @return
   */
  public boolean doitFaireControleProchainement(Long vehiculeId) {
    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return false;
    }

    return controlTechniqueService.doitFaireControleProchainement(vehicule);
  }

  /**
   * correspond au US.A.11
   *
   * @param vehiculeId
   * @return
   */
  public List<String> getRecommandationsEntretien(Long vehiculeId) {
    Vehicule vehicule = findVehiculeById(vehiculeId);

    if (vehicule == null) {
      return new ArrayList<>();
    }

    return controlTechniqueService.getRecommandationsEntretienParKilometrage(vehicule);
  }

  // ==================== methode ====================

  /**
   * obtenir la liste des véhicules d'un agent en tant qu'entités
   */
  private List<Vehicule> getVehiculesEntityByAgent(Agent agent) {

    List<VehiculeDTO> vehiculeDTOs = vehiculeService.getVehiculesByAgent(agent);


    List<Vehicule> vehicules = new ArrayList<>();
    if (vehiculeDTOs != null) {
      for (VehiculeDTO dto : vehiculeDTOs) {
        Vehicule vehicule = convertDtoToEntity(dto);
        if (vehicule != null) {
          vehicules.add(vehicule);
        }
      }
    }

    return vehicules;
  }



  private VehiculeDTO convertToDTO(Vehicule vehicule) {
    if (vehicule == null) {
      return null;
    }

    VehiculeDTO dto = new VehiculeDTO();

    dto.setId(vehicule.getId());

    dto.setType(vehicule.getType());
    dto.setMarque(vehicule.getMarque());
    dto.setModele(vehicule.getModele());
    dto.setCouleur(vehicule.getCouleur());
    dto.setVille(vehicule.getVille());
    dto.setPrixJ(vehicule.getPrixJ());

    // Get technical control data via service
    ControleTechnique ct =
        controlTechniqueService.getControleTechniqueByVehiculeId(vehicule.getId());
    if (ct != null) {
      dto.setDateMiseEnCirculation(ct.getDateMiseEnCirculation());
      dto.setDateDernierControle(ct.getDate());
      dto.setKilometrageActuel(ct.getKilometrageActuel());
      dto.setKilometrageDernierControle(ct.getKilometrageDernierControle());
      dto.setDateProchainControle(ct.getDateLimite());
      dto.setDateDernierEntretien(ct.getDateDernierEntretien());
    }
    dto.setDisponible(vehicule.isDisponible());

    return dto;
  }

  private Vehicule convertDtoToEntity(VehiculeDTO dto) {
    if (dto == null) {
      return null;
    }

    Vehicule vehicule = new Vehicule(dto.getType(), dto.getMarque(), dto.getModele(),
        dto.getCouleur(), dto.getVille(), dto.getPrixJ(), null);

    vehicule.setId(dto.getId());
    vehicule.setType(dto.getType());
    vehicule.setMarque(dto.getMarque());
    vehicule.setModele(dto.getModele());
    vehicule.setCouleur(dto.getCouleur());
    vehicule.setVille(dto.getVille());
    vehicule.setPrixJ(dto.getPrixJ());
    vehicule.setDisponible(dto.isDisponible());

    // Set control technique fields via service if vehicule has an ID
    if (dto.getId() != null) {
      controlTechniqueService.updateControleTechnique(dto.getId(), dto.getDateMiseEnCirculation(),
          dto.getDateDernierControle(), dto.getKilometrageActuel(),
          dto.getKilometrageDernierControle(), dto.getDateProchainControle(),
          dto.getDateDernierEntretien(), null);
    }

    return vehicule;
  }

  /**
   * consulter un agent par son ID
   */
  public Agent findById(int agentId) {
    return super.findById((long) agentId);
  }

  /**
   * consulter un véhicule par son ID
   */
  public Vehicule findVehiculeById(Long vehiculeId) {
    if (vehiculeId == null) {
      return null;
    }
    return vehiculeService.findById(vehiculeId);
  }

  /**
   * obtenir le rapport de contrôle technique pour tous les agents
   *
   * @return
   */
  public String genererRapportTousAgents(List<Integer> agentIds) {

    if (agentIds == null || agentIds.isEmpty()) {
      return "Aucun agent trouvé";
    }

    StringBuilder rapport = new StringBuilder();
    rapport.append("📊 RAPPORT CONTRÔLE TECHNIQUE - TOUS LES AGENTS\n");
    rapport.append("═".repeat(70)).append("\n");
    rapport.append("Nombre total d'agents: ").append(agentIds.size()).append("\n\n");

    int totalVehicules = 0;
    int totalUrgents = 0;

    for (Integer agentId : agentIds) {
      Agent agent = findById(agentId);
      if (agent == null) {
        rapport.append("❌ Agent ID ").append(agentId).append(" non trouvé\n\n");
        continue;
      }
      List<Vehicule> vehicules = getVehiculesEntityByAgent(agent);
      int vehiculesCount = vehicules != null ? vehicules.size() : 0;
      totalVehicules += vehiculesCount;

      rapport.append("👤 Agent: ");
      if (agent instanceof AgentParticulier) {
        AgentParticulier particulier = (AgentParticulier) agent;
        rapport.append(particulier.getPrenom()).append(" ").append(particulier.getNom());
      } else if (agent instanceof AgentProfessionnel) {
        AgentProfessionnel pro = (AgentProfessionnel) agent;
        rapport.append(pro.getNom()).append(" (Entreprise)");
      }
      rapport.append("\n");

      rapport.append("   Email: ").append(agent.getEmail()).append("\n");
      rapport.append("   Véhicules: ").append(vehiculesCount).append("\n");

      if (vehiculesCount > 0) {
        List<Vehicule> urgents = new ArrayList<>();
        for (Vehicule v : vehicules) {
          if (controlTechniqueService.doitFaireControleProchainement(v)) {
            urgents.add(v);
          }
        }

        totalUrgents += urgents.size();
        rapport.append("   Contrôles urgents: ").append(urgents.size()).append("\n");

        if (!urgents.isEmpty()) {
          for (Vehicule urgent : urgents) {
            rapport.append("     - ").append(urgent.getMarque()).append(" ")
                .append(urgent.getModele()).append("\n");
          }
        }
      }

      rapport.append("\n");
    }

    // 总结
    rapport.append("📈 RÉSUMÉ GÉNÉRAL\n");
    rapport.append("═".repeat(30)).append("\n");
    rapport.append("Agents totaux: ").append(agentIds.size()).append("\n");
    rapport.append("Véhicules totaux: ").append(totalVehicules).append("\n");
    rapport.append("Contrôles urgents: ").append(totalUrgents).append("\n");

    if (totalUrgents > 0) {
      rapport.append("🔴 ATTENTION: ").append(totalUrgents)
          .append(" véhicules nécessitent un contrôle urgent!\n");
    }

    rapport.append("═".repeat(70));

    return rapport.toString();
  }

  /**
   *
   */
  public ControlTechniqueService getControlTechniqueService() {
    return controlTechniqueService;
  }

  /**
   * obtenir le service de véhicule
   */
  public VehiculeService getVehiculeService() {
    return vehiculeService;
  }
}
