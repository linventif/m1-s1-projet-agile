package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.entretienTechnique.entity.TypeTechnique;
import fr.univ.m1.projetagile.entretienTechnique.persistence.EntretienTechniqueRepository;
import fr.univ.m1.projetagile.entretienTechnique.persistence.TypeTechniqueRepository;
import fr.univ.m1.projetagile.entretienTechnique.service.EntretienTechniqueService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.10 Permet de journaliser les opérations de maintenance effectuées (ex: courroie, pneus)
 * pour garder un historique technique du véhicule.
 */
public class TestUSA10 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      EntretienTechniqueService entretienService =
          new EntretienTechniqueService(new TypeTechniqueRepository(),
              new EntretienTechniqueRepository(), new VehiculeRepository());

      // Ensure we have test data
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
        vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
            LocalDate.now().plusDays(60));
        System.out.println("✓ Véhicule créé avec ID: " + idVehicule);
      }

      // Test US.A.10
      System.out.println("\n=== US.A.10: Journalisation des opérations de maintenance ===");

      // Fetch or create TypeTechnique entities
      TypeTechnique typeCourroie = entretienService.getTypeTechniqueByNom("Courroie");
      if (typeCourroie == null) {
        typeCourroie = entretienService.creerTypeTechnique("Courroie", 15000);
        System.out.println("✓ Type technique 'Courroie' créé");
      }

      TypeTechnique typePneus = entretienService.getTypeTechniqueByNom("Pneus");
      if (typePneus == null) {
        typePneus = entretienService.creerTypeTechnique("Pneus", 30000);
        System.out.println("✓ Type technique 'Pneus' créé");
      }

      TypeTechnique typeBougies = entretienService.getTypeTechniqueByNom("Bougies");
      if (typeBougies == null) {
        typeBougies = entretienService.creerTypeTechnique("Bougies", 60000);
        System.out.println("✓ Type technique 'Bougies' créé");
      }

      TypeTechnique typeFreins = entretienService.getTypeTechniqueByNom("Plaques de frein");
      if (typeFreins == null) {
        typeFreins = entretienService.creerTypeTechnique("Plaques de frein", 80000);
        System.out.println("✓ Type technique 'Plaques de frein' créé");
      }

      entretienService.creerEntretienTechnique(vehicule.getId(), typeCourroie.getId(),
          LocalDate.now());
      System.out.println("✓ Entretien courroie enregistré");

      entretienService.creerEntretienTechnique(vehicule.getId(), typePneus.getId(),
          LocalDate.now());
      System.out.println("✓ Entretien pneus enregistré");

      entretienService.creerEntretienTechnique(vehicule.getId(), typeBougies.getId(),
          LocalDate.now());
      System.out.println("✓ Entretien bougies enregistré");

      entretienService.creerEntretienTechnique(vehicule.getId(), typeFreins.getId(),
          LocalDate.now());
      System.out.println("✓ Entretien freins enregistré");

      // Display latest entretien technique for the vehicle
      System.out.println("\n=== Dernier entretien technique du véhicule ===");
      fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique dernierEntretien =
          entretienService.getDernierEntretienTechnique(vehicule.getId());
      if (dernierEntretien != null) {
        System.out.println("Type: " + dernierEntretien.getTypeTechnique().getNom());
        System.out.println("Date: " + dernierEntretien.getDate());
        System.out.println("Véhicule: " + dernierEntretien.getVehicule().getMarque() + " "
            + dernierEntretien.getVehicule().getModele());
      }

      // Display latest entretien for each type
      System.out.println("\n=== Dernier entretien par type technique ===");
      fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique dernierCourroie =
          entretienService.getDernierEntretienTechniqueByType(vehicule.getId(),
              typeCourroie.getId());
      if (dernierCourroie != null) {
        System.out.println("Courroie: " + dernierCourroie.getDate());
      }

      fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique dernierPneus =
          entretienService.getDernierEntretienTechniqueByType(vehicule.getId(), typePneus.getId());
      if (dernierPneus != null) {
        System.out.println("Pneus: " + dernierPneus.getDate());
      }

      fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique dernierBougies =
          entretienService.getDernierEntretienTechniqueByType(vehicule.getId(),
              typeBougies.getId());
      if (dernierBougies != null) {
        System.out.println("Bougies: " + dernierBougies.getDate());
      }

      fr.univ.m1.projetagile.entretienTechnique.entity.EntretienTechnique dernierFreins =
          entretienService.getDernierEntretienTechniqueByType(vehicule.getId(), typeFreins.getId());
      if (dernierFreins != null) {
        System.out.println("Plaques de frein: " + dernierFreins.getDate());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
