package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.controleTechnique.service.ControlTechniqueService;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.8 Permet à l'agent de saisir et mettre à jour la date et le statut du dernier contrôle
 * technique du véhicule.
 */
public class TestUSA8 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      ControlTechniqueService controlTechniqueService =
          new ControlTechniqueService(new VehiculeRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Vehicule vehicule = vehiculeService.findVehiculeById(1L);
      if (vehicule == null) {
        Long idVehicule = vehiculeService
            .createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire", "Nice", 80.0, agent).getId();
        vehicule = vehiculeService.findVehiculeById(idVehicule);
        vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
            LocalDate.now().plusDays(60));
        System.out.println("✓ Véhicule créé avec ID: " + idVehicule);
      }

      // Test US.A.8
      System.out.println("\n=== US.A.8: Enregistrement du contrôle technique ===");
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(), LocalDate.now(), 10000,
          "Passé", "Aucun commentaire");
      System.out.println("✓ Contrôle technique enregistré");
      System.out.println("Rapport: " + controlTechniqueService.genererRapportControle(vehicule));

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
