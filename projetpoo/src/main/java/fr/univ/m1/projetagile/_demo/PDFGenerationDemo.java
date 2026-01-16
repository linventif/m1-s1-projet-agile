package fr.univ.m1.projetagile._demo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LocationService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * Démonstration de la génération de PDF pour un contrat de location.
 */
public class PDFGenerationDemo {

  public static void main(String[] args) {
    System.out.println("=== Démonstration de génération de PDF ===\n");

    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      // Initialisation des services
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new fr.univ.m1.projetagile.core.persistence.AgentRepository());
      LoueurService loueurService = new LoueurService(new fr.univ.m1.projetagile.core.persistence.LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());

      // Générer un identifiant unique pour éviter les conflits
      String uniqueId = String.valueOf(System.currentTimeMillis());

      // 1. Créer un agent (propriétaire du véhicule)
      Agent agent = agentService.createAgentParticulier("Dupont", "Jean",
          "jean.dupont." + uniqueId + "@example.com", "password123");
      System.out.println("✓ Agent créé : " + agent.getNomComplet() + " (ID: " + agent.getIdU() + ")");

      // 2. Créer un véhicule
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Peugeot", "308", "Bleu",
          "Paris", 45.0, agent);
      System.out.println(
          "✓ Véhicule créé : " + vehicule.getMarque() + " " + vehicule.getModele() + " (ID: "
              + vehicule.getId() + ")");

      // 3. Créer une disponibilité pour le véhicule
      vehiculeService.createDisponibilite(agent, vehicule.getId(), LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Disponibilité créée pour le véhicule");

      // 4. Créer un loueur
      Loueur loueur = loueurService.createLoueur("Martin", "Sophie",
          "sophie.martin." + uniqueId + "@example.com", "password456");
      loueur.setAdresse("25 Avenue des Champs, 75008 Paris");
      System.out.println("✓ Loueur créé : " + loueur.getNomComplet() + " (ID: " + loueur.getIdU() + ")");

      // 5. Créer une location
      LocalDateTime dateDebut = LocalDateTime.now().plusDays(1);
      LocalDateTime dateFin = LocalDateTime.now().plusDays(4);
      Location location = locationService.creerLocation(dateDebut, dateFin, vehicule, loueur);
      System.out.println("✓ Location créée : ID = " + location.getId());
      System.out.println("  - Date début : " + dateDebut.toLocalDate());
      System.out.println("  - Date fin : " + dateFin.toLocalDate());
      System.out.println("  - Durée : " + location.getNombreJours() + " jours");
      System.out.println("  - Statut : " + location.getStatut());
      System.out.println("  - Prix total : " + String.format("%.2f", locationService.getPrixLocation(location)) + " EUR");

      // 6. Générer le PDF
      System.out.println("\n=== Génération du contrat PDF ===");
      String pdfPath = locationService.generatePDF(location);
      System.out.println("✓ PDF généré avec succès : " + pdfPath);

      System.out.println("\n=== Démonstration terminée avec succès ===");
      System.out.println("Vous pouvez consulter le contrat PDF dans le répertoire : pdf/");

    } catch (IOException e) {
      System.err.println("✗ Erreur lors de la génération du PDF : " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("✗ Erreur lors de la démonstration : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
      System.out.println("\n✓ Connexion DB fermée");
    }
  }

  /**
   * Méthode alternative : générer un PDF pour une location existante
   * 
   * @param locationId l'ID d'une location existante dans la base de données
   */
  public static void generatePDFForExistingLocation(Long locationId) {
    System.out.println("=== Génération de PDF pour une location existante ===\n");

    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      LocationRepository locationRepo = new LocationRepository();
      LocationService locationService = new LocationService(locationRepo);

      Location location = locationService.findLocationById(locationId);

      if (location == null) {
        System.err.println("✗ Erreur : Aucune location trouvée avec l'ID " + locationId);
        return;
      }

      System.out.println("✓ Location trouvée : " + location);
      System.out.println("\nGénération du contrat PDF...");

      String pdfPath = locationService.generatePDF(location);
      System.out.println("✓ PDF généré avec succès : " + pdfPath);

    } catch (IOException e) {
      System.err.println("✗ Erreur lors de la génération du PDF : " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("✗ Erreur : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
      System.out.println("\n✓ Connexion DB fermée");
    }
  }
}
