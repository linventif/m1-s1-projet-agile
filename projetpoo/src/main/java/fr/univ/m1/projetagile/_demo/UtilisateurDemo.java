package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;

/**
 * Démonstration complète de la gestion des utilisateurs Couvre toutes les fonctionnalités des
 * services AgentService et LoueurService avec exemples et validations
 */
public class UtilisateurDemo {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée\n");

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());

      // ========================================
      // 1. CRÉATION D'AGENTS PARTICULIERS
      // ========================================
      System.out.println("╔════════════════════════════════════════╗");
      System.out.println("║   1. AGENTS PARTICULIERS              ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Création d'agents particuliers ---");
      AgentParticulier bob = agentService.createAgentParticulier("Maurise", "Bob",
          "bob.maurise@gmail.com", "P@ssw0rd123", "33601020304");
      System.out.println("✓ Créé: " + bob.getPrenom() + " " + bob.getNom() + " (ID: " + bob.getIdU()
          + ", Tél: " + bob.getTelephone() + ")");

      AgentParticulier alice = agentService.createAgentParticulier("Dupont", "Alice",
          "alice.dupont@gmail.com", "SecurePass456", "33605060708");
      System.out.println("✓ Créé: " + alice.getPrenom() + " " + alice.getNom() + " (ID: "
          + alice.getIdU() + ", Tél: " + alice.getTelephone() + ")");

      AgentParticulier charlie = agentService.createAgentParticulier("Martin", "Charlie",
          "charlie.martin@outlook.fr", "MyP@ss789", "33609101112");
      System.out.println("✓ Créé: " + charlie.getPrenom() + " " + charlie.getNom() + " (ID: "
          + charlie.getIdU() + ", Tél: " + charlie.getTelephone() + ")");

      // ========================================
      // 2. CRÉATION D'AGENTS PROFESSIONNELS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   2. AGENTS PROFESSIONNELS            ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Création d'agents professionnels ---");
      AgentProfessionnel locaSmart = agentService.createAgentProfessionnel("contact@locasmart.fr",
          "AdminPass123", "12345678901234", "LocaSmart");
      System.out.println("✓ Créé: " + locaSmart.getNomEntreprise() + " (ID: " + locaSmart.getIdU()
          + ", SIRET: " + locaSmart.getSiret() + ")");

      AgentProfessionnel habitatPlus = agentService.createAgentProfessionnel("info@habitatplus.fr",
          "SecureAdmin456", "98765432109876", "HabitatPlus");
      System.out.println("✓ Créé: " + habitatPlus.getNomEntreprise() + " (ID: "
          + habitatPlus.getIdU() + ", SIRET: " + habitatPlus.getSiret() + ")");

      AgentProfessionnel autoLoc = agentService.createAgentProfessionnel("contact@autoloc.com",
          "AutoP@ss789", "11223344556677", "AutoLoc Services");
      System.out.println("✓ Créé: " + autoLoc.getNomEntreprise() + " (ID: " + autoLoc.getIdU()
          + ", SIRET: " + autoLoc.getSiret() + ")");

      // ========================================
      // 3. CRÉATION DE LOUEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   3. LOUEURS                          ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Création de loueurs ---");
      Loueur john = loueurService.createLoueur("Doe", "John", "john.doe@gmail.com", "JohnPass123");
      System.out.println(
          "✓ Créé: " + john.getPrenom() + " " + john.getNom() + " (ID: " + john.getIdU() + ")");

      Loueur jane =
          loueurService.createLoueur("Smith", "Jane", "jane.smith@yahoo.com", "JaneSecure456");
      System.out.println(
          "✓ Créé: " + jane.getPrenom() + " " + jane.getNom() + " (ID: " + jane.getIdU() + ")");

      Loueur maria =
          loueurService.createLoueur("Garcia", "Maria", "maria.garcia@hotmail.com", "MariaP@ss789");
      System.out.println(
          "✓ Créé: " + maria.getPrenom() + " " + maria.getNom() + " (ID: " + maria.getIdU() + ")");

      // ========================================
      // 4. RECHERCHE D'UTILISATEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   4. RECHERCHE D'UTILISATEURS         ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Recherche par email ---");
      Utilisateur foundBob = agentService.findByEmail("bob.maurise@gmail.com");
      if (foundBob != null) {
        System.out.println("✓ Trouvé (Agent): " + foundBob.getEmail() + " - "
            + foundBob.getClass().getSimpleName());
      }

      Loueur foundJohn = loueurService.findByEmail("john.doe@gmail.com");
      if (foundJohn != null) {
        System.out.println("✓ Trouvé (Loueur): " + foundJohn.getEmail() + " - "
            + foundJohn.getPrenom() + " " + foundJohn.getNom());
      }

      System.out.println("\n--- Recherche email inexistant ---");
      Utilisateur notFound = agentService.findByEmail("inexistant@example.com");
      System.out.println(notFound == null ? "✓ NULL (attendu)" : "✗ Trouvé (inattendu)");

      // ========================================
      // 5. MODIFICATION D'UTILISATEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   5. MODIFICATION D'UTILISATEURS      ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Modification de mot de passe ---");
      String oldEmail = bob.getEmail();
      System.out.println("Avant: " + bob.getEmail());
      agentService.updateEmail(bob.getIdU(), "bob.maurise.new@gmail.com");
      Utilisateur updatedBob = agentService.findById(bob.getIdU());
      System.out.println("Après: " + updatedBob.getEmail());
      System.out.println("✓ Email modifié avec succès");

      System.out.println("\n--- Modification de téléphone (Agent Particulier) ---");
      AgentParticulier aliceReloaded = (AgentParticulier) agentService.findById(alice.getIdU());
      System.out.println("Avant: " + aliceReloaded.getTelephone());
      aliceReloaded.setTelephone("33699887766");
      agentService.update(aliceReloaded);
      AgentParticulier aliceUpdated = (AgentParticulier) agentService.findById(alice.getIdU());
      System.out.println("Après: " + aliceUpdated.getTelephone());
      System.out.println("✓ Téléphone modifié avec succès");

      // ========================================
      // 6. LISTE DE TOUS LES UTILISATEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   6. LISTE DES UTILISATEURS           ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Tous les agents ---");
      var allAgents = agentService.findAll();
      System.out.println("Nombre total d'agents: " + allAgents.size());
      for (Utilisateur agent : allAgents) {
        if (agent instanceof AgentParticulier ap) {
          System.out.println(
              "  • " + ap.getPrenom() + " " + ap.getNom() + " (Particulier) - " + ap.getEmail());
        } else if (agent instanceof AgentProfessionnel ap) {
          System.out
              .println("  • " + ap.getNomEntreprise() + " (Professionnel) - " + ap.getEmail());
        }
      }

      System.out.println("\n--- Tous les loueurs ---");
      var allLoueurs = loueurService.findAll();
      System.out.println("Nombre total de loueurs: " + allLoueurs.size());
      for (Loueur loueur : allLoueurs) {
        System.out.println(
            "  • " + loueur.getPrenom() + " " + loueur.getNom() + " - " + loueur.getEmail());
      }

      // ========================================
      // 7. SUPPRESSION D'UTILISATEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   7. SUPPRESSION D'UTILISATEURS       ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      // Créer un utilisateur temporaire pour le supprimer
      AgentParticulier tempAgent = agentService.createAgentParticulier("Temp", "User",
          "temp@example.com", "TempPass123", "33600000000");
      System.out.println("✓ Agent temporaire créé (ID: " + tempAgent.getIdU() + ")");

      agentService.delete(tempAgent.getIdU());
      System.out.println("✓ Agent supprimé");

      Utilisateur verif = agentService.findById(tempAgent.getIdU());
      System.out.println(
          "Vérification après suppression: " + (verif == null ? "✓ NULL" : "✗ Existe encore"));

      // ========================================
      // 8. TESTS DE VALIDATION
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   8. TESTS DE VALIDATION              ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Test: Email invalide ---");
      try {
        agentService.createAgentParticulier("Test", "User", "email-invalide", "Pass123",
            "0600000000");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Mot de passe trop court ---");
      try {
        agentService.createAgentParticulier("Test", "User", "test@example.com", "123",
            "0600000000");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Email déjà utilisé ---");
      try {
        agentService.createAgentParticulier("Duplicate", "User", "bob.maurise.new@gmail.com",
            "Pass123", "0600000000");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Nom vide ---");
      try {
        loueurService.createLoueur("", "User", "empty@example.com", "Pass123");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: SIRET invalide (trop court) ---");
      try {
        agentService.createAgentProfessionnel("test@example.com", "Pass123", "123", "Entreprise");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Téléphone invalide ---");
      try {
        agentService.createAgentParticulier("Test", "User", "phone@example.com", "Pass123",
            "123abc");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   ✓ DÉMONSTRATION TERMINÉE            ║");
      System.out.println("╚════════════════════════════════════════╝");

    } catch (Exception e) {
      System.err.println("\n✗ ERREUR: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
