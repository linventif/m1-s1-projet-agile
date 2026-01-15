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
 * US.A.11 Analyse le kilométrage parcouru et suggère des entretiens préventifs (ex: changer la
 * courroie tous les X kms) via des notifications.
 */
public class TestUSA11 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      ControlTechniqueService controlTechniqueService =
          new ControlTechniqueService(new VehiculeRepository());

      // S'assurer que nous avons des données de test
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

      // Tester US.A.11
      System.out.println("\n=== US.A.11: Recommandations d'entretien préventif ===");

      // Scénario 1: Véhicule avec faible kilométrage - pas de recommandations
      System.out.println("\n--- Scénario 1: Véhicule avec faible kilométrage ---");
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(),
          LocalDate.now().minusMonths(6), 10000, "Passé", "Véhicule en bon état");
      controlTechniqueService.updateControleTechnique(vehicule.getId(), null, null, 15000, null,
          null, null, null);
      System.out.println("Kilométrage dernier contrôle: 10000 km");
      System.out.println("Kilométrage actuel: 15000 km");
      System.out.println("Kilométrage parcouru: 5000 km");
      System.out.println("Recommandations: "
          + controlTechniqueService.getRecommandationsEntretienParKilometrage(vehicule));

      // Scénario 2: Véhicule nécessitant vidange (15,000+ km)
      System.out.println("\n--- Scénario 2: Véhicule nécessitant vidange (15000 km) ---");
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(),
          LocalDate.now().minusMonths(12), 30000, "Passé", "Véhicule en bon état");
      controlTechniqueService.updateControleTechnique(vehicule.getId(), null, null, 50000, null,
          null, null, null);
      System.out.println("Kilométrage dernier contrôle: 30000 km");
      System.out.println("Kilométrage actuel: 50000 km");
      System.out.println("Kilométrage parcouru: 20000 km");
      System.out.println("Recommandations:");
      controlTechniqueService.getRecommandationsEntretienParKilometrage(vehicule)
          .forEach(r -> System.out.println("  - " + r));

      // Scénario 3: Véhicule nécessitant entretien majeur (60,000+ km)
      System.out.println("\n--- Scénario 3: Véhicule nécessitant entretien majeur (60000+ km) ---");
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(),
          LocalDate.now().minusYears(2), 50000, "Passé", "Véhicule en bon état");
      controlTechniqueService.updateControleTechnique(vehicule.getId(), null, null, 120000, null,
          null, null, null);
      System.out.println("Kilométrage dernier contrôle: 50000 km");
      System.out.println("Kilométrage actuel: 120000 km");
      System.out.println("Kilométrage parcouru: 70000 km");
      System.out.println("Recommandations:");
      controlTechniqueService.getRecommandationsEntretienParKilometrage(vehicule)
          .forEach(r -> System.out.println("  - " + r));

      // Scénario 4: Véhicule avec kilométrage très élevé (100,000+ km)
      System.out.println(
          "\n--- Scénario 4: Véhicule avec kilométrage très élevé (100000+ km) ---");
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(),
          LocalDate.now().minusYears(3), 80000, "Passé", "Véhicule en bon état");
      controlTechniqueService.updateControleTechnique(vehicule.getId(), null, null, 200000, null,
          null, null, null);
      System.out.println("Kilométrage dernier contrôle: 80000 km");
      System.out.println("Kilométrage actuel: 200000 km");
      System.out.println("Kilométrage parcouru: 120000 km");
      System.out.println("Recommandations:");
      controlTechniqueService.getRecommandationsEntretienParKilometrage(vehicule)
          .forEach(r -> System.out.println("  - " + r));

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
