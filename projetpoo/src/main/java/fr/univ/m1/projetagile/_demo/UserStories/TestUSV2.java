package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.V.2. Je veux pouvoir appliquer des filtres sur les véhicules que je vois. (2)
 */
public class TestUSV2 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
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

      // Test US.V.2
      System.out.println("\n=== US.V.2: Application de filtres sur les véhicules ===");
      System.out.println("Filtres appliqués: Ville=Paris, Marque=Peugeot, Modèle=308, Type=voiture");
      System.out.println();
      
      List<VehiculeDTO> vehiculesFiltres = vehiculeService.searchVehiculesWithFilters(null, null,
          "Paris", "Peugeot", "308", null, null, null, TypeV.voiture, null);
      
      System.out.println("Nombre de véhicules trouvés: " + vehiculesFiltres.size());

      for (VehiculeDTO v : vehiculesFiltres) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + v.getMarque() + " " + v.getModele() + " (" + v.getCouleur() + ")");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Lieu: " + v.getVille());
        System.out.println("Type: " + v.getType());
        System.out.println("Prix: " + v.getPrixJ() + "€/jour");
        System.out.println("Note moyenne: " + String.format("%.2f", v.getNoteMoyenne()) + "/10");
        System.out.println("Disponible: " + (v.isDisponible() ? "✓ Oui" : "✗ Non"));
        
        System.out.println("\nPériodes de disponibilité:");
        if (v.getDatesDispo() != null && !v.getDatesDispo().isEmpty()) {
          for (int i = 0; i < v.getDatesDispo().size(); i++) {
            LocalDate[] periode = v.getDatesDispo().get(i);
            if (periode != null && periode.length == 2) {
              long jours = ChronoUnit.DAYS.between(periode[0], periode[1]);
              System.out.println("  " + (i + 1) + ". Du " + periode[0] + " au " + periode[1] 
                  + " (" + jours + " jours)");
            }
          }
        } else {
          System.out.println("  Aucune période de disponibilité définie");
        }
      }
      
      System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
