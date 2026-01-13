package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;

public class MessagerieDemoSimple {

  public static void main(String[] args) {
    try {
      DatabaseConnection.init();

      MessagerieService messagerieService = new MessagerieService(new MessageRepository());

      // Création d’utilisateurs légers (IDs requis par MessagerieService)
      Loueur loueur = new Loueur("Doe", "John", "john@example.com", "pass");
      loueur.setIdU(1L);
      AgentParticulier agent =
          new AgentParticulier("Smith", "Alice", "alice@example.com", "pass", "0600000000");
      agent.setIdU(2L);

      // 4 exemples simples
      Message m1 = messagerieService.envoyerMessage(loueur, agent, "Bonjour, véhicule dispo ?");
      Message m2 = messagerieService.envoyerMessage(agent, loueur, "Oui, dates souhaitées ?");
      Message m3 = messagerieService.envoyerMessage(loueur, agent, "Du 1 au 5 mai, merci.");
      Message m4 = messagerieService.envoyerMessage(agent, loueur, "Parfait, je confirme.");

      System.out.println("Messages envoyés :");
      System.out.println(m1);
      System.out.println(m2);
      System.out.println(m3);
      System.out.println(m4);

    } finally {
      DatabaseConnection.close();
    }
  }
}
