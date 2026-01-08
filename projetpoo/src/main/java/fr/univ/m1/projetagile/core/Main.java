package fr.univ.m1.projetagile.core;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Assurance;
import fr.univ.m1.projetagile.core.entity.GrilleTarif;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.service.AssuranceService;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.notes.NoteA;
import fr.univ.m1.projetagile.notes.NoteV;
import jakarta.persistence.EntityManager;

public class Main {

  public static void main(String[] args) {
    EntityManager em = null;

    try {
      DatabaseConnection.init();
      em = DatabaseConnection.getEntityManager();

      System.out.println("✓ DB connectée\n");

      // =========================
      // 1) Liste des tables
      // =========================
      System.out.println("=== Liste des tables ===");
      String sql = "SELECT table_name FROM user_tables ORDER BY table_name";

      @SuppressWarnings("unchecked")
      List<String> tables = em.createNativeQuery(sql).getResultList();

      for (String tableName : tables) {
        System.out.println("  - " + tableName);
      }

      // =========================
      // 2) TEST AssuranceService
      // =========================
      System.out.println("\n=== TEST AssuranceService ===");

      AssuranceService assuranceService = new AssuranceService();

      em.getTransaction().begin();

      GrilleTarif grille = assuranceService.creerGrille();
      em.persist(grille);

      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "Clio", 10);
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "208", 12);
      assuranceService.ajouterTarifOption(grille, "GPS", "Navigation", 2);
      assuranceService.ajouterTarifOption(grille, "SiegeBebe", "Siège bébé", 3);

      Assurance assurance = assuranceService.creerAssurance("AZA", grille);
      grille.ajouterAssurance(assurance);
      em.persist(assurance);

      em.getTransaction().commit();

      System.out.println("✓ Assurance OK");

      // =========================
      // 3) TEST #23 - NOTATION
      // =========================
      System.out.println("\n=== TEST #23 - NOTATION ===");

      Agent agent = new AgentParticulier("Dupont", "Jean", "agent@test.com", "pass", "0600000000");

      Loueur loueur = new Loueur("Martin", "Paul", "loueur@test.com", "pass");

      Vehicule vehicule =
          new Vehicule(TypeV.voiture, "Peugeot", "208", "Rouge", "Toulouse", 45.0, agent);

      NoteA noteAgent = NoteA.NoterAgent(agent, loueur, 4.0, 5.0, 3.0);
      System.out.println("NOTE AGENT");
      System.out.println(NoteA.getCritere1() + " = " + noteAgent.getPonctualite());
      System.out.println(NoteA.getCritere2() + " = " + noteAgent.getCommunication());
      System.out.println(NoteA.getCritere3() + " = " + noteAgent.getSerieux());
      System.out.println("Moyenne = " + noteAgent.getNoteMoyenne());

      NoteV noteVehicule = NoteV.NoterVehicule(vehicule, loueur, 5.0, 4.0, 4.0);
      System.out.println("\nNOTE VEHICULE");
      System.out.println(NoteV.getCritere1() + " = " + noteVehicule.getProprete());
      System.out.println(NoteV.getCritere2() + " = " + noteVehicule.getConfort());
      System.out.println(NoteV.getCritere3() + " = " + noteVehicule.getConformiteAnnonce());
      System.out.println("Moyenne = " + noteVehicule.getNoteMoyenne());

      System.out.println("\n✓ TEST #23 OK");

    } catch (Exception e) {
      e.printStackTrace();
      if (em != null && em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
      DatabaseConnection.close();
    }
  }
}
