package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.1 Je veux pouvoir ajouter, modifier ou supprimer les véhicules mis à disposition. (3)
 */
public class TestUSA1 {
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

      // Tester US.A.1
      System.out.println("\n=== US.A.1: Ajout, modification et suppression de véhicules ===");

      // Ajouter un véhicule
      Vehicule vehicule =
          vehiculeService.createVehicule(TypeV.voiture, "Audi", "Q3", "noire", "Toulouse", 100.0,
              agent);
      System.out.println("✓ Véhicule créé: " + vehicule.getMarque() + " " + vehicule.getModele()
          + " (ID: " + vehicule.getId() + ")");

      // Modifier le véhicule
      vehiculeService.updateVehiculeMarque(agent, vehicule.getId(), "Mercedes");
      vehiculeService.updateVehiculeModele(agent, vehicule.getId(), "Classe C");
      Vehicule vehiculeModifie = vehiculeService.findVehiculeById(vehicule.getId());
      System.out.println("✓ Véhicule modifié: " + vehiculeModifie.getMarque() + " "
          + vehiculeModifie.getModele());

      // Supprimer le véhicule
      vehiculeService.deleteVehicule(vehicule.getId());
      System.out.println("✓ Véhicule supprimé (ID: " + vehicule.getId() + ")");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
