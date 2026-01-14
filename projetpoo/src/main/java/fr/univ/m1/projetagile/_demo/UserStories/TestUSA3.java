package fr.univ.m1.projetagile._demo.UserStories;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.entity.SouscriptionOption;
import fr.univ.m1.projetagile.options.service.SouscriptionOptionService;

/**
 * US.A.3 Je veux pouvoir contracter ou annuler des options payantes. (3)
 */
public class TestUSA3 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());
      SouscriptionOptionService souscriptionOptionService = new SouscriptionOptionService();

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      // Test US.A.3
      System.out.println("\n=== US.A.3: Souscription et annulation d'options payantes ===");

      // Rechercher ou créer une option pour le test
      Options option = souscriptionOptionService.findOptionByNom("Option Premium");
      if (option == null) {
        option = new Options("Option Premium", 29.99);
        option = souscriptionOptionService.saveOption(option);
        System.out
            .println("✓ Option créée: " + option.getNomOption() + " - " + option.getPrix() + "€");
      } else {
        System.out
            .println("✓ Option trouvée: " + option.getNomOption() + " - " + option.getPrix() + "€");
      }

      // Souscrire à l'option
      SouscriptionOption souscription =
          souscriptionOptionService.souscrireOption(agent.getIdU(), option.getId(), 1, true);
      System.out.println("✓ Souscription créée avec ID: " + souscription.getId());

      // Annuler la souscription
      souscriptionOptionService.annulerSouscription(souscription.getId());
      System.out.println("✓ Souscription annulée");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
