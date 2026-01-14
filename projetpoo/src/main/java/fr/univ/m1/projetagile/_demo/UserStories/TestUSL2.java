package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import fr.univ.m1.projetagile.assurance.entity.Assurance;
import fr.univ.m1.projetagile.assurance.entity.GrilleTarif;
import fr.univ.m1.projetagile.assurance.entity.SouscriptionAssurance;
import fr.univ.m1.projetagile.assurance.service.AssuranceService;
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

/**
 * US.L.2 Je veux pouvoir louer un véhicule. Cela inclut : choisir une assurance, choisir le lieu de
 * dépose du véhicule si option disponible, choisir les dates de location. (3)
 */
public class TestUSL2 {

  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      // Initialize services
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      AssuranceService assuranceService = new AssuranceService();

      // Ensure we have test data
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Dupont", "Jean", "jdupont@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Martin", "Sophie", "sophie.martin@example.com", "motdepasse123")
            .getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }


      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "Renault", "Clio", "rouge", "Paris", 45.0, agent).getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);

      // Create availability for the vehicle
      vehiculeService.createDisponibilite(agent, vehicule.getId(), LocalDate.now(),
          LocalDate.now().plusDays(30));
      System.out.println("✓ Disponibilité créée pour le véhicule");

      // Create insurance with pricing grid
      GrilleTarif grille = assuranceService.creerGrille();
      System.out.println("✓ Grille tarifaire créée avec ID: " + grille.getId());

      // Add vehicle pricing to grid
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "Renault", 5.0);
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "Peugeot", 5.0);
      assuranceService.ajouterTarifVehicule(grille, TypeV.voiture, "Clio", 10.0);
      System.out.println("✓ Tarifs véhicules ajoutés à la grille");

      // Add insurance options to grid
      assuranceService.ajouterTarifOption(grille, "Protection vol",
          "Protection contre le vol du véhicule", 3.0);
      assuranceService.ajouterTarifOption(grille, "Protection bris de glace",
          "Couverture des dommages aux vitres", 2.0);
      assuranceService.ajouterTarifOption(grille, "Assistance 24h/24",
          "Assistance routière disponible 24h/24", 4.0);
      System.out.println("✓ Options d'assurance ajoutées à la grille");

      // Create insurance
      Assurance assurance = assuranceService.creerAssurance("Assurance Tous Risques", grille);
      System.out
          .println("✓ Assurance créée: " + assurance.getNom() + " (ID: " + assurance.getId() + ")");

      // Test US.L.2
      System.out.println("\n=== US.L.2: Création d'une location avec assurance ===");

      // Create location with insurance and options
      LocalDateTime dateDebut = LocalDateTime.now();
      LocalDateTime dateFin = LocalDateTime.now().plusDays(10);

      Location location = locationService.creerLocation(dateDebut, dateFin, null, vehicule, loueur,
          assurance, Arrays.asList("Protection vol", "Assistance 24h/24"));

      System.out.println("✓ Location créée avec ID: " + location.getId());

      // Display location details
      System.out.println("\n=== Détails de la location ===");
      System.out.println("Date de début: " + location.getDateDebut());
      System.out.println("Date de fin: " + location.getDateFin());
      System.out.println("Lieu de dépôt: "
          + (location.getLieuDepot() != null ? location.getLieuDepot().getAdresse()
              : "Lieu d'origine"));
      System.out.println("Véhicule: " + location.getVehicule().getMarque() + " "
          + location.getVehicule().getModele());
      System.out.println("Couleur: " + location.getVehicule().getCouleur());
      System.out.println("Ville: " + location.getVehicule().getVille());
      System.out.println("Prix du véhicule par jour: " + location.getVehicule().getPrixJ() + " €");
      System.out.println(
          "Loueur: " + location.getLoueur().getNom() + " " + location.getLoueur().getPrenom());

      // Retrieve and display insurance information
      SouscriptionAssurance souscription =
          assuranceService.getSouscriptionParLocation(location.getId());
      if (souscription != null) {
        System.out.println("Assurance: " + souscription.getAssurance().getNom());
        if (!souscription.getOptions().isEmpty()) {
          System.out
              .println("Options souscrites: " + String.join(", ", souscription.getOptions()));
        } else {
          System.out.println("Options souscrites: Aucune");
        }
      } else {
        System.out.println("Assurance: Aucune");
      }

      System.out.println("Statut: " + location.getStatut());

      // Calculate and display total price
      double prixLocation = locationService.getPrixLocation(location);
      System.out
          .println("\n✓ Prix total de la location: " + String.format("%.2f", prixLocation) + " €");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
