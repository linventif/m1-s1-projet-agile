package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.service.AgentService;

/**
 * US.V.3 Je veux pouvoir consulter le profil des agents de la plateforme, et les véhicules qu'ils
 * louent. (1)
 */
public class TestUSV3 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());

      // S'assurer que nous avons des données de test
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      // Test US.V.3
      System.out.println("\n=== US.V.3: Consultation du profil d'un agent ===");
      AgentDTO agentProfile = agentService.getAgentProfile(agent);

      System.out.println("\n╔══════════════════════════════════════════════╗");
      System.out.println("║          PROFIL DE L'AGENT                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");
      System.out.println("Nom: " + agentProfile.getNom());
      System.out.println("Prénom: " + agentProfile.getPrenom());
      System.out.println("Email: " + agentProfile.getEmail());
      if (agentProfile.getSiret() != null) {
        System.out.println("SIRET: " + agentProfile.getSiret());
      }

      System.out.println("\n╔══════════════════════════════════════════════╗");
      System.out.println("║      VÉHICULES PROPOSÉS PAR CET AGENT       ║");
      System.out.println("╚══════════════════════════════════════════════╝");
      System.out.println("Nombre de véhicules: " + agentProfile.getVehicules().size());

      for (VehiculeDTO v : agentProfile.getVehicules()) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out
            .println("  " + v.getMarque() + " " + v.getModele() + " (" + v.getCouleur() + ")");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Lieu: " + v.getVille());
        System.out.println("Type: " + v.getType());
        System.out.println("Prix: " + v.getPrixJ() + "€/jour");
        System.out.println("Note moyenne: " + String.format("%.2f", v.getNoteMoyenne()) + "/10");
        System.out.println("Disponible: " + (v.isDisponible() ? "✓ Oui" : "✗ Non"));

        System.out.println("\nPériodes de disponibilité:");
        if (v.getDatesDispo() != null && !v.getDatesDispo().isEmpty()) {
          for (int i = 0; i < v.getDatesDispo().size(); i++) {
            LocalDate[] periode = v.getDatesDispo().get(i);
            if (periode != null && periode.length == 2) {
              long jours = ChronoUnit.DAYS.between(periode[0], periode[1]);
              System.out.println("  " + (i + 1) + ". Du " + periode[0] + " au " + periode[1] + " ("
                  + jours + " jours)");
            }
          }
        } else {
          System.out.println("  Aucune période de disponibilité définie");
        }
      }

      System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
