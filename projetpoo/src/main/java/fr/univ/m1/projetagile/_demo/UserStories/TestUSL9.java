package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LocationService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.parrainage.entity.Crédit;
import fr.univ.m1.projetagile.parrainage.entity.Parrainage;
import fr.univ.m1.projetagile.parrainage.persistence.CreditRepository;
import fr.univ.m1.projetagile.parrainage.persistence.ParrainageRepository;
import fr.univ.m1.projetagile.parrainage.service.CreditService;
import fr.univ.m1.projetagile.parrainage.service.ParrainageService;

/**
 * US.L.9 Gère le parrainage d'un nouveau loueur. Si le filleul effectue au moins une location, le
 * parrain reçoit un crédit utilisable pour ses futures locations.
 */
public class TestUSL9 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      ParrainageService parrainageService = new ParrainageService(new ParrainageRepository());
      CreditService creditService = new CreditService(new CreditRepository());

      // S'assurer que nous avons des données de test
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Loueur loueurParrain = loueurService.findById(1L);
      if (loueurParrain == null) {
        Long idParrain = loueurService
            .createLoueur("Martin", "Sophie", "sophie.martin@example.com", "motdepasse123")
            .getIdU();
        loueurParrain = loueurService.findById(idParrain);
        System.out.println("✓ Loueur parrain créé avec ID: " + idParrain);
      }

      Loueur loueurFilleul = loueurService.findById(2L);
      if (loueurFilleul == null) {
        Long idFilleul = loueurService
            .createLoueur("Bernard", "Luc", "luc.bernard@example.com", "motdepasse123").getIdU();
        loueurFilleul = loueurService.findById(idFilleul);
        System.out.println("✓ Loueur filleul créé avec ID: " + idFilleul);
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

      // Test US.L.9
      System.out.println("\n=== US.L.9: Parrainage d'un nouveau loueur ===");
      Parrainage parrainage = parrainageService.parrainer(loueurParrain, loueurFilleul);
      System.out.println("✓ Parrainage créé avec ID: " + parrainage.getId());

      // Le filleul effectue une location
      Location location = locationService.creerLocation(LocalDateTime.now().plusDays(10),
          LocalDateTime.now().plusDays(15), vehicule, loueurFilleul);
      System.out.println("✓ Location du filleul créée avec ID: " + location.getId());

      // Vérifier le crédit du parrain par le biais du service de crédit
      Crédit credit = creditService.getCredit(loueurParrain.getIdU());
      System.out.println("Crédit du parrain: " + credit.getCredit() + "€");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
