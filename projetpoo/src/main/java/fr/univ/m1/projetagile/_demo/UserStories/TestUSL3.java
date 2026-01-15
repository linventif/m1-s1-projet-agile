package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.service.NoteService;

/**
 * US.L.3 Je veux pouvoir noter un véhicule que j'ai loué, selon certains critères, et noter l'agent
 * concerné. (2)
 */
public class TestUSL3 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      LocationService locationService = new LocationService(new LocationRepository());
      NoteService noteService = new NoteService();

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


      // Créer une location terminée pour les tests
      Location location = locationService.creerLocation(LocalDateTime.now(),
          LocalDateTime.now().plusDays(5), vehicule, loueur);
      location.setStatut(StatutLocation.TERMINE);
      System.out.println("✓ Location de test créée avec ID: " + location.getId());

      // Test US.L.3
      System.out.println("\n=== US.L.3: Notation d'un véhicule et d'un agent ===");
      List<Critere> criteresVehicule = Arrays.asList(new Critere("Propreté", 2.0),
          new Critere("Rapidité", 5.0), new Critere("Communication", 4.0));

      List<Critere> criteresAgent = Arrays.asList(new Critere("Professionalisme", 3.5));

      NoteVehicule noteVehicule =
          noteService.noterVehicule(loueur, location.getVehicule(), criteresVehicule);
      NoteAgent noteAgent = noteService.noterAgent(loueur, agent, criteresAgent);

      System.out
          .println("✓ Note du véhicule enregistrée: " + noteVehicule.getNoteMoyenne() + "/10");
      System.out.println("✓ Note de l'agent enregistrée: " + noteAgent.getNoteMoyenne() + "/10");

      // Afficher les moyennes globales via NoteService
      System.out.println("\n=== Moyennes globales via NoteService ===");
      Double moyenneVehicule = noteService.getMoyenneVehicule(vehicule);
      Double moyenneAgent = noteService.getMoyenneAgent(agent);

      System.out.println("Note moyenne du véhicule: "
          + (moyenneVehicule != null ? String.format("%.2f", moyenneVehicule) + "/10"
              : "Aucune note"));
      System.out.println("Note moyenne de l'agent: "
          + (moyenneAgent != null ? String.format("%.2f", moyenneAgent) + "/10" : "Aucune note"));

      // // Afficher toutes les notes pour le véhicule et l'agent
      // System.out.println("\n=== Historique des notes ===");
      // List<NoteVehicule> notesVehicule = noteService.getNotesVehicule(vehicule);
      // System.out.println("Nombre de notes pour le véhicule: " + notesVehicule.size());

      // List<NoteAgent> notesAgent = noteService.getNotesAgent(agent);
      // System.out.println("Nombre de notes pour l'agent: " + notesAgent.size());

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
