package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
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

/**
 * US.A.12 Bascule le statut d'un véhicule en "Pause" pour empêcher temporairement toute nouvelle
 * réservation sans supprimer l'annonce.
 */
public class TestUSA12 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire", "Nice", 80.0, agent).getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);

      // Test US.A.12
      System.out.println("\n=== US.A.12: Mise en pause d'un véhicule ===");
      System.out.println("Disponibilité actuelle: " + vehicule.isDisponible());

      vehiculeService.updateVehiculeDisponibilite(agent, vehicule.getId(), false);
      System.out.println("✓ Véhicule mis en pause");

      vehicule = vehiculeService.findVehiculeById(vehicule.getId());
      System.out.println("Nouvelle disponibilité: " + vehicule.isDisponible());

      // Test: Try to create a location when vehicle is paused
      System.out
          .println("\n=== Test: Tentative de création de location avec véhicule en pause ===");

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123").getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      try {
        locationService.creerLocation(LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3), vehicule, loueur);
        System.out.println("✗ ERREUR: La location a été créée alors que le véhicule est en pause!");
      } catch (IllegalStateException e) {
        System.out.println("✓ Location refusée comme attendu: " + e.getMessage());
      }

      // Reactivate vehicle and try again
      System.out.println("\n=== Réactivation du véhicule ===");
      vehiculeService.updateVehiculeDisponibilite(agent, vehicule.getId(), true);
      vehicule = vehiculeService.findVehiculeById(vehicule.getId());
      System.out.println("Disponibilité après réactivation: " + vehicule.isDisponible());

      try {
        locationService.creerLocation(LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3), vehicule, loueur);
        System.out.println("✓ Location créée avec succès après réactivation du véhicule");
      } catch (IllegalStateException e) {
        System.out.println("✗ ERREUR: La location a échoué alors que le véhicule est disponible: "
            + e.getMessage());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
