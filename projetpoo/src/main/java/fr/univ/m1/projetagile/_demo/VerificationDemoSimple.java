package fr.univ.m1.projetagile._demo;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.VerificationLocation.entity.Verification;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.VerificationLocation.service.VerificationService;
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

public class VerificationDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      LocationRepository locationRepository = new LocationRepository();
      LocationService locationService = new LocationService(locationRepository);
      VerificationRepository verificationRepository = new VerificationRepository();
      VerificationService verificationService =
          new VerificationService(verificationRepository, locationRepository);

      AgentParticulier agent = agentService.createAgentParticulier("Verif", "Agent",
          "verifagent" + suffix + "@demo", "pass", "342" + String.format("%08d", suffix));
      Loueur loueur =
          loueurService.createLoueur("Verif", "Loueur", "verifloueur" + suffix + "@demo", "pass");

      Vehicule v1 = vehiculeService.createVehicule(TypeV.voiture, "Renault", "Clio", "Bleu",
          "Paris", 33.0, agent);
      Vehicule v2 =
          vehiculeService.createVehicule(TypeV.voiture, "VW", "Golf", "Gris", "Paris", 45.0, agent);
      Vehicule v3 = vehiculeService.createVehicule(TypeV.moto, "Yamaha", "MT07", "Noir", "Paris",
          38.0, agent);
      Vehicule v4 = vehiculeService.createVehicule(TypeV.camion, "Iveco", "Daily", "Blanc", "Paris",
          80.0, agent);

      LocalDateTime base = LocalDateTime.now().plusDays(1);
      Location loc1 = locationService.creerLocation(base, base.plusDays(2), v1, loueur);
      Location loc2 = locationService.creerLocation(base.plusDays(3), base.plusDays(5), v2, loueur);
      Location loc3 = locationService.creerLocation(base.plusDays(6), base.plusDays(8), v3, loueur);
      Location loc4 =
          locationService.creerLocation(base.plusDays(9), base.plusDays(11), v4, loueur);

      Verification ver1 = verificationService.creerVerification(loc1.getId(), 1000);
      Verification ver2 = verificationService.creerVerification(loc2.getId(), 2000);
      Verification ver3 = verificationService.creerVerification(loc3.getId(), 3000);
      Verification ver4 = verificationService.creerVerification(loc4.getId(), 4000);

      System.out.println("Vérifications créées :");
      System.out.println(ver1);
      System.out.println(ver2);
      System.out.println(ver3);
      System.out.println(ver4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
