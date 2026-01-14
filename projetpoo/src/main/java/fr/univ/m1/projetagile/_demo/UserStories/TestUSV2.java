package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
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
 * US.V.2. Je veux pouvoir appliquer des filtres sur les véhicules que je vois. (2)
 */
public class TestUSV2 {
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

      // Test US.V.2
      System.out.println("\n=== US.V.2: Application de filtres sur les véhicules ===");
      List<VehiculeDTO> vehiculesFiltres = vehiculeService.searchVehiculesWithFilters(null, null,
          "Paris", "Peugeot", "308", null, null, null, TypeV.voiture, null);
      System.out.println("Vehicules avec filtres (Paris, Peugeot, 308, voiture): ");
      for (VehiculeDTO v : vehiculesFiltres) {
        System.out.println(" - " + v.getMarque() + " " + v.getModele() + " " + v.getCouleur() + " "
            + v.getVille() + " " + v.getPrixJ() + "€/j" + " Note: " + v.getNoteMoyenne()
            + " Disponibilités: " + v.getDatesDispo());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
