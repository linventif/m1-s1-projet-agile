package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

public class VehiculeDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());

      AgentParticulier proprietaire = agentService.createAgentParticulier("Auto", "Owner",
          "owner" + suffix + "@vehicule.demo", "pass");

      Vehicule v1 = vehiculeService.createVehicule(TypeV.voiture, "Peugeot", "208", "Bleu", "Paris",
          40.0, proprietaire);
      Vehicule v2 = vehiculeService.createVehicule(TypeV.moto, "Honda", "CB500", "Noir", "Lyon",
          32.0, proprietaire);
      Vehicule v3 = vehiculeService.createVehicule(TypeV.camion, "Renault", "Master", "Blanc",
          "Nantes", 75.0, proprietaire);
      Vehicule v4 = vehiculeService.createVehicule(TypeV.voiture, "Tesla", "Model 3", "Gris",
          "Bordeaux", 95.0, proprietaire);

      System.out.println("Véhicules créés :");
      System.out.println(v1);
      System.out.println(v2);
      System.out.println(v3);
      System.out.println(v4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
