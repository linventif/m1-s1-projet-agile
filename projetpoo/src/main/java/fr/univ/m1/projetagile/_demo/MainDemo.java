package fr.univ.m1.projetagile._demo;

import java.time.LocalDateTime;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
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
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.service.NoteService;

public class MainDemo {
  public static void main(String[] args) {
    try {
      // -- // -- // -- // -- // -- // -- // -- //
      // Database Connection
      // -- // -- // -- // -- // -- // -- // -- //
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      // -- // -- // -- // -- // -- // -- // -- //
      // Services
      // -- // -- // -- // -- // -- // -- // -- //
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      NoteService noteService = new NoteService();

      // ✅ Location (pour #99 + #100)
      LocationRepository locationRepository = new LocationRepository();
      LocationService locationService = new LocationService(locationRepository);

      // -- // -- // -- // -- // -- // -- // -- //
      // Utilisateurs
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Initialisation des Utilisateurs ===");
      // Particulier
      AgentParticulier APar_bob =
          (AgentParticulier) agentService.findByEmail("bob.maurise@gmail.com");
      if (APar_bob == null) {
        APar_bob = agentService.createAgentParticulier("maurise", "bob", "bob.maurise@gmail.com",
            "p@ssw0rd", "33601020304");
        System.out.println("✓ Agent créé: " + APar_bob);
      } else {
        System.out.println("✓ Agent existant récupéré: " + APar_bob);
      }

      AgentParticulier APar_alice =
          (AgentParticulier) agentService.findByEmail("alice.dupont@gmail.com");
      if (APar_alice == null) {
        APar_alice = agentService.createAgentParticulier("dupont", "alice",
            "alice.dupont@gmail.com", "p@ssw0rd", "33605060708");
        System.out.println("✓ Agent créé: " + APar_alice);
      } else {
        System.out.println("✓ Agent existant récupéré: " + APar_alice);
      }

      // Professionnel
      AgentProfessionnel APro_locasmart =
          (AgentProfessionnel) agentService.findByEmail("contact@localsmart.fr");
      if (APro_locasmart == null) {
        APro_locasmart = agentService.createAgentProfessionnel("contact@localsmart.fr", "p@ssw0rd",
            "12345678901234", "LocaSmart");
        System.out.println("✓ Agent créé: " + APro_locasmart);
      } else {
        System.out.println("✓ Agent existant récupéré: " + APro_locasmart);
      }

      AgentProfessionnel APro_habitatplus =
          (AgentProfessionnel) agentService.findByEmail("contact@habitatplus.fr");
      if (APro_habitatplus == null) {
        APro_habitatplus = agentService.createAgentProfessionnel("contact@habitatplus.fr",
            "p@ssw0rd", "98765432109876", "HabitatPlus");
        System.out.println("✓ Agent créé: " + APro_habitatplus);
      } else {
        System.out.println("✓ Agent existant récupéré: " + APro_habitatplus);
      }

      // Loueur
      Loueur L_john = loueurService.findByEmail("john.doe@gmail.com");
      if (L_john == null) {
        L_john = loueurService.createLoueur("doe", "john", "john.doe@gmail.com", "p@ssw0rd");
        System.out.println("✓ Loueur créé: " + L_john);
      } else {
        System.out.println("✓ Loueur existant récupéré: " + L_john);
      }

      Loueur L_jane = loueurService.findByEmail("jane.smith@gmail.com");
      if (L_jane == null) {
        L_jane = loueurService.createLoueur("smith", "jane", "jane.smith@gmail.com", "p@ssw0rd");
        System.out.println("✓ Loueur créé: " + L_jane);
      } else {
        System.out.println("✓ Loueur existant récupéré: " + L_jane);
      }

      // -- // -- // -- // -- // -- // -- // -- //
      // Vehicules
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Initialisation des Véhicules ===");

      // Véhicule 1 - Voiture de Bob
      Vehicule V1 = vehiculeService.createVehicule(TypeV.voiture, "Peugeot", "308", "Blanc",
          "Paris", 45.0, APar_bob);
      System.out.println("✓ Véhicule créé: " + V1);

      // Véhicule 2 - Moto d'Alice
      Vehicule V2 = vehiculeService.createVehicule(TypeV.moto, "Yamaha", "MT-07", "Noir", "Lyon",
          35.0, APar_alice);
      System.out.println("✓ Véhicule créé: " + V2);

      // Véhicule 3 - Camion de LocaSmart
      Vehicule V3 = vehiculeService.createVehicule(TypeV.camion, "Renault", "Master", "Blanc",
          "Marseille", 80.0, APro_locasmart);
      System.out.println("✓ Véhicule créé: " + V3);

      // Véhicule 4 - Voiture de HabitatPlus
      Vehicule V4 = vehiculeService.createVehicule(TypeV.voiture, "Mercedes", "Classe A", "Noir",
          "Toulouse", 65.0, APro_habitatplus);
      System.out.println("✓ Véhicule créé: " + V4);

      // -- // -- // -- // -- // -- // -- // -- //
      // TEST LLD (#99 + #100)
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== TEST LLD (#99 + #100) : Location courte vs longue ===");

      // Location courte (3 jours)
      LocalDateTime debutCourte = LocalDateTime.now();
      LocalDateTime finCourte = debutCourte.plusDays(3);

      Location locCourte = new Location(debutCourte, finCourte, V1, L_john);
      locCourte = locationRepository.save(locCourte);

      System.out.println("\n--- Location COURTE (3 jours) ---");
      System.out.println("ID   : " + locCourte.getId());
      System.out.println("Début: " + locCourte.getDateDebut());
      System.out.println("Fin  : " + locCourte.getDateFin());
      System.out.println("Jours: " + locCourte.getNombreJours());
      System.out.println("LLD ? " + locCourte.estLongueDuree());
      System.out.println("Prix courte: " + locationService.getPrixLocation(locCourte));

      // Location longue (7 jours)
      LocalDateTime debutLongue = LocalDateTime.now();
      LocalDateTime finLongue = debutLongue.plusDays(7);

      Location locLongue = new Location(debutLongue, finLongue, V1, L_john);
      locLongue = locationRepository.save(locLongue);

      System.out.println("\n--- Location LONGUE (7 jours) ---");
      System.out.println("ID   : " + locLongue.getId());
      System.out.println("Début: " + locLongue.getDateDebut());
      System.out.println("Fin  : " + locLongue.getDateFin());
      System.out.println("Jours: " + locLongue.getNombreJours());
      System.out.println("LLD ? " + locLongue.estLongueDuree());
      System.out.println("Prix longue: " + locationService.getPrixLocation(locLongue));

      // -- // -- // -- // -- // -- // -- // -- //
      // Messaging
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Tests de messagerie ===");
      // Loueur1 -> Agent Pro
      Message msg1 = messagerieService.envoyerMessage(L_john, APro_locasmart,
          "Bonjour, je suis intéressé par vos services");
      System.out.println("✓ Message envoyé: " + msg1);
      // Agent Pro -> Loueur1
      Message msg2 = messagerieService.envoyerMessage(APro_locasmart, L_john,
          "Bonjour, merci pour votre intérêt. Comment puis-je vous aider ?");
      System.out.println("✓ Message envoyé: " + msg2);

      // Loueur2 -> Agent Particulier
      Message msg3 = messagerieService.envoyerMessage(L_jane, APar_bob,
          "Bonjour, j'aimerais louer un véhicule pour le week-end.");
      System.out.println("✓ Message envoyé: " + msg3);

      // Agent Particulier -> Loueur2
      Message msg4 = messagerieService.envoyerMessage(APar_bob, L_jane,
          "Bonjour, j'ai plusieurs véhicules disponibles. Quel type recherchez-vous ?");
      System.out.println("✓ Message envoyé: " + msg4);

      // -- // -- // -- // -- // -- // -- // -- //
      // Notes
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Tests de notation ===");

      // Loueur 1 note Agent Bob
      NoteAgent noteAgent1 = noteService.noterAgent(L_john, APar_bob, 8.5, 9.0, 8.0);
      System.out.println("✓ Note créée: " + noteAgent1);
      System.out.println("  Moyenne: " + noteAgent1.getNoteMoyenne() + "/10");

      // Loueur 2 note Agent Alice
      NoteAgent noteAgent2 = noteService.noterAgent(L_jane, APar_alice, 9.0, 9.5, 8.5);
      System.out.println("✓ Note créée: " + noteAgent2);
      System.out.println("  Moyenne: " + noteAgent2.getNoteMoyenne() + "/10");

      // Agent Bob note Loueur 1
      NoteLoueur noteLoueur1 = noteService.noterLoueur(APar_bob, L_john, 9.0, 8.5, 9.0);
      System.out.println("✓ Note créée: " + noteLoueur1);
      System.out.println("  Moyenne: " + noteLoueur1.getNoteMoyenne() + "/10");

      // Agent Alice note Loueur 2
      NoteLoueur noteLoueur2 = noteService.noterLoueur(APar_alice, L_jane, 8.0, 8.5, 7.5);
      System.out.println("✓ Note créée: " + noteLoueur2);
      System.out.println("  Moyenne: " + noteLoueur2.getNoteMoyenne() + "/10");

      // Loueur 1 note Véhicule 3 (Camion)
      NoteVehicule noteVehicule1 = noteService.noterVehicule(L_john, V3, 8.0, 9.0, 8.5);
      System.out.println("✓ Note créée: " + noteVehicule1);
      System.out.println("  Moyenne: " + noteVehicule1.getNoteMoyenne() + "/10");

      // Loueur 2 note Véhicule 1 (Peugeot)
      NoteVehicule noteVehicule2 = noteService.noterVehicule(L_jane, V1, 9.5, 9.0, 9.0);
      System.out.println("✓ Note créée: " + noteVehicule2);
      System.out.println("  Moyenne: " + noteVehicule2.getNoteMoyenne() + "/10");

      // Affichage des moyennes générales
      System.out.println("\n=== Statistiques des notes ===");
      System.out.println("Moyenne Agent Bob: " + noteService.getMoyenneAgent(APar_bob) + "/10");
      System.out.println("Moyenne Agent Alice: " + noteService.getMoyenneAgent(APar_alice) + "/10");
      System.out.println("Moyenne Loueur John: " + noteService.getMoyenneLoueur(L_john) + "/10");
      System.out.println("Moyenne Loueur Jane: " + noteService.getMoyenneLoueur(L_jane) + "/10");
      System.out
          .println("Moyenne Véhicule Peugeot 308: " + noteService.getMoyenneVehicule(V1) + "/10");
      System.out.println(
          "Moyenne Véhicule Renault Master: " + noteService.getMoyenneVehicule(V3) + "/10");

      System.out.println("\n✓ Démonstration complète terminée avec succès!");
    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
