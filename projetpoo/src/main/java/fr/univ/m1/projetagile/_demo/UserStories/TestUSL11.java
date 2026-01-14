package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
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
import fr.univ.m1.projetagile.parking.entity.Parking;
import fr.univ.m1.projetagile.parking.persistence.ParkingRepository;
import fr.univ.m1.projetagile.parking.service.ParkingService;

/**
 * US.L.11 Un loueur peut choisir un parking en tant que lieu de depôt
 */
public class TestUSL11 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      SouscriptionOptionService souscriptionOptionService = new SouscriptionOptionService();
      ParkingService parkingService = new ParkingService(new ParkingRepository());

      // Ensure we have test data - Agent with parking option
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Martin", "Sophie", "smartin@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      // Ensure agent has the parking option
      Options optionParking = souscriptionOptionService.findOptionByNom("Option Parking");
      if (optionParking == null) {
        optionParking = new Options("Option Parking", 15.0);
        optionParking = souscriptionOptionService.saveOption(optionParking);
        System.out.println("✓ Option Parking créée avec ID: " + optionParking.getId());
      }

      boolean hasOption = souscriptionOptionService.getOptionsActives(agent).stream()
          .anyMatch(opt -> opt.getOption().getNomOption().equals("Option Parking"));

      if (!hasOption) {
        souscriptionOptionService.souscrireOption(agent.getIdU(), optionParking.getId(), 1, true);
        agent = agentService.findById(agent.getIdU()); // Refresh agent
        System.out.println("✓ Agent souscrit à l'option Parking");
      }

      // Create a vehicle with availability
      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "Renault", "Megane", "grise", "Paris", 45.0, agent)
          .getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);


      // Ensure we have test data - Loueur
      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dupont", "Pierre", "pierre.dupont@example.com", "motdepasse123")
            .getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      // Create or find a parking
      List<Parking> parkings = parkingService.getParkingsByVille("Paris");
      Parking parking;
      if (parkings.isEmpty()) {
        parking = parkingService.createParking("Parking Central", "15 Rue de la République",
            "Paris", "75001", 5.0);
        System.out.println("✓ Parking créé: " + parking.getNom() + " - " + parking.getAdresse());
      } else {
        parking = parkings.get(0);
        System.out.println("✓ Parking trouvé: " + parking.getNom() + " - " + parking.getAdresse());
      }

      // Test US.L.11
      System.out.println("\n=== US.L.11: Recherche de véhicules avec option Parking ===");

      // Search for vehicles with parking option available
      LocalDate dateDebut = LocalDate.now().plusDays(5);
      LocalDate dateFin = LocalDate.now().plusDays(10);

      List<VehiculeDTO> vehiculesAvecParking = vehiculeService.searchVehiculesWithFilters(dateDebut,
          dateFin, "Paris", null, null, null, null, null, null, true);

      System.out.println("Dates de location: du " + dateDebut + " au " + dateFin);
      System.out.println("Ville: Paris");
      System.out.println("Filtre: Véhicules avec option Parking activée");
      System.out.println("\nNombre de véhicules trouvés: " + vehiculesAvecParking.size());

      if (!vehiculesAvecParking.isEmpty()) {
        System.out.println("\nVéhicules disponibles avec option Parking:");
        for (VehiculeDTO v : vehiculesAvecParking) {
          System.out.println("  - " + v.getMarque() + " " + v.getModele() + " (" + v.getCouleur()
              + ") - " + v.getPrixJ() + "€/jour");
        }
      }

      // Create a location with parking as lieu de dépôt
      System.out.println("\n=== Création d'une location avec Parking comme lieu de dépôt ===");

      Location location = locationService.creerLocation(LocalDateTime.now().plusDays(5),
          LocalDateTime.now().plusDays(10), parking, vehicule, loueur);

      System.out.println("✓ Location créée avec ID: " + location.getId());
      System.out.println("Véhicule: " + vehicule.getMarque() + " " + vehicule.getModele());
      System.out.println("Loueur: " + loueur.getNom() + " " + loueur.getPrenom());
      System.out.println("Lieu de dépôt: " + parking.getNom() + " - " + parking.getAdresse());
      System.out.println("Coût du parking: " + parking.getCoutSupp() + "€");
      System.out.println("Statut: " + location.getStatut());
      System.out.println("Prix total: " + locationService.getPrixLocation(location) + "€");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
