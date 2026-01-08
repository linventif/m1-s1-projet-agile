package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.notes.NoteA;
import fr.univ.m1.projetagile.notes.NoteL;
import fr.univ.m1.projetagile.notes.NoteV;
import fr.univ.m1.projetagile.notes.service.NoteService;
import jakarta.persistence.EntityManager;

public class NoteDemo {

  public static void main(String[] args) {

    try {
      // 1️⃣ Init DB
      DatabaseConnection.init();
      EntityManager em = DatabaseConnection.getEntityManager();
      em.getTransaction().begin();

      System.out.println("✓ DB connectée");

      // 2️⃣ Loueur
      Loueur loueur = new Loueur("Doe", "John", "jdoe@example.com", "password123");
      em.persist(loueur);
      System.out.println("✓ Loueur ID: " + loueur.getIdU());

      // 3️⃣ AgentParticulier
      AgentParticulier agent =
          new AgentParticulier("Smith", "Alice", "asmith@example.com", "securePass", "0612345678");
      em.persist(agent);
      System.out.println("✓ Agent ID: " + agent.getIdU());

      // 4️⃣ Vehicule
      Vehicule v = new Vehicule();
      v.setMarque("Peugeot");
      v.setModele("208");
      v.setVille("Paris");
      v.setPrixJ(50.0); // 必填
      v.setType(TypeV.voiture); // 必填
      v.setProprietaire(agent); // agent 是 AgentParticulier 类型

      em.persist(v);
      System.out.println("✓ Vehicule ID: " + v.getId());

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
