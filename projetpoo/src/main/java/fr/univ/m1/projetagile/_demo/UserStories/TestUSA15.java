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
 * US.A.15 Gère le parrainage d'un nouvel agent. Le parrain reçoit des crédits pour options
 * payantes si le filleul met un véhicule en ligne et que celui-ci est loué au moins une fois.
 */
public class TestUSA15 {
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
      Agent agentParrain = agentService.findById(1L);
      if (agentParrain == null) {
        Long idParrain = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com",
                "motdepasse123")
            .getIdU();
        agentParrain = agentService.findById(idParrain);
        System.out.println("✓ Agent parrain créé avec ID: " + idParrain);
      }

      Agent agentFilleul = agentService.findById(2L);
      if (agentFilleul == null) {
        Long idFilleul = agentService
            .createAgentParticulier("Leroy", "Thomas", "tleroy@example.com", "motdepasse123")
            .getIdU();
        agentFilleul = agentService.findById(idFilleul);
        System.out.println("✓ Agent filleul créé avec ID: " + idFilleul);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Bernard", "Luc", "luc.bernard@example.com", "motdepasse123").getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      // Tester US.A.15
      System.out.println("\n=== US.A.15: Parrainage d'un nouvel agent ===");

      Parrainage parrainage = parrainageService.parrainer(agentParrain, agentFilleul);
      System.out.println("✓ Parrainage créé avec ID: " + parrainage.getId());

      // Le filleul crée un véhicule
      Vehicule vehiculeFilleul = vehiculeService.createVehicule(TypeV.voiture, "Renault", "Clio",
          "rouge", "Lyon", 45.0, agentFilleul);
      vehiculeService.createDisponibilite(agentFilleul, vehiculeFilleul.getId(), LocalDate.now(),
          LocalDate.now().plusDays(30));
      System.out.println("✓ Véhicule du filleul créé avec ID: " + vehiculeFilleul.getId());

      // Une location est effectuée sur le véhicule du filleul
      Location location = locationService.creerLocation(LocalDateTime.now(),
          LocalDateTime.now().plusDays(5), vehiculeFilleul, loueur);
      System.out.println("✓ Location effectuée avec ID: " + location.getId());

      // Vérifier le crédit du parrain
      Crédit credit = creditService.getCredit(agentParrain.getIdU());
      System.out.println("Crédit du parrain: " + credit.getCredit() + "€");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
