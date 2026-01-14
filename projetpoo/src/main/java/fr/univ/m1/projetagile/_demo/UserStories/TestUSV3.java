package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.V.3 Je veux pouvoir consulter le profil des agents de la plateforme, et les véhicules qu'ils
 * louent. (1)
 */
public class TestUSV3 {
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

      // Test US.V.3
      System.out.println("\n=== US.V.3: Consultation du profil d'un agent ===");
      AgentDTO agentProfile = agentService.getAgentProfile(agent);
      System.out.println("Profil de l'agent: ");
      System.out.println(" - Nom: " + agentProfile.getNom());
      System.out.println(" - Prénom: " + agentProfile.getPrenom());
      System.out.println(" - Email: " + agentProfile.getEmail());
      System.out.println(" - SIRET: " + agentProfile.getSiret());
      System.out.println(" - Véhicules disponibles: ");
      for (VehiculeDTO v : agentProfile.getVehicules()) {
        System.out.println("   - " + v.getMarque() + " " + v.getModele() + " " + v.getCouleur()
            + " " + v.getVille() + " " + v.getPrixJ() + "€/j" + " Note: " + v.getNoteMoyenne()
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
