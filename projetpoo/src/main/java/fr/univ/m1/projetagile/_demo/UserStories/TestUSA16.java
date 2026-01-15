package fr.univ.m1.projetagile._demo.UserStories;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.entity.SouscriptionOption;
import fr.univ.m1.projetagile.options.service.SouscriptionOptionService;

/**
 * US.A.16 Agent peut ajouter un parking. Un agent peut choisir des emplacements de parking pour
 * l'ajouter en tant que lieu de depot pour leur vehicule
 */
public class TestUSA16 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());
      SouscriptionOptionService souscriptionOptionService = new SouscriptionOptionService();

      // S'assurer que nous avons des données de test - Agent
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Dubois", "Jean", "jdubois@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      // Tester US.A.16
      System.out.println("\n=== US.A.16: Souscription à l'option Parking ===");

      // Rechercher ou créer l'option Parking
      Options optionParking = souscriptionOptionService.findOptionByNom("Option Parking");
      if (optionParking == null) {
        optionParking = new Options("Option Parking", 15.0);
        optionParking = souscriptionOptionService.saveOption(optionParking);
        System.out.println("✓ Option Parking créée avec ID: " + optionParking.getId());
      } else {
        System.out.println(
            "✓ Option Parking trouvée: " + optionParking.getNomOption() + " - " + optionParking.getPrix() + "€");
      }

      // Vérifier si l'agent a déjà souscrit à l'option Parking
      boolean hasOption = souscriptionOptionService.getOptionsActives(agent).stream()
          .anyMatch(opt -> opt.getOption().getNomOption().equals("Option Parking"));

      if (!hasOption) {
        // Souscrire à l'option Parking
        SouscriptionOption souscription = souscriptionOptionService.souscrireOption(agent.getIdU(),
            optionParking.getId(), 1, true);
        System.out.println("✓ Agent souscrit à l'option Parking avec ID: " + souscription.getId());
        System.out.println("Prix de l'option: " + optionParking.getPrix() + "€/mois");
      } else {
        System.out.println("✓ Agent déjà souscrit à l'option Parking");
      }

      // Afficher les options actives de l'agent
      System.out.println("\nOptions actives de l'agent:");
      souscriptionOptionService.getOptionsActives(agent).forEach(opt -> {
        System.out.println("  - " + opt.getOption().getNomOption() + " (ID: " + opt.getId() + ")");
      });

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
