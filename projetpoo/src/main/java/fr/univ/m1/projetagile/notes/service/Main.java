package fr.univ.m1.projetagile._example.;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.notes.NoteA;
import fr.univ.m1.projetagile.notes.NoteL;
import fr.univ.m1.projetagile.notes.NoteV;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class Main {

  public static void main(String[] args) {

    try {
      // 1️⃣ Init DB
      DatabaseConnection.init();
      EntityManager em = DatabaseConnection.getEntityManager();
      em.getTransaction().begin();

      System.out.println("✓ DB connectée");

      /// Get or create Loueur
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

      // 5️⃣ Créer le service NoteService
      NoteService noteService = new NoteService(em);

      // 6️⃣ Loueur note Agent
      NoteA noteA = noteService.noterAgent(loueur, agent, 8.0, 9.0, 7.5);
      System.out.println("NoteA moyenne: " + noteA.getNoteMoyenne());

      // 7️⃣ Agent note Loueur
      NoteL noteL = noteService.noterLoueur(agent, loueur, 9.0, 8.5, 10.0);
      System.out.println("NoteL moyenne: " + noteL.getNoteMoyenne());

      // 8️⃣ Loueur note Vehicule
      NoteV noteV = noteService.noterVehicule(loueur, v, 7.0, 8.0, 9.0);
      System.out.println("NoteV moyenne: " + noteV.getNoteMoyenne());

      System.out.println("✓ Toutes les notes ont été créées avec succès !");

    } catch (Exception e) {
      e.printStackTrace();
      // Rollback si nécessaire
      try {
        EntityManager em = DatabaseConnection.getEntityManager();
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } finally {
      DatabaseConnection.close();
    }
  }
}
