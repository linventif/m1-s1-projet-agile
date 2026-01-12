package fr.univ.m1.projetagile._demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LocationRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.DisponibiliteService;
import fr.univ.m1.projetagile.core.service.LocationService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.service.CritereService;
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
      LocationRepository locationRepository = new LocationRepository();
      LocationService locationService = new LocationService(locationRepository);
      DisponibiliteService disponibiliteService = new DisponibiliteService();
      NoteService noteService = new NoteService();
      CritereService critereService = new CritereService();

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
      // Disponibilités
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Création des disponibilités ===");

      // Disponibilités pour tous les véhicules (sur les 6 prochains mois)
      LocalDate aujourdhui = LocalDate.now();
      LocalDate dans6mois = aujourdhui.plusMonths(6);

      Disponibilite dispo1 = disponibiliteService.creerDisponibilite(V1, aujourdhui, dans6mois);
      System.out.println("✓ Disponibilité créée: " + dispo1);

      Disponibilite dispo2 = disponibiliteService.creerDisponibilite(V2, aujourdhui, dans6mois);
      System.out.println("✓ Disponibilité créée: " + dispo2);

      Disponibilite dispo3 = disponibiliteService.creerDisponibilite(V3, aujourdhui, dans6mois);
      System.out.println("✓ Disponibilité créée: " + dispo3);

      Disponibilite dispo4 =
          disponibiliteService.creerDisponibilite(V4, aujourdhui.minusDays(30), dans6mois);
      System.out.println("✓ Disponibilité créée: " + dispo4);

      // Récupération des disponibilités d'un véhicule
      List<Disponibilite> disponibilitesV1 = disponibiliteService.getDisponibilitesVehicule(V1);
      System.out.println(
          "\n✓ Nombre de disponibilités pour " + V1.getMarque() + " : " + disponibilitesV1.size());

      // Vérification de disponibilité (exemple)
      LocalDate testDebut = LocalDate.now().plusDays(15);
      LocalDate testFin = testDebut.plusDays(7);
      boolean v1Dispo = disponibiliteService.estDisponible(V1, testDebut, testFin);
      System.out.println("✓ Véhicule V1 disponible du " + testDebut + " au " + testFin + " : "
          + (v1Dispo ? "OUI" : "NON"));


      // -- // -- // -- // -- // -- // -- // -- //
      // Locations
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Création de locations ===");

      // Location 1 : John loue la Peugeot 308 de Bob pour 5 jours
      LocalDateTime debut1 = LocalDateTime.now().plusDays(1);
      LocalDateTime fin1 = debut1.plusDays(5);
      Location loc1 = locationService.creerLocation(debut1, fin1, V1, L_john);
      System.out.println("✓ Location créée: " + L_john.getNomComplet() + " loue " + V1.getMarque()
          + " " + V1.getModele() + " du " + debut1.toLocalDate() + " au " + fin1.toLocalDate());
      System.out.println("  Prix total: " + locationService.getPrixLocation(loc1) + "€");

      // Location 2 : Jane loue la Yamaha MT-07 d'Alice pour 3 jours
      LocalDateTime debut2 = LocalDateTime.now().plusDays(2);
      LocalDateTime fin2 = debut2.plusDays(3);
      Location loc2 = locationService.creerLocation(debut2, fin2, V2, L_jane);
      System.out.println("✓ Location créée: " + L_jane.getNomComplet() + " loue " + V2.getMarque()
          + " " + V2.getModele() + " du " + debut2.toLocalDate() + " au " + fin2.toLocalDate());
      System.out.println("  Prix total: " + locationService.getPrixLocation(loc2) + "€");

      // Location 3 : John loue le Renault Master de LocaSmart pour 7 jours
      LocalDateTime debut3 = LocalDateTime.now().plusDays(10);
      LocalDateTime fin3 = debut3.plusDays(7);
      Location loc3 = locationService.creerLocation(debut3, fin3, V3, L_john);
      System.out.println("✓ Location créée: " + L_john.getNomComplet() + " loue " + V3.getMarque()
          + " " + V3.getModele() + " du " + debut3.toLocalDate() + " au " + fin3.toLocalDate());
      System.out.println("  Prix total: " + locationService.getPrixLocation(loc3) + "€");

      // Location 4 : Jane loue la Mercedes Classe A de HabitatPlus pour 2 jours (passée)
      LocalDateTime debut4 = LocalDateTime.now().minusDays(10);
      LocalDateTime fin4 = debut4.plusDays(2);
      Location loc4 = locationService.creerLocation(debut4, fin4, V4, L_jane);
      System.out.println("✓ Location créée: " + L_jane.getNomComplet() + " loue " + V4.getMarque()
          + " " + V4.getModele() + " du " + debut4.toLocalDate() + " au " + fin4.toLocalDate());
      System.out.println("  Prix total: " + locationService.getPrixLocation(loc4) + "€");

      // Accepter les locations
      APar_bob.accepterLocation(loc1);
      APar_alice.accepterLocation(loc2);
      APro_habitatplus.accepterLocation(loc4); // Accepter avant de terminer
      System.out.println("\n✓ Locations acceptées par les agents");

      // Terminer la location 4 (historique)
      locationService.terminer(loc4, 1000, "photo.jpg");
      System.out.println("✓ Location terminée (historique): " + L_jane.getNomComplet() + " a loué "
          + V4.getMarque() + " " + V4.getModele());


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
      // Critères
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Initialisation des Critères ===");

      // Afficher les critères existants
      Long nbCriteres = critereService.countCriteres();
      System.out.println("Nombre de critères en base: " + nbCriteres);

      if (nbCriteres > 0) {
        System.out.println("Critères existants:");
        for (Critere c : critereService.getAllCriteres()) {
          System.out.println("  - " + c.getNom() + " (ID: " + c.getId() + ")");
        }
      }

      // -- // -- // -- // -- // -- // -- // -- //
      // Notes
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Tests de notation avec critères ===");

      // Loueur 1 note Agent Bob avec critères (réutilisation automatique)
      List<Critere> criteresAgent1 = critereService.getOrCreateCriteres(
          new String[] {"Ponctualité", "Professionnalisme", "Communication"},
          new Double[] {8.5, 9.0, 8.0});
      NoteAgent noteAgent1 = noteService.noterAgent(L_john, APar_bob, criteresAgent1);
      System.out.println("✓ Note créée: " + noteAgent1);
      System.out.println("  Critères:");
      for (Critere c : noteAgent1.getCriteres()) {
        System.out.println("    - " + c);
      }
      System.out.println("  Moyenne: " + noteAgent1.getNoteMoyenne() + "/10");

      // Loueur 2 note Agent Alice avec d'autres critères
      List<Critere> criteresAgent2 = critereService.getOrCreateCriteres(
          new String[] {"Disponibilité", "Qualité du service", "Résolution problèmes"},
          new Double[] {9.0, 9.5, 8.5});
      NoteAgent noteAgent2 = noteService.noterAgent(L_jane, APar_alice, criteresAgent2);
      System.out.println("✓ Note créée: " + noteAgent2);
      System.out.println("  Critères:");
      for (Critere c : noteAgent2.getCriteres()) {
        System.out.println("    - " + c);
      }
      System.out.println("  Moyenne: " + noteAgent2.getNoteMoyenne() + "/10");

      // Agent Bob note Loueur 1 avec critères (réutilise "Ponctualité" et "Communication")
      List<Critere> criteresLoueur1 = critereService.getOrCreateCriteres(
          new String[] {"Respect du véhicule", "Ponctualité", "Communication"},
          new Double[] {9.0, 8.5, 9.0});
      NoteLoueur noteLoueur1 = noteService.noterLoueur(APar_bob, L_john, criteresLoueur1);
      System.out.println("✓ Note créée: " + noteLoueur1);
      System.out.println("  Critères:");
      for (Critere c : noteLoueur1.getCriteres()) {
        System.out.println("    - " + c);
      }
      System.out.println("  Moyenne: " + noteLoueur1.getNoteMoyenne() + "/10");

      // Agent Alice note Loueur 2 - Méthode classique (backward compatible)
      NoteLoueur noteLoueur2 = noteService.noterLoueur(APar_alice, L_jane, 8.0, 8.5, 7.5);
      System.out.println("✓ Note créée (méthode classique): " + noteLoueur2);
      System.out.println("  Moyenne: " + noteLoueur2.getNoteMoyenne() + "/10");

      // Loueur 1 note Véhicule 3 (Camion) avec critères
      List<Critere> criteresVehicule1 = critereService.getOrCreateCriteres(
          new String[] {"Propreté", "État mécanique", "Confort", "Consommation"},
          new Double[] {8.0, 9.0, 8.5, 7.5});
      NoteVehicule noteVehicule1 = noteService.noterVehicule(L_john, V3, criteresVehicule1);
      System.out.println("✓ Note créée: " + noteVehicule1);
      System.out.println("  Critères:");
      for (Critere c : noteVehicule1.getCriteres()) {
        System.out.println("    - " + c);
      }
      System.out.println("  Moyenne: " + noteVehicule1.getNoteMoyenne() + "/10");

      // Loueur 2 note Véhicule 1 (Peugeot) - Méthode classique
      NoteVehicule noteVehicule2 = noteService.noterVehicule(L_jane, V1, 9.5, 9.0, 9.0);
      System.out.println("✓ Note créée (méthode classique): " + noteVehicule2);
      System.out.println("  Moyenne: " + noteVehicule2.getNoteMoyenne() + "/10");

      // Affichage des critères créés
      System.out.println("\n=== Statistiques des critères ===");
      System.out.println("Nombre total de critères uniques: " + critereService.countCriteres());
      System.out.println("Liste des critères:");
      for (Critere c : critereService.getAllCriteres()) {
        System.out.println("  - " + c.getNom() + " (utilisé dans les notes)");
      }

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
