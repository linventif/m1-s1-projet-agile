package fr.univ.m1.projetagile.messagerie;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class Main {
  public static void main(String[] args) {
    EntityManager em = null;

    try {
      // Initialize database connection
      DatabaseConnection.init();
      em = DatabaseConnection.getEntityManager();

      System.out.println("✓ DB connectée\n");

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

      // Create messages between them
      Message messageLoueurToAgent =
          new Message("Bonjour, je suis intéressé par votre véhicule.", loueur, agent);
      em.persist(messageLoueurToAgent);
      System.out.println("✓ Message du Loueur vers l'Agent créé");

      Message messageAgentToLoueur = new Message(
          "Bonjour, merci pour votre intérêt. Quand souhaitez-vous louer ?", agent, loueur);
      em.persist(messageAgentToLoueur);
      System.out.println("✓ Message de l'Agent vers le Loueur créé");

      em.getTransaction().commit();
      System.out.println("\n✓ Tous les éléments ont été sauvegardés avec succès!");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();

    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
      DatabaseConnection.close();
    }
  }
}
