package fr.univ.m1.projetagile._demo;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.DisponibiliteService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

public class DisponibiliteDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      DisponibiliteService dispoService = new DisponibiliteService();

      AgentParticulier agent =
          agentService.createAgentParticulier("Dispo", "Owner", "dispo" + suffix + "@demo", "pass");
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Citroen", "C3", "Rouge",
          "Lille", 28.0, agent);

      LocalDate base = LocalDate.now();
      Disponibilite d1 = dispoService.creerDisponibilite(vehicule, base, base.plusDays(10));
      Disponibilite d2 =
          dispoService.creerDisponibilite(vehicule, base.plusDays(12), base.plusDays(20));
      Disponibilite d3 =
          dispoService.creerDisponibilite(vehicule, base.plusDays(25), base.plusDays(35));
      Disponibilite d4 =
          dispoService.creerDisponibilite(vehicule, base.plusDays(40), base.plusDays(50));

      System.out.println("Disponibilités créées :");
      System.out.println(d1);
      System.out.println(d2);
      System.out.println(d3);
      System.out.println(d4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
