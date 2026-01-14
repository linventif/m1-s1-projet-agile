package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.VerificationLocation.service.VerificationService;
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
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.L.10 Enregistre le kilométrage au départ et au retour du véhicule. Nécessite l'upload d'une
 * photo du tableau de bord comme preuve justificative.
 */
public class TestUSL10 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      VerificationService verificationService =
          new VerificationService(new VerificationRepository(), new LocationRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123").getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "Peugeot", "308", "blanche", "Paris", 50.0, agent).getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);

      // Create a location for testing
      Location location = locationService.creerLocation(LocalDateTime.now().plusDays(5),
          LocalDateTime.now().plusDays(10), vehicule, loueur);
      location.setStatut(StatutLocation.ACCEPTE);
      System.out.println("✓ Location créée avec ID: " + location.getId());

      // Test US.L.10
      System.out.println("\n=== US.L.10: Enregistrement du kilométrage ===");
      System.out.println("Kilométrage au départ: 1560 km");
      verificationService.creerVerification(location.getId(), 1560);
      System.out.println("✓ Vérification au départ enregistrée");

      System.out.println("\nKilométrage au retour: 1800 km");
      locationService.terminer(location, 1800, "photo_tableau_bord.jpg");
      System.out.println("✓ Kilométrage au retour enregistré avec photo");

      // Fetch verification from database to verify it was saved correctly
      System.out.println("\n=== Vérification depuis la base de données ===");
      fr.univ.m1.projetagile.VerificationLocation.entity.Verification verificationFromDB =
          verificationService.getVerificationByLocationId(location.getId());

      if (verificationFromDB != null) {
        System.out.println("✓ Vérification récupérée depuis la base de données");
        System.out.println("ID de la vérification: " + verificationFromDB.getId());
        System.out.println("ID de la location: " + verificationFromDB.getLocation().getId());
        System.out
            .println("Kilométrage au départ: " + verificationFromDB.getKilometrageDebut() + " km");
        System.out
            .println("Kilométrage au retour: " + verificationFromDB.getKilometrageFin() + " km");
        System.out.println("Distance parcourue: "
            + (verificationFromDB.getKilometrageFin() - verificationFromDB.getKilometrageDebut())
            + " km");
        System.out.println("Photo du tableau de bord: " + verificationFromDB.getPhoto());

        // Check if verification is complete
        if (verificationFromDB.getKilometrageDebut() != null
            && verificationFromDB.getKilometrageFin() != null) {
          System.out.println("✓ Vérification complète (départ + retour)");
        } else {
          System.out.println("✗ Vérification incomplète");
        }
      } else {
        System.out.println("✗ ERREUR: Aucune vérification trouvée pour cette location");
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
