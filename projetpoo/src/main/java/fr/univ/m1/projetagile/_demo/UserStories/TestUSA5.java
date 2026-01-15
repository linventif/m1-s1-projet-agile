package fr.univ.m1.projetagile._demo.UserStories;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;

/**
 * US.A.5 Je veux pouvoir envoyer un message à un loueur ou à un agent par la messagerie interne
 * (1)
 */
public class TestUSA5 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());

      // S'assurer que nous avons des données de test
      Agent agent1 = agentService.findById(1L);
      if (agent1 == null) {
        Long idAgent1 = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com",
                "motdepasse123")
            .getIdU();
        agent1 = agentService.findById(idAgent1);
        System.out.println("✓ Agent 1 créé avec ID: " + idAgent1);
      }

      Agent agent2 = agentService.findById(2L);
      if (agent2 == null) {
        Long idAgent2 = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent2 = agentService.findById(idAgent2);
        System.out.println("✓ Agent 2 créé avec ID: " + idAgent2);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Martin", "Sophie", "sophie.martin@example.com", "motdepasse123")
            .getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      // Tester US.A.5
      System.out.println("\n=== US.A.5: Envoi de messages par un agent ===");

      // Message à un autre agent
      messagerieService.envoyerMessage(agent1, agent2,
          "Bonjour, je voudrais savoir si ce loueur est fiable");
      System.out.println("✓ Message envoyé à un agent");

      // Message à un loueur
      messagerieService.envoyerMessage(agent1, loueur,
          "Pas de problème pour vous louer du 17 décembre au 20 décembre");
      System.out.println("✓ Message envoyé à un loueur");

      // Afficher les messages reçus par l'agent 1
      List<Message> messages = messagerieService.getMessagesUtilisateur(agent1);
      System.out.println("\nMessages reçus par l'agent:");
      for (Message message : messages) {
        System.out.println(" - " + message.getContenu() + " (" + message.getDateEnvoi() + ")");
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
