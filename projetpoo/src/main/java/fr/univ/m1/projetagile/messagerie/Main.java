package fr.univ.m1.projetagile.messagerie;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class Main {
  public static void main(String[] args) {
    try {
      // Initialize database connection
      DatabaseConnection.init();

      System.out.println("✓ DB connectée\n");

      // Correction des valeurs type_agent dans la base si nécessaire
      corrigerTypeAgent();

      // Utiliser un seul EntityManager réutilisé par thread
      EntityManager em = DatabaseConnection.getEntityManager();
      em.getTransaction().begin();

      // Get or create Loueur
      String loueurEmail = "jdoe@example.com";
      TypedQuery<Loueur> loueurQuery =
          em.createQuery("SELECT l FROM Loueur l WHERE l.email = :email", Loueur.class);
      loueurQuery.setParameter("email", loueurEmail);
      Loueur loueur = loueurQuery.getResultList().stream().findFirst().orElse(null);

      if (loueur == null) {
        loueur = new Loueur("Doe", "John", loueurEmail, "securePassword");
        em.persist(loueur);
        System.out.println("✓ Loueur créé avec l'ID: " + loueur.getIdU());
      } else {
        System.out.println("✓ Loueur existant trouvé avec l'ID: " + loueur.getIdU());
      }

      // Get or create Agent
      String agentEmail = "asmith@example.com";
      TypedQuery<AgentParticulier> agentQuery = em.createQuery(
          "SELECT a FROM AgentParticulier a WHERE a.email = :email", AgentParticulier.class);
      agentQuery.setParameter("email", agentEmail);
      AgentParticulier agent = agentQuery.getResultList().stream().findFirst().orElse(null);

      if (agent == null) {
        agent = new AgentParticulier("Smith", "Alice", agentEmail, "securePassword", "0612345678");
        em.persist(agent);
        System.out.println("✓ Agent créé avec l'ID: " + agent.getIdU());
      } else {
        System.out.println("✓ Agent existant trouvé avec l'ID: " + agent.getIdU());
      }

      em.getTransaction().commit();

      // ======== Utilisation du MessagerieService ========
      System.out.println("\n=== Démonstration du MessagerieService ===\n");

      MessagerieService service = new MessagerieService();

      // Envoi de messages via le service (sauvegarde automatique)
      System.out.println("📧 Envoi de messages...");
      Message msg1 = service.envoyerMessage(loueur, agent,
          "Bonjour, je suis intéressé par votre véhicule Peugeot 208.");
      System.out.println("✓ Message 1 envoyé et sauvegardé (ID: " + msg1.getId() + ")");

      Message msg2 = service.envoyerMessage(agent, loueur,
          "Bonjour ! Oui, il est disponible. Quand souhaitez-vous le louer ?");
      System.out.println("✓ Message 2 envoyé et sauvegardé (ID: " + msg2.getId() + ")");

      Message msg3 =
          service.envoyerMessage(loueur, agent, "Je souhaiterais le louer du 15 au 20 décembre.");
      System.out.println("✓ Message 3 envoyé et sauvegardé (ID: " + msg3.getId() + ")");

      Message msg4 = service.envoyerMessage(agent, loueur,
          "Parfait ! Je vais préparer le contrat de location.");
      System.out.println("✓ Message 4 envoyé et sauvegardé (ID: " + msg4.getId() + ")");

      // Utilisation via les méthodes de Utilisateur
      System.out.println("\n📧 Envoi via méthode Utilisateur.envoyerMessage()...");
      Message msg5 = loueur.envoyerMessage(agent, "Merci beaucoup !");
      System.out.println("✓ Message 5 envoyé (ID: " + msg5.getId() + ")");

      // Récupération des messages du loueur
      System.out.println("\n=== Messages du Loueur (via service) ===");
      List<Message> messagesLoueur = service.getMessagesUtilisateur(loueur);
      for (Message msg : messagesLoueur) {
        String direction = msg.getExpediteurId().equals(loueur.getIdU()) ? "→ Envoyé" : "← Reçu";
        System.out.println(direction + ": " + msg.getContenu());
      }

      // Récupération de la conversation
      System.out.println("\n=== Conversation complète (via service) ===");
      List<Message> conversation = service.getConversation(loueur, agent);
      for (Message msg : conversation) {
        String expediteur = msg.getExpediteurId().equals(loueur.getIdU()) ? "Loueur" : "Agent";
        System.out.println("[" + expediteur + "] " + msg.getContenu());
      }

      // Utilisation via méthode de Utilisateur
      System.out.println("\n=== Conversation via Utilisateur.getConversationAvec() ===");
      List<Message> conversationViaUtilisateur = loueur.getConversationAvec(agent);
      System.out.println("Nombre de messages échangés: " + conversationViaUtilisateur.size());

      // Statistiques
      System.out.println("\n=== Statistiques ===");
      System.out
          .println("Messages envoyés par le loueur: " + service.getMessagesEnvoyes(loueur).size());
      System.out
          .println("Messages reçus par le loueur: " + service.getMessagesRecus(loueur).size());
      System.out
          .println("Total messages du loueur: " + service.getMessagesUtilisateur(loueur).size());
      System.out.println(
          "Messages dans la conversation: " + service.compterMessagesConversation(loueur, agent));
      System.out.println("Ont échangé des messages: " + service.ontEchangeMessages(loueur, agent));

      // Récupération de la conversation complète
      System.out.println("\n=== Conversation entre Loueur et Agent ===");
      List<Message> conversation = messageRepository.findConversationBetween(loueur, agent);
      for (Message msg : conversation) {
        System.out.println("[" + msg.getDateEnvoi() + "] " + msg.getContenu());
      }

      System.out.println("\n✓ Tous les éléments ont été sauvegardés avec succès!");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();

      // Rollback si transaction active
      try {
        EntityManager em = DatabaseConnection.getEntityManager();
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
      } catch (Exception rollbackEx) {
        System.err.println("Erreur lors du rollback: " + rollbackEx.getMessage());
      }

    } finally {
      // Ferme l'EntityManager du thread et l'EntityManagerFactory
      DatabaseConnection.close();
    }
  }

  /**
   * Corrige les valeurs type_agent dans la base pour correspondre à l'enum TypeAgent Change
   * "AgentParticulier" en "PARTICULIER" et "AgentProfessionnel" en "PROFESSIONNEL"
   */
  private static void corrigerTypeAgent() {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      em.getTransaction().begin();

      // Correction pour AgentParticulier -> PARTICULIER
      int updated1 = em
          .createNativeQuery(
              "UPDATE agents SET type_agent = 'PARTICULIER' WHERE type_agent = 'AgentParticulier'")
          .executeUpdate();

      // Correction pour AgentProfessionnel -> PROFESSIONNEL
      int updated2 = em.createNativeQuery(
          "UPDATE agents SET type_agent = 'PROFESSIONNEL' WHERE type_agent = 'AgentProfessionnel'")
          .executeUpdate();

      em.getTransaction().commit();

      if (updated1 > 0 || updated2 > 0) {
        System.out.println("✓ Correction des type_agent: " + updated1 + " particuliers, " + updated2
            + " professionnels\n");
      }

    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      // Ignorer silencieusement si déjà corrigé ou si erreur
    }
  }
}
