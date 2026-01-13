package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;

public class UtilisateurDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());

      // 2 agents + 2 loueurs = 4 utilisateurs
      AgentParticulier agent1 = agentService.createAgentParticulier("NomA1", "PrenomA1",
          "agentp" + suffix + "@demo.test", "pass", "336" + String.format("%08d", suffix));
      AgentProfessionnel agent2 =
          agentService.createAgentProfessionnel("agentpro" + suffix + "@demo.test", "pass",
              "" + (10_000_000_000_000L + suffix), "Entreprise" + suffix);

      Loueur loueur1 = loueurService.createLoueur("NomL1", "PrenomL1",
          "loueur1" + suffix + "@demo.test", "pass");
      Loueur loueur2 = loueurService.createLoueur("NomL2", "PrenomL2",
          "loueur2" + suffix + "@demo.test", "pass");

      System.out.println("Utilisateurs créés :");
      System.out.println(agent1);
      System.out.println(agent2);
      System.out.println(loueur1);
      System.out.println(loueur2);
    } finally {
      DatabaseConnection.close();
    }
  }
}
