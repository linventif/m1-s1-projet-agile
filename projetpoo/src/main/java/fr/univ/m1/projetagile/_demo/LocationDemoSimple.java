package fr.univ.m1.projetagile._demo;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
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

public class LocationDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      LocationRepository locationRepository = new LocationRepository();
      LocationService locationService = new LocationService(locationRepository);

      AgentParticulier agent = agentService.createAgentParticulier("Loc", "Owner",
          "locowner" + suffix + "@demo", "pass", "340" + String.format("%08d", suffix));
      Loueur loueur =
          loueurService.createLoueur("Loc", "User", "locuser" + suffix + "@demo", "pass");
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Ford", "Focus", "Noir",
          "Paris", 55.0, agent);

      LocalDateTime start = LocalDateTime.now().plusDays(1);
      Location l1 = locationService.creerLocation(start, start.plusDays(2), vehicule, loueur);
      Location l2 =
          locationService.creerLocation(start.plusDays(5), start.plusDays(8), vehicule, loueur);
      Location l3 =
          locationService.creerLocation(start.plusDays(12), start.plusDays(15), vehicule, loueur);
      Location l4 =
          locationService.creerLocation(start.plusDays(20), start.plusDays(23), vehicule, loueur);

      System.out.println("Locations créées :");
      System.out.println(l1);
      System.out.println(l2);
      System.out.println(l3);
      System.out.println(l4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
