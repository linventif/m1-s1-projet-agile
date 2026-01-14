package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LocationService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.2 Je veux pouvoir consulter l'historique de chaque véhicule mis à disposition. (1)
 */
public class TestUSA2 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LocationService locationService = new LocationService(new LocationRepository());

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

      // Test US.A.2
      System.out.println("\n=== US.A.2: Consultation de l'historique d'un véhicule ===");
      System.out.println("Historique du véhicule " + vehicule.getMarque() + " "
          + vehicule.getModele() + " (ID: " + vehicule.getId() + "):");

      for (LocationDTO location : locationService
          .getPreviousLocationsForVehicule(vehicule.getId())) {
        System.out.println(" - Location:");
        System.out.println("   Date début: " + location.getDateDebut());
        System.out.println("   Date fin: " + location.getDateFin());
        System.out.println("   Lieu dépôt: " + location.getLieuDepot());
        System.out.println("   Véhicule: " + location.getVehicule().getMarque() + " "
            + location.getVehicule().getModele());
        System.out.println("   Statut: " + location.getStatut());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
