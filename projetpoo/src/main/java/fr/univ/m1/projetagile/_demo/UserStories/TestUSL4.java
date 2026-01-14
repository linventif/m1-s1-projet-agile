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
 * US.L.4 Je veux pouvoir contacter un agent, ayant conclu un contrat avec lui ou non, par un
 * service de messagerie interne à la plateforme. (2)
 */
public class TestUSL4 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123")
            .getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      // Test US.L.4
      System.out.println("\n=== US.L.4: Contact d'un agent par messagerie ===");
      messagerieService.envoyerMessage(loueur, agent,
          "Bonjour, je voudrais louer votre véhicule");

      List<Message> messages = messagerieService.getMessagesUtilisateur(agent);
      for (Message message : messages) {
        System.out.println("Message reçu par l'agent:");
        System.out.println(" - Contenu: " + message.getContenu());
        System.out.println(" - Date: " + message.getDateEnvoi());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
