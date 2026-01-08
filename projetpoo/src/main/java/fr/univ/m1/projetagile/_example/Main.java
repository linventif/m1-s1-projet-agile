package fr.univ.m1.projetagile._example;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;

public class Main {
  public static void main(String[] args) {
    Agent bob =
        new AgentParticulier("maurise", "bob", "bob.maurise@gmail.com", "p@ssw0rd", "0601020304");

    Agent alice =
        new AgentParticulier("dupont", "alice", "alice.dupont@gmail.com", "p@ssw0rd", "0605060708");

    System.out.println(bob);
    System.out.println(alice);
  }
}
