package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.service.AgentService;

public class AgentDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());

      // 4 agents (2 particuliers, 2 professionnels)
      AgentParticulier a1 = agentService.createAgentParticulier("Martin", "Lena",
          "lena" + suffix + "@agent.demo", "pass", "336" + String.format("%08d", suffix));
      AgentParticulier a2 = agentService.createAgentParticulier("Durand", "Paul",
          "paul" + suffix + "@agent.demo", "pass", "337" + String.format("%08d", suffix));

      AgentProfessionnel p1 = agentService.createAgentProfessionnel("pro1" + suffix + "@agent.demo",
          "pass", "" + (11_000_000_000_000L + suffix), "AgencePro" + suffix);
      AgentProfessionnel p2 = agentService.createAgentProfessionnel("pro2" + suffix + "@agent.demo",
          "pass", "" + (12_000_000_000_000L + suffix), "Mobilia" + suffix);

      System.out.println("Agents créés :");
      System.out.println(a1);
      System.out.println(a2);
      System.out.println(p1);
      System.out.println(p2);
    } finally {
      DatabaseConnection.close();
    }
  }
}
