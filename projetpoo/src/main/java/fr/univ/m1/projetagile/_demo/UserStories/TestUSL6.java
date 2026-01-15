package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.LoueurDTO;
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
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.L.6 Je veux pouvoir consulter mon profil, y changer des informations, voir mes précédentes
 * locations. (1)
 */
public class TestUSL6 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());

      // S'assurer que nous avons des données de test
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Loueur loueur = loueurService.findById(1L);
      if (loueur == null) {
        Long idLoueur = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123").getIdU();
        loueur = loueurService.findById(idLoueur);
        System.out.println("✓ Loueur créé avec ID: " + idLoueur);
      }

      Long idVehicule = vehiculeService
          .createVehicule(TypeV.voiture, "Peugeot", "308", "blanche", "Paris", 50.0, agent).getId();
      Vehicule vehicule = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule);


      // Créer une location terminée pour l'historique
      Location location = locationService.creerLocation(LocalDateTime.now(),
          LocalDateTime.now().plusDays(5), vehicule, loueur);
      location.setStatut(StatutLocation.TERMINE);

      // Enregistrer la location avec le statut TERMINE
      LocationRepository locationRepository = new LocationRepository();
      locationRepository.save(location);
      System.out.println("✓ Location de test créée et terminée avec ID: " + location.getId());

      // Test US.L.6
      System.out.println("\n=== US.L.6: Consultation et modification du profil loueur ===");
      LoueurDTO loueurProfile = loueurService.getLoueurProfile(loueur);
      System.out.println("Profil actuel du loueur: ");
      System.out.println(" - Nom: " + loueurProfile.getNom());
      System.out.println(" - Prénom: " + loueurProfile.getPrenom());
      System.out.println(" - Email: " + loueurProfile.getEmail());

      System.out.println("\n - Locations précédentes: ");
      if (loueurService.getOldLocationsForLoueur(loueur).isEmpty()) {
        System.out.println("   Aucune location terminée trouvée");
      } else {
        for (LocationDTO loc : loueurService.getOldLocationsForLoueur(loueur)) {
          System.out.println("   - Date début: " + loc.getDateDebut());
          System.out.println("     Date fin: " + loc.getDateFin());
          System.out.println("     Véhicule: " + loc.getVehicule().getMarque() + " "
              + loc.getVehicule().getModele());
          System.out.println("     Statut: " + loc.getStatut());
        }
      }

      // Modifier le profil
      System.out.println("\n=== Modification du profil ===");
      loueurService.updateLoueurNom(loueur, "Dupont");
      loueurService.updateLoueurPrenom(loueur, "Jean");
      System.out.println("✓ Profil modifié");
      System.out.println(" - Nouveau nom: " + loueur.getNom());
      System.out.println(" - Nouveau prénom: " + loueur.getPrenom());

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
