package fr.univ.m1.projetagile._demo;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.dto.AgentDTO;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.LoueurDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
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
import fr.univ.m1.projetagile.notes.service.NoteService;

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
      //
      //
      //
      //
      NoteService noteService = new NoteService();


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
          "Paris", "Peugeot", "308", null, null, null, TypeV.voiture);
      System.out.println("Vehicules avec filtres disponibles: ");
      for (VehiculeDTO vehicule : vehiculesFiltres) {
        System.out.println(" - " + vehicule.getMarque() + " " + vehicule.getModele() + " "
            + vehicule.getCouleur() + " " + vehicule.getVille() + " " + vehicule.getPrixJ() + " "
            + vehicule.getNoteMoyenne() + " " + vehicule.getDatesDispo());
      }
      // US.V.3 Je veux pouvoir consulter le profil des agents de la plateforme, et les véhicules
      // qu’ils louent. (1)

      AgentDTO agent = agentService.getAgentProfile(agentService.findByEmail("asmith@example.com"));
      System.out.println("Profil de l'agent: ");
      System.out.println(" - " + agent.getNom() + " " + agent.getPrenom() + " " + agent.getEmail()
          + " " + agent.getTelephone() + " " + agent.getSiret() + " " + agent.getVehicules());
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
      // A FAIRE

      // US.L.4 Je veux pouvoir contacter un agent, ayant conclu un contrat avec lui ou non, par un
      // service de messagerie interne à la plateforme. (2)
      messagerieService.envoyerMessage(loueurService.findById(4L), agentService.findById(1L),
          "Bonjour, je voudrais louer votre véhicule");
      List<Message> messages = messagerieService.getMessagesUtilisateur(agentService.findById(4L));
      for (Message message : messages) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }
      // US.L.5 Je veux pouvoir contacter un autre loueur par l’intermédiaire de la plateforme. (1)
      messagerieService.envoyerMessage(loueurService.findById(4L), loueurService.findById(1L),
          "Bonjour, je voudrais savoir si vous avez déjà loué un véhicule chez Olivier Bertrand ?");
      List<Message> messages2 =
          messagerieService.getMessagesUtilisateur(loueurService.findById(4L));
      for (Message message : messages2) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }

      // US.L.6 Je veux pouvoir consulter mon profil, y changer des informations, voir mes
      // précédentes locations. (1)
      LoueurDTO loueurProfile = loueurService.getLoueurProfile(loueurService.findById(4L));
      System.out.println("Profil de l'utilisateur: ");
      System.out.println(" - " + loueurProfile.getNom() + " " + loueurProfile.getPrenom() + " "
          + loueurProfile.getNom() + " " + loueurProfile.getEmail());

      System.out.println(" - Locations précédentes: ");
      for (LocationDTO location : loueurService
          .getOldLocationsForLoueur(loueurService.findById(4L))) {
        System.out.println("   - " + location.getDateDebut() + " " + location.getDateFin() + " "
            + location.getLieuDepot() + " " + location.getVehicule().getMarque() + " "
            + location.getVehicule().getModele() + " " + location.getVehicule().getCouleur() + " "
            + location.getVehicule().getVille() + " " + location.getVehicule().getPrixJ());
      }

      loueurService.updateLoueurNom(loueurService.findById(4L), "Dupont");
      loueurService.updateLoueurPrenom(loueurService.findById(4L), "Jean");


      // US.A.1 Je veux pouvoir ajouter, modifier ou supprimer les véhicules mis à disposition. (3)
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Audi", "Q3", "noire",
          "Toulouse", 100.0, agentService.findById(4L));
      System.out.println("Véhicule créé: " + vehicule.getModele());

      vehiculeService.updateVehiculeMarque(agentService.findById(4L), vehicule.getId(), "Mercedes");
      vehiculeService.updateVehiculeModele(agentService.findById(4L), vehicule.getId(), "Classe C");
      System.out.println(
          "Véhicule modifié: " + vehiculeService.findVehiculeById(vehicule.getId()).getModele());

      vehiculeService.deleteVehicule(vehicule.getId());
      System.out.println("Véhicule supprimé");



      // US.A.2 Je veux pouvoir consulter l’historique de chaque véhicule mis à disposition. (1)
      // A FAIRE

      // US.A.3 Je veux pouvoir contracter ou annuler des options payantes. (3)
      // A FAIRE

      // US.A.4 Si option, je veux pouvoir accepter manuellement les contrats de location pré signé
      // par un loueur. (2)
      // A FAIRE

      // US.A.5 Je veux pouvoir envoyer un message à un loueur ou à un agent par la messagerie
      // interne (1)
      messagerieService.envoyerMessage(agentService.findById(4L), agentService.findById(1L),
          "Bonjour, je voudrais savoir si Bertrand est un loueur fiable");
      messagerieService.envoyerMessage(agentService.findById(4L), loueurService.findById(1L),
          "Pas de problème pour vous louer du 17 decembre au 20 decembre");
      List<Message> messages3 = messagerieService.getMessagesUtilisateur(agentService.findById(4L));
      for (Message message : messages3) {
        System.out.println("Message: " + message.getContenu() + " " + message.getDateEnvoi());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Fermer la connexion
      DatabaseConnection.close();
    }
  }
}
