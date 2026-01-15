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
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.service.SouscriptionOptionService;

/**
 * US.A.4 Si option, je veux pouvoir accepter manuellement les contrats de location pré signé par un
 * loueur. (2)
 */
public class TestUSA4 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      SouscriptionOptionService souscriptionOptionService = new SouscriptionOptionService();

      // S'assurer que nous avons des données de test - Agent
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      // S'assurer que nous avons des données de test - Loueur
      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123").getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      // S'assurer que nous avons des données de test - Véhicule avec disponibilité
      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "Peugeot", "308", "blanche", "Paris", 50.0, agent).getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);


      // S'assurer que l'agent a l'option d'acceptation manuelle
      Options optionAcceptationManuelle =
          souscriptionOptionService.findOptionByNom("Accepter les contrats manuellement");
      if (optionAcceptationManuelle == null) {
        optionAcceptationManuelle = new Options("Accepter les contrats manuellement", 19.99);
        optionAcceptationManuelle = souscriptionOptionService.saveOption(optionAcceptationManuelle);
        System.out.println("✓ Option 'Accepter les contrats manuellement' créée avec ID: "
            + optionAcceptationManuelle.getId());
      }

      // Souscrire l'agent à l'option d'acceptation manuelle s'il n'y est pas déjà souscrit
      boolean hasOption = souscriptionOptionService.getOptionsActives(agent).stream().anyMatch(
          opt -> opt.getOption().getNomOption().equals("Accepter les contrats manuellement"));
      if (!hasOption) {
        souscriptionOptionService.souscrireOption(agent.getIdU(), optionAcceptationManuelle.getId(),
            1, true);
        agent = agentService.findById(agent.getIdU()); // Rafraîchir l'agent
        System.out.println("✓ Agent souscrit à l'option 'Accepter les contrats manuellement'");
      }

      // Tester US.A.4
      System.out.println("\n=== US.A.4: Acceptation manuelle d'un contrat de location ===");

      // Créer une location
      Location location = locationService.creerLocation(LocalDateTime.now().plusDays(5),
          LocalDateTime.now().plusDays(10), vehicule, loueur);
      System.out.println("✓ Location créée avec ID: " + location.getId());
      System.out.println("Statut de la location AVANT acceptation: " + location.getStatut());

      // Accepter la location manuellement
      locationService.accepterLocationManuellement(location.getId(), agent);
      location = locationService.findLocationById(location.getId()); // Rafraîchir la location
      System.out.println("✓ Location acceptée manuellement par l'agent");
      System.out.println("Statut de la location APRÈS acceptation: " + location.getStatut());

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
