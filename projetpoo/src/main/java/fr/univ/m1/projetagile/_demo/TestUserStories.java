package fr.univ.m1.projetagile._demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import fr.univ.m1.projetagile.VerificationLocation.persistence.VerificationRepository;
import fr.univ.m1.projetagile.VerificationLocation.service.VerificationService;
import fr.univ.m1.projetagile.controleTechnique.service.ControlTechniqueService;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.LoueurDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Disponibilite;
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
import fr.univ.m1.projetagile.entretienTechnique.entity.TypeTechnique;
import fr.univ.m1.projetagile.entretienTechnique.persistence.EntretienTechniqueRepository;
import fr.univ.m1.projetagile.entretienTechnique.persistence.TypeTechniqueRepository;
import fr.univ.m1.projetagile.entretienTechnique.service.EntretienTechniqueService;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.service.NoteService;
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.entity.SouscriptionOption;
import fr.univ.m1.projetagile.options.persistence.SouscriptionOptionRepository;
import fr.univ.m1.projetagile.options.service.SouscriptionOptionService;
import fr.univ.m1.projetagile.parrainage.entity.Crédit;
import fr.univ.m1.projetagile.parrainage.entity.Parrainage;
import fr.univ.m1.projetagile.parrainage.persistence.CreditRepository;
import fr.univ.m1.projetagile.parrainage.persistence.ParrainageRepository;
import fr.univ.m1.projetagile.parrainage.service.CreditService;
import fr.univ.m1.projetagile.parrainage.service.ParrainageService;

public class TestUserStories {
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
      LocationService locationService = new LocationService(new LocationRepository());
      SouscriptionOptionService souscriptionOptionService = new SouscriptionOptionService();
      ParrainageService parrainageService = new ParrainageService(new ParrainageRepository());
      CreditService creditService = new CreditService(new CreditRepository());
      VerificationService verificationService =
          new VerificationService(new VerificationRepository(), new LocationRepository());
      ControlTechniqueService controlTechniqueService =
          new ControlTechniqueService(new VehiculeRepository());
      EntretienTechniqueService entretienService =
          new EntretienTechniqueService(new TypeTechniqueRepository(),
              new EntretienTechniqueRepository(), new VehiculeRepository());
      //
      //
      //
      //
      NoteService noteService = new NoteService();

      // -- // -- // -- // -- // -- // -- // -- //
      // CREATION DES DONNEES DE TEST
      // -- // -- // -- // -- // -- // -- // -- //
      System.out.println("\n=== Création des données de test ===");

      // Création des Loueurs
      Long idLoueur1 = loueurService
          .createLoueur("Martin", "Sophie", "sophie.martin@example.com", "motdepasse123").getIdU();
      System.out.println("✓ Loueur créé avec ID: " + idLoueur1);

      Long idLoueur2 = loueurService
          .createLoueur("Bernard", "Luc", "luc.bernard@example.com", "motdepasse123").getIdU();
      System.out.println("✓ Loueur créé avec ID: " + idLoueur2);

      Long idLoueur4 = loueurService
          .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123").getIdU();
      System.out.println("✓ Loueur créé avec ID: " + idLoueur4);

      // Création des Agents
      Long idAgent1 = agentService
          .createAgentParticulier("Smith", "Alice", "asmith@example.com", "motdepasse123").getIdU();
      System.out.println("✓ Agent créé avec ID: " + idAgent1);

      Long idAgent4 = agentService
          .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com", "motdepasse123")
          .getIdU();
      System.out.println("✓ Agent créé avec ID: " + idAgent4);

      Long idAgent31 = agentService
          .createAgentParticulier("Leroy", "Thomas", "tleroy@example.com", "motdepasse123")
          .getIdU();
      System.out.println("✓ Agent créé avec ID: " + idAgent31);

      // Création des Véhicules
      Long idVehicule1 = vehiculeService.createVehicule(TypeV.voiture, "Peugeot", "308", "blanche",
          "Paris", 50.0, agentService.findById(idAgent1)).getId();
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule1);

      Long idVehicule35 = vehiculeService.createVehicule(TypeV.voiture, "Renault", "Clio", "rouge",
          "Lyon", 45.0, agentService.findById(idAgent31)).getId();
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule35);

      Long idVehicule84 = vehiculeService.createVehicule(TypeV.voiture, "Citroën", "C3", "grise",
          "Marseille", 40.0, agentService.findById(idAgent4)).getId();
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule84);

      Long idVehicule136 = vehiculeService.createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire",
          "Nice", 80.0, agentService.findById(idAgent4)).getId();
      System.out.println("✓ Véhicule créé avec ID: " + idVehicule136);

      // Création des disponibilités pour les véhicules (seulement dans le futur)
      vehiculeService.createDisponibilite(agentService.findById(idAgent1), idVehicule1,
          LocalDate.now(), LocalDate.now().plusDays(60));
      vehiculeService.createDisponibilite(agentService.findById(idAgent31), idVehicule35,
          LocalDate.now(), LocalDate.now().plusDays(60));
      vehiculeService.createDisponibilite(agentService.findById(idAgent4), idVehicule84,
          LocalDate.now(), LocalDate.now().plusDays(60));
      vehiculeService.createDisponibilite(agentService.findById(idAgent4), idVehicule136,
          LocalDate.now(), LocalDate.now().plusDays(60));
      System.out.println("✓ Disponibilités créées pour les véhicules");

      // Création d'une location de test pour les tests de notation
      // La location est créée maintenant et terminée immédiatement pour permettre la notation
      Long idLocation66 = locationService
          .creerLocation(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
              vehiculeService.findVehiculeById(idVehicule1), loueurService.findById(idLoueur4))
          .getId();
      Location location66 = locationService.findLocationById(idLocation66);
      location66.setStatut(StatutLocation.TERMINE);
      System.out.println("✓ Location de test créée avec ID: " + idLocation66);

      System.out.println("=== Données de test créées avec succès ===\n");


      // US.V.1 Je veux pouvoir consulter les véhicules disponibles. J’ai alors accès aux
      // informations suivantes : note du véhicule, date de disponibilités, lieu de disponibilité.
      // (3)
      List<VehiculeDTO> vehicules = vehiculeService.getVehicules();
      System.out.println("Vehicules disponibles: ");

      for (VehiculeDTO vehicule : vehicules) {
        System.out.println(" - " + vehicule.getMarque() + " " + vehicule.getModele() + " "
            + vehicule.getCouleur() + " " + vehicule.getVille() + " " + vehicule.getPrixJ() + " "
            + vehicule.getNoteMoyenne() + " " + vehicule.getDatesDispo());
      }

      // US.V.2. Je veux pouvoir appliquer des filtres sur les véhicules que je vois. (2)
      List<VehiculeDTO> vehiculesFiltres = vehiculeService.searchVehiculesWithFilters(null, null,
          "Paris", "Peugeot", "308", null, null, null, TypeV.voiture, null);
      System.out.println("Vehicules avec filtres disponibles: ");
      for (VehiculeDTO vehicule : vehiculesFiltres) {
        System.out.println(" - " + vehicule.getMarque() + " " + vehicule.getModele() + " "
            + vehicule.getCouleur() + " " + vehicule.getVille() + " " + vehicule.getPrixJ() + " "
            + vehicule.getNoteMoyenne() + " " + vehicule.getDatesDispo());
      }
      // US.V.3 Je veux pouvoir consulter le profil des agents de la plateforme, et les véhicules
      // qu’ils louent. (1)

      AgentDTO agent = agentService.getAgentProfile(agentService.findById(idAgent1));
      System.out.println("Profil de l'agent: ");
      System.out.println(" - " + agent.getNom() + " " + agent.getPrenom() + " " + agent.getEmail()
          + " " + agent.getSiret() + " " + agent.getVehicules());
      System.out.println(" - Véhicules disponibles: ");
      for (VehiculeDTO vehicule : agent.getVehicules()) {
        System.out.println("   - " + vehicule.getMarque() + " " + vehicule.getModele() + " "
            + vehicule.getCouleur() + " " + vehicule.getVille() + " " + vehicule.getPrixJ() + " "
            + vehicule.getNoteMoyenne() + " " + vehicule.getDatesDispo());
      }

      // US.L.2 Je veux pouvoir louer un véhicule. Cela inclut : choisir une assurance, choisir le
      // lieu de dépose du véhicule si option disponible, choisir les dates de location. (3)

      // Vehicule vehicule = vehiculeService.findVehiculeById(1L);
      // vehiculeService.createDisponibilite(agentService.findById(4L), vehicule.getId(),
      // LocalDate.now(), LocalDate.now().plusDays(10));
      // Location location =
      // locationService.creerLocation(LocalDateTime.now(), LocalDateTime.now().plusDays(10),
      // "Paris", vehiculeService.findVehiculeById(1L), loueurService.findById(1L));

      // locationService.getLocation(location.getId());
      // System.out.println("Location: " + location.getDateDebut() + " " + location.getDateFin() + "
      // "
      // + location.getLieuDepot() + " " + location.getVehicule().getMarque() + " "
      // + location.getVehicule().getModele() + " " + location.getVehicule().getCouleur() + " "
      // + location.getVehicule().getVille() + " " + location.getVehicule().getPrixJ());

      // US.L.3 Je veux pouvoir noter un véhicule que j’ai loué, selon certains critères, et noter
      // l’agent concerné. (2)
      List<Critere> criteresV = Arrays.asList(new Critere("Propreté", 5.0),
          new Critere("Rapidité", 5.0), new Critere("Communication", 5.0));

      List<Critere> criteresA = Arrays.asList(new Critere("Professionalisme", 5.0));

      Location locationNoteTest = locationService.findLocationById(idLocation66);
      NoteVehicule noteVehicule = noteService.noterVehicule(loueurService.findById(idLoueur4),
          locationNoteTest.getVehicule(), criteresV);
      NoteAgent noteAgent = noteService.noterAgent(loueurService.findById(idLoueur4),
          agentService.findById(idAgent4), criteresA);
      System.out.println("Note du véhicule: " + noteVehicule.getNoteMoyenne());
      System.out.println("Note de l'agent: " + noteAgent.getNoteMoyenne());

      // US.L.4 Je veux pouvoir contacter un agent, ayant conclu un contrat avec lui ou non, par un
      // service de messagerie interne à la plateforme. (2)
      messagerieService.envoyerMessage(loueurService.findById(idLoueur4),
          agentService.findById(idAgent1), "Bonjour, je voudrais louer votre véhicule");
      List<Message> messages =
          messagerieService.getMessagesUtilisateur(agentService.findById(idAgent4));
      for (Message message : messages) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }
      // US.L.5 Je veux pouvoir contacter un autre loueur par l’intermédiaire de la plateforme. (1)
      messagerieService.envoyerMessage(loueurService.findById(idLoueur4),
          loueurService.findById(idLoueur1),
          "Bonjour, je voudrais savoir si vous avez déjà loué un véhicule chez Olivier Bertrand ?");
      List<Message> messages2 =
          messagerieService.getMessagesUtilisateur(loueurService.findById(idLoueur4));
      for (Message message : messages2) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }

      // US.L.6 Je veux pouvoir consulter mon profil, y changer des informations, voir mes
      // précédentes locations. (1)
      LoueurDTO loueurProfile = loueurService.getLoueurProfile(loueurService.findById(idLoueur4));
      System.out.println("Profil de l'utilisateur: ");
      System.out.println(" - " + loueurProfile.getNom() + " " + loueurProfile.getPrenom() + " "
          + loueurProfile.getNom() + " " + loueurProfile.getEmail());

      System.out.println(" - Locations précédentes: ");
      for (LocationDTO location : loueurService
          .getOldLocationsForLoueur(loueurService.findById(idLoueur4))) {
        System.out.println("   - " + location.getDateDebut() + " " + location.getDateFin() + " "
            + location.getLieuDepot() + " " + location.getVehicule().getMarque() + " "
            + location.getVehicule().getModele() + " " + location.getVehicule().getCouleur() + " "
            + location.getVehicule().getVille() + " " + location.getVehicule().getPrixJ());
      }

      loueurService.updateLoueurNom(loueurService.findById(idLoueur4), "Dupont");
      loueurService.updateLoueurPrenom(loueurService.findById(idLoueur4), "Jean");


      // US.A.1 Je veux pouvoir ajouter, modifier ou supprimer les véhicules mis à disposition. (3)
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Audi", "Q3", "noire",
          "Toulouse", 100.0, agentService.findById(idAgent4));
      System.out.println("Véhicule créé: " + vehicule.getModele());

      vehiculeService.updateVehiculeMarque(agentService.findById(idAgent4), vehicule.getId(),
          "Mercedes");
      vehiculeService.updateVehiculeModele(agentService.findById(idAgent4), vehicule.getId(),
          "Classe C");
      System.out.println(
          "Véhicule modifié: " + vehiculeService.findVehiculeById(vehicule.getId()).getModele());

      vehiculeService.deleteVehicule(vehicule.getId());
      System.out.println("Véhicule supprimé");



      // US.A.2 Je veux pouvoir consulter l’historique de chaque véhicule mis à disposition. (1)
      Vehicule vehiculeHistory = vehiculeService.findVehiculeById(idVehicule1);
      List<LocationDTO> locations =
          locationService.getPreviousLocationsForVehicule(vehiculeHistory.getId());
      for (LocationDTO location : locations) {
        System.out.println("Location: " + location.getDateDebut() + " " + location.getDateFin()
            + " " + location.getLieuDepot() + " " + location.getVehicule().getMarque() + " "
            + location.getVehicule().getModele() + " " + location.getVehicule().getCouleur() + " "
            + location.getVehicule().getVille() + " " + location.getVehicule().getPrixJ());
      }

      // US.A.3 Je veux pouvoir contracter ou annuler des options payantes. (3)
      Agent agentContractTest = agentService.findById(idAgent1);
      // Création d'une option pour le test
      SouscriptionOptionRepository souscriptionOptionRepository =
          new SouscriptionOptionRepository();
      Options optionTest = new Options("Option Premium", 29.99);
      optionTest = souscriptionOptionRepository.saveOption(optionTest);
      System.out.println("✓ Option créée avec ID: " + optionTest.getId());

      SouscriptionOption souscriptionOption = souscriptionOptionService
          .souscrireOption(agentContractTest.getIdU(), optionTest.getId(), 1, true);
      souscriptionOptionService.annulerSouscription(souscriptionOption.getId());
      // AJOUTER FONCTIONS POUR GET OPTIONS D'UN UTILISATEUR

      // US.A.4 Si option, je veux pouvoir accepter manuellement les contrats de location pré signé
      // par un loueur. (2)
      // A FAIRE

      // US.A.5 Je veux pouvoir envoyer un message à un loueur ou à un agent par la messagerie
      // interne (1)
      messagerieService.envoyerMessage(agentService.findById(idAgent4),
          agentService.findById(idAgent1),
          "Bonjour, je voudrais savoir si Bertrand est un loueur fiable");
      messagerieService.envoyerMessage(agentService.findById(idAgent4),
          loueurService.findById(idLoueur1),
          "Pas de problème pour vous louer du 17 decembre au 20 decembre");
      List<Message> messages3 =
          messagerieService.getMessagesUtilisateur(agentService.findById(idAgent4));
      for (Message message : messages3) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }

      // -----------------------------------------------------------------------------
      // SECTION LOUEUR
      // -----------------------------------------------------------------------------

      // US.L.9
      // Gère le parrainage d'un nouveau loueur. Si le filleul effectue au moins une location,
      // le parrain reçoit un crédit utilisable pour ses futures locations.
      Loueur loueurParrainage = loueurService.findById(idLoueur1);
      Loueur loueurFilleul = loueurService.findById(idLoueur2);
      Parrainage parrainage = parrainageService.parrainer(loueurParrainage, loueurFilleul);
      System.out.println("Parrainage créé: " + parrainage.getId());
      Location location = locationService.creerLocation(LocalDateTime.now().plusDays(39),
          LocalDateTime.now().plusDays(45), vehiculeService.findVehiculeById(idVehicule1),
          loueurFilleul);

      Crédit credit = creditService.getCredit(loueurParrainage.getIdU());
      System.out.println("Crédit: " + credit.getCredit());

      // US.L.10
      // Enregistre le kilométrage au départ et au retour du véhicule.
      // Nécessite l'upload d'une photo du tableau de bord comme preuve justificative.
      location.setStatut(StatutLocation.ACCEPTE);
      verificationService.creerVerification(location.getId(), 1560);
      locationService.terminer(location, 1800, "photo.jpg");

      // -----------------------------------------------------------------------------
      // SECTION AGENT
      // -----------------------------------------------------------------------------

      // US.A.7
      // Applique une réduction sur la commission (part variable) prélevée par la plateforme
      // lorsque la location dépasse une certaine durée.
      Location locationLLD =
          locationService.creerLocation(LocalDateTime.now(), LocalDateTime.now().plusDays(10),
              vehiculeService.findVehiculeById(idVehicule136), loueurService.findById(idLoueur4));
      Location locationNormale =
          locationService.creerLocation(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
              vehiculeService.findVehiculeById(idVehicule84), loueurService.findById(idLoueur1));
      System.out.println("Prix location LLD: " + locationService.getPrixLocation(locationLLD));
      System.out
          .println("Prix location normale: " + locationService.getPrixLocation(locationNormale));

      // US.A.8
      // Permet à l'agent de saisir et mettre à jour la date et le statut
      // du dernier contrôle technique du véhicule.
      Vehicule vehiculeControlTest = vehiculeService.findVehiculeById(idVehicule136);
      controlTechniqueService.enregistrerNouveauControle(vehiculeControlTest.getId(),
          LocalDate.now(), 10000, "Passé", "Aucun commentaire");
      System.out.println("Date dernier contrôle: "
          + controlTechniqueService.genererRapportControle(vehiculeControlTest));

      // US.A.9
      // Vérifie les dates de validité et envoie une notification ou un email à l'agent
      // lorsque la date de repassage du contrôle technique approche.
      System.out.println("Date prochain contrôle: "
          + controlTechniqueService.calculerDateProchainControle(vehiculeControlTest));

      // US.A.10
      // Permet de journaliser les opérations de maintenance effectuées (ex: courroie, pneus)
      // pour garder un historique technique du véhicule.
      TypeTechnique typeTechniqueCourroie = entretienService.creerTypeTechnique("Courroie", 15000);
      TypeTechnique typeTechniquePneus = entretienService.creerTypeTechnique("Pneus", 30000);
      TypeTechnique typeTechniqueBougies = entretienService.creerTypeTechnique("Bougies", 60000);
      TypeTechnique typeTechniquePlaquesDeFrein =
          entretienService.creerTypeTechnique("Plaques de frein", 80000);
      TypeTechnique typeTechniqueEtatGeneral =
          entretienService.creerTypeTechnique("Etat général", 100000);
      entretienService.creerEntretienTechnique(vehiculeControlTest.getId(),
          typeTechniqueCourroie.getId(), LocalDate.now());
      entretienService.creerEntretienTechnique(vehiculeControlTest.getId(),
          typeTechniquePneus.getId(), LocalDate.now());
      entretienService.creerEntretienTechnique(vehiculeControlTest.getId(),
          typeTechniqueBougies.getId(), LocalDate.now());
      entretienService.creerEntretienTechnique(vehiculeControlTest.getId(),
          typeTechniquePlaquesDeFrein.getId(), LocalDate.now());
      entretienService.creerEntretienTechnique(vehiculeControlTest.getId(),
          typeTechniqueEtatGeneral.getId(), LocalDate.now());

      // US.A.11
      // Analyse le kilométrage parcouru et suggère des entretiens préventifs
      // (ex: changer la courroie tous les X kms) via des notifications.
      System.out.println("Recommandations entretien: "
          + controlTechniqueService.getRecommandationsEntretienParKilometrage(vehiculeControlTest));

      // US.A.12
      // Bascule le statut d'un véhicule en "Pause" pour empêcher temporairement
      // toute nouvelle réservation sans supprimer l'annonce.
      vehiculeService.updateVehiculeDisponibilite(vehiculeControlTest.getProprietaire(),
          vehiculeControlTest.getId(), false);

      // US.A.13
      // Gère le calendrier de disponibilité spécifique pour chaque véhicule,
      // définissant les plages horaires ou jours ouverts à la location.
      vehiculeService.createDisponibilite(vehiculeControlTest.getProprietaire(),
          vehiculeControlTest.getId(), LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 15));
      List<Disponibilite> disponibilites =
          vehiculeService.getDisponibilitesByVehicule(vehiculeControlTest.getId());
      for (Disponibilite dispo : disponibilites) {
        System.out.println("Disponibilité: " + dispo.getDateDebut() + " " + dispo.getDateFin());
      }

      // US.A.14
      // Permet à l'agent de répondre aux commentaires laissés sur ses véhicules ou son profil,
      // garantissant un droit de réponse en cas de désaccord ou litige.
      // EN ATTENTE


      // US.A.15
      // Gère le parrainage d'un nouvel agent. Le parrain reçoit des crédits pour options payantes
      // si le filleul met un véhicule en ligne et que celui-ci est loué au moins une fois.
      Agent agentParrainage = agentService.findById(idAgent4);
      Agent agentFilleul = agentService.findById(idAgent31);
      Parrainage parrainageAgent = parrainageService.parrainer(agentParrainage, agentFilleul);
      System.out.println("Parrainage agent créé: " + parrainageAgent.getId());

      Vehicule vehiculeFilleul = vehiculeService.findVehiculeById(idVehicule35);
      vehiculeService.createDisponibilite(agentFilleul, vehiculeFilleul.getId(), LocalDate.now(),
          LocalDate.now().plusDays(11));
      locationService.creerLocation(LocalDateTime.now(), LocalDateTime.now().plusDays(10),
          vehiculeFilleul, loueurFilleul);
      System.out.println("Location créée: " + location.getId());

      Crédit creditAgent = creditService.getCredit(agentParrainage.getIdU());
      System.out.println("Crédit: " + creditAgent.getCredit());

      // US.A.16 Agent peut ajouter un parking

      // US.L.11 Un loueur peut choisir un parking en tant que lieu de depôt


    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Fermer la connexion
      DatabaseConnection.close();
    }
  }
}
