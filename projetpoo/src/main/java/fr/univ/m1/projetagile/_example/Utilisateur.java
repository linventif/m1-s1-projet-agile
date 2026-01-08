package fr.univ.m1.projetagile._example;

import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;

public class Utilisateur {
  public static void main(String[] args) {
    // 2 agents particuliers
    AgentParticulier AP_bob =
        new AgentParticulier("maurise", "bob", "bob.maurise@gmail.com", "p@ssw0rd", "0601020304");

    AgentParticulier AP_alice =
        new AgentParticulier("dupont", "alice", "alice.dupont@gmail.com", "p@ssw0rd", "0605060708");

    // 2 agents professionnels
    AgentProfessionnel AP_locasmart =
        new AgentProfessionnel("contact@localsmart.fr", "p@ssw0rd", "12345678901234", "LocaSmart");
    AgentProfessionnel AP_habitatplus = new AgentProfessionnel("contact@habitatplus.fr", "p@ssw0rd",
        "98765432109876", "HabitatPlus");

    // 2 loueurs
    Loueur L_john = new Loueur("doe", "john", "john.doe@gmail.com", "p@ssw0rd");
    Loueur L_jane = new Loueur("smith", "jane", "jane.smith@gmail.com", "p@ssw0rd");

    System.out.println(AP_bob);
    System.out.println(AP_alice);

    System.out.println(AP_locasmart);
    System.out.println(AP_habitatplus);

    System.out.println(L_john);
    System.out.println(L_jane);
  }
}
