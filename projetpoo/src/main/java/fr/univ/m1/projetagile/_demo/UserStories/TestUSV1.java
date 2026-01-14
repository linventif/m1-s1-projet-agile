package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.V.1 Je veux pouvoir consulter les véhicules disponibles. J'ai alors accès aux informations
 * suivantes : note du véhicule, date de disponibilités, lieu de disponibilité. (3)
 */
public class TestUSV1 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Vehicule vehicule = vehiculeService.findVehiculeById(1L);
      if (vehicule == null) {
        Long idVehicule = vehiculeService
            .createVehicule(TypeV.voiture, "Peugeot", "308", "blanche", "Paris", 50.0, agent)
            .getId();
        vehicule = vehiculeService.findVehiculeById(idVehicule);
        vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
            LocalDate.now().plusDays(60));
        System.out.println("✓ Véhicule créé avec ID: " + idVehicule);
      }

      // Test US.V.1
      System.out.println("\n=== US.V.1: Consultation des véhicules disponibles ===");
      List<VehiculeDTO> vehicules = vehiculeService.getVehicules();
      System.out.println("Nombre de véhicules disponibles: " + vehicules.size());

      for (VehiculeDTO v : vehicules) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + v.getMarque() + " " + v.getModele() + " (" + v.getCouleur() + ")");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Lieu: " + v.getVille());
        System.out.println("Prix: " + v.getPrixJ() + "€/jour");
        System.out.println("Note moyenne: " + String.format("%.2f", v.getNoteMoyenne()) + "/10");
        System.out.println("Disponible: " + (v.isDisponible() ? "✓ Oui" : "✗ Non"));
        
        System.out.println("\nPériodes de disponibilité:");
        if (v.getDatesDispo() != null && !v.getDatesDispo().isEmpty()) {
          for (int i = 0; i < v.getDatesDispo().size(); i++) {
            LocalDate[] periode = v.getDatesDispo().get(i);
            if (periode != null && periode.length == 2) {
              long jours = ChronoUnit.DAYS.between(periode[0], periode[1]);
              System.out.println("  " + (i + 1) + ". Du " + periode[0] + " au " + periode[1] 
                  + " (" + jours + " jours)");
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
