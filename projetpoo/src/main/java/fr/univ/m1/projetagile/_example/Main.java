package fr.univ.m1.projetagile._example;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;

public class Main {
  public static void main(String[] args) {
    try {
      // -- // -- // -- // -- // -- // -- // -- //
      // Initialize database connection
      // -- // -- // -- // -- // -- // -- // -- //
      DatabaseConnection.init();
      System.out.println("✓ DB connectée\n");


      // -- // -- // -- // -- // -- // -- // -- //
      // Initialize services
      // -- // -- // -- // -- // -- // -- // -- //
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());


      // -- // -- // -- // -- // -- // -- // -- //
      // Initialize Utilisateurs
      // -- // -- // -- // -- // -- // -- // -- //

      // Particulier
      AgentParticulier APar_bob = agentService.createAgentParticulier("maurise", "bob",
          "bob.maurise@gmail.com", "p@ssw0rd", "33601020304");
      System.out.println("✓ Agent créé: " + APar_bob);
      AgentParticulier APar_alice = agentService.createAgentParticulier("dupont", "alice",
          "alice.dupont@gmail.com", "p@ssw0rd", "33605060708");
      System.out.println("✓ Agent créé: " + APar_alice);

      // Professionnel
      AgentProfessionnel APro_locasmart = agentService.createAgentProfessionnel(
          "contact@localsmart.fr", "p@ssw0rd", "12345678901234", "LocaSmart");
      System.out.println("✓ Agent créé: " + APro_locasmart);
      AgentProfessionnel APro_habitatplus = agentService.createAgentProfessionnel(
          "contact@habitatplus.fr", "p@ssw0rd", "98765432109876", "HabitatPlus");
      System.out.println("✓ Agent créé: " + APro_habitatplus);

      // Loueur
      Loueur L_john = loueurService.createLoueur("doe", "john", "john.doe@gmail.com", "p@ssw0rd");
      System.out.println("✓ Loueur créé: " + L_john);
      Loueur L_jane =
          loueurService.createLoueur("smith", "jane", "jane.smith@gmail.com", "p@ssw0rd");
      System.out.println("✓ Loueur créé: " + L_jane);

      // -- // -- // -- // -- // -- // -- // -- //
      // Messaging tests
      // -- // -- // -- // -- // -- // -- // -- //

      // Loueur1 -> Agent Pro
      Message msg1 = messagerieService.envoyerMessage(L_john, APro_locasmart,
          "Bonjour, je suis intéressé par vos services");
      System.out.println("✓ Message envoyé: " + msg1);
      // Agent Pro -> Loueur1
      Message msg2 = messagerieService.envoyerMessage(APro_locasmart, L_john,
          "Bonjour, merci pour votre intérêt. Comment puis-je vous aider ?");
      System.out.println("✓ Message envoyé: " + msg2);

      // Loueur2 -> Agent Particulier
      Message msg3 = messagerieService.envoyerMessage(L_jane, APar_bob,
          "Bonjour, j'aimerais louer un véhicule pour le week-end.");
      System.out.println("✓ Message envoyé: " + msg3);

      // Agent Particulier -> Loueur2
      Message msg4 = messagerieService.envoyerMessage(APar_bob, L_jane,
          "Bonjour, j'ai plusieurs véhicules disponibles. Quel type recherchez-vous ?");
      System.out.println("✓ Message envoyé: " + msg4);

      System.out.println("\n✓ Tout s'est bien passé!");
    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Fermer la connexion
      DatabaseConnection.close();
    }
  }
}
