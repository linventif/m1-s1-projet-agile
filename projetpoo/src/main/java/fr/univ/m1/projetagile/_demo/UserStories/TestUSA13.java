package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.13 Gère le calendrier de disponibilité spécifique pour chaque véhicule, définissant les
 * plages horaires ou jours ouverts à la location.
 */
public class TestUSA13 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());

      // S'assurer que nous avons des données de test
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com",
                "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Vehicule vehicule = vehiculeService.findVehiculeById(1L);
      if (vehicule == null) {
        Long idVehicule = vehiculeService
            .createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire", "Nice", 80.0, agent)
            .getId();
        vehicule = vehiculeService.findVehiculeById(idVehicule);
        System.out.println("✓ Véhicule créé avec ID: " + idVehicule);
      }

      // Tester US.A.13
      System.out.println("\n=== US.A.13: Gestion du calendrier de disponibilité ===");

      vehiculeService.createDisponibilite(agent, vehicule.getId(), LocalDate.of(2026, 10, 10),
          LocalDate.of(2026, 10, 15));
      System.out.println("✓ Disponibilité créée: 10/10/2026 - 15/10/2026");

      vehiculeService.createDisponibilite(agent, vehicule.getId(), LocalDate.of(2026, 11, 1),
          LocalDate.of(2026, 11, 30));
      System.out.println("✓ Disponibilité créée: 01/11/2026 - 30/11/2026");

      System.out.println("\nDisponibilités du véhicule:");
      List<Disponibilite> disponibilites =
          vehiculeService.getDisponibilitesByVehicule(vehicule.getId());
      for (Disponibilite dispo : disponibilites) {
        System.out
            .println(" - Du " + dispo.getDateDebut() + " au " + dispo.getDateFin());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
