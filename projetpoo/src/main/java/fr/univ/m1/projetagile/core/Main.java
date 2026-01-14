package fr.univ.m1.projetagile.core;

import java.util.List;
import fr.univ.m1.projetagile.assurance.entity.Assurance;
import fr.univ.m1.projetagile.assurance.entity.GrilleTarif;
import fr.univ.m1.projetagile.assurance.service.AssuranceService;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.EntityManager;

public class Main {

  public static void main(String[] args) {
    EntityManager em = null;

    try {
      // Initialize database connection
      DatabaseConnection.init();
      em = DatabaseConnection.getEntityManager();

      System.out.println("DB connected\n");

      // =========================
      // 1) Liste des tables
      // =========================
      System.out.println("=== Liste des tables ===");
      String sql = "SELECT table_name FROM user_tables ORDER BY table_name";

      @SuppressWarnings("unchecked")
      List<String> tables = em.createNativeQuery(sql).getResultList();

      if (tables.isEmpty()) {
        System.out.println("  (aucune table trouvée)");
      } else {
        for (String tableName : tables) {
          System.out.println("  - " + tableName);
        }
      }
      System.out.println("\nNombre total de tables: " + tables.size());

      // =========================
      // 2) TEST AssuranceService (persist en DB)
      // =========================
      System.out.println("\n=== TEST AssuranceService (persist) ===");

      AssuranceService assuranceService = new AssuranceService();

      // Important : transaction obligatoire pour persist
      em.getTransaction().begin();

      // Créer grille
      GrilleTarif grille = assuranceService.creerGrille();
      em.persist(grille); // persist d'abord la grille (parent)

      // Ajouter tarifs (ils seront persist grâce à cascade depuis GrilleTarif)
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "Clio", 10);
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "208", 12);
      assuranceService.ajouterTarifOption(grille, "GPS", "Navigation", 2);
      assuranceService.ajouterTarifOption(grille, "SiegeBebe", "Siège bébé", 3);

      // Créer assurance liée à la grille
      Assurance assurance = assuranceService.creerAssurance("AZA", grille);
      // Optionnel : si tu utilises grille.ajouterAssurance(assurance)
      grille.ajouterAssurance(assurance);

      // Persist assurance (cascade peut le faire aussi, mais là c'est clair)
      em.persist(assurance);

      em.getTransaction().commit();

      System.out.println("Grille saved (id=" + grille.getId() + ")");
      System.out.println(
          "Assurance saved (id=" + assurance.getId() + ", nom=" + assurance.getNom() + ")");
      System.out.println("Tarifs véhicules registered: " + grille.getTarifVehi().size());
      System.out.println("Tarifs options registered: " + grille.getTarifOptions().size());

      // =========================
      // 3) Vérification lecture (JPQL simple)
      // =========================
      System.out.println("\n=== Vérification lecture ===");

      Long nbAssurances =
          em.createQuery("SELECT COUNT(a) FROM Assurance a", Long.class).getSingleResult();
      Long nbGrilles =
          em.createQuery("SELECT COUNT(g) FROM GrilleTarif g", Long.class).getSingleResult();

      System.out.println("Assurances en DB: " + nbAssurances);
      System.out.println("Grilles en DB: " + nbGrilles);

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();

      // rollback si une transaction est ouverte
      try {
        if (em != null && em.getTransaction().isActive()) {
          em.getTransaction().rollback();
          System.out.println("Transaction rollback");
        }
      } catch (Exception ex) {
        System.err.println("Rollback error: " + ex.getMessage());
      }

    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
      DatabaseConnection.close();
    }
  }
}
