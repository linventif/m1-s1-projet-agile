package fr.univ.m1.projetagile.messagerie;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
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

      // Utilisation du MessageRepository (utilise le même EntityManager du thread)
      MessageRepository messageRepository = new MessageRepository();

      // Create messages between them using repository
      Message messageLoueurToAgent =
          new Message("Bonjour, je suis intéressé par votre véhicule.", loueur, agent);
      messageLoueurToAgent = messageRepository.save(messageLoueurToAgent);
      System.out.println("✓ Message du Loueur vers l'Agent créé via Repository");

      Message messageAgentToLoueur = new Message(
          "Bonjour, merci pour votre intérêt. Quand souhaitez-vous louer ?", agent, loueur);
      messageAgentToLoueur = messageRepository.save(messageAgentToLoueur);
      System.out.println("✓ Message de l'Agent vers le Loueur créé via Repository");

      // Récupération des messages via Utilisateur.getMessages()
      System.out.println("\n=== Messages du Loueur ===");
      List<Message> messagesLoueur = loueur.getMessages();
      for (Message msg : messagesLoueur) {
        System.out.println("- " + msg.getContenu());
      }

      System.out.println("\n=== Messages de l'Agent ===");
      List<Message> messagesAgent = agent.getMessages();
      for (Message msg : messagesAgent) {
        System.out.println("- " + msg.getContenu());
      }

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
