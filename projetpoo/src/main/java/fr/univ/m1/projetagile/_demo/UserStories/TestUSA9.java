package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import fr.univ.m1.projetagile.controleTechnique.service.ControlTechniqueService;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * US.A.9 Vérifie les dates de validité et envoie une notification ou un email à l'agent lorsque la
 * date de repassage du contrôle technique approche.
 */
public class TestUSA9 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      AgentService agentService = new AgentService(new AgentRepository());
      ControlTechniqueService controlTechniqueService =
          new ControlTechniqueService(new VehiculeRepository());

      // S'assurer que nous avons des données de test
      Agent agent = agentService.findById(1L);
      if (agent == null) {
        Long idAgent = agentService
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com", "motdepasse123")
            .getIdU();
        agent = agentService.findById(idAgent);
        System.out.println("✓ Agent créé avec ID: " + idAgent);
      }

      Vehicule vehicule = vehiculeService.findVehiculeById(1L);
      if (vehicule == null) {
        Long idVehicule = vehiculeService
            .createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire", "Nice", 80.0, agent).getId();
        vehicule = vehiculeService.findVehiculeById(idVehicule);
        vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
            LocalDate.now().plusDays(60));
        System.out.println("✓ Véhicule créé avec ID: " + idVehicule);
      }

      // Tester US.A.9
      System.out.println("\n=== US.A.9: Vérification et notifications de contrôle technique ===");

      // Définir la date de mise en circulation du véhicule (requis pour les calculs de contrôle)
      // Disons que le véhicule a été mis en circulation il y a 6 ans
      LocalDate dateMiseEnCirculation = LocalDate.now().minusYears(6);
      controlTechniqueService.updateControleTechnique(vehicule.getId(), dateMiseEnCirculation,
          null, null, null, null, null, null);
      System.out.println("Date de mise en circulation du véhicule: " + dateMiseEnCirculation);
      System.out.println("Âge du véhicule: 6 ans (contrôle tous les 2 ans)");
      System.out.println(
          "Note: Chaque scénario met à jour le même véhicule pour démontrer différentes situations.\n");

      // Scénario 1: Date de contrôle dans un futur lointain - statut OK
      // Le dernier contrôle date d'1 an, donc le prochain contrôle est dans 1 an
      System.out.println("--- Scénario 1: Contrôle technique OK (dans 365 jours) ---");
      LocalDate lastControl1 = LocalDate.now().minusYears(1);
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(), lastControl1, 10000,
          "Passé", "Véhicule en bon état");
      LocalDate nextControl1 = controlTechniqueService.calculerDateProchainControle(vehicule);
      long joursRestants1 =
          java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextControl1);
      System.out.println("Date du dernier contrôle: " + lastControl1);
      System.out.println("Date du prochain contrôle: " + nextControl1);
      System.out.println("Jours restants: " + joursRestants1);
      System.out.println("Statut: " + controlTechniqueService.getStatutControleDetaille(vehicule));

      // Scénario 2: Contrôle approchant dans ~25 jours - alerte PLANIFIÉ
      // Le dernier contrôle date d'environ 23 mois (2 ans - 25 jours)
      System.out.println("\n--- Scénario 2: Contrôle technique approchant (dans 25 jours) ---");
      LocalDate lastControl2 = LocalDate.now().minusYears(2).plusDays(25);
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(), lastControl2, 15000,
          "Passé", "Véhicule en bon état");
      LocalDate nextControl2 = controlTechniqueService.calculerDateProchainControle(vehicule);
      long joursRestants2 =
          java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextControl2);
      System.out.println("Date du dernier contrôle: " + lastControl2);
      System.out.println("Date du prochain contrôle: " + nextControl2);
      System.out.println("Jours restants: " + joursRestants2);
      System.out.println("Statut: " + controlTechniqueService.getStatutControleDetaille(vehicule));
      System.out.println("⚠️  NOTIFICATION: Pensez à programmer votre contrôle technique!");

      // Scénario 3: Contrôle dans 5 jours - alerte URGENTE
      // Le dernier contrôle date d'environ 2 ans (2 ans - 5 jours)
      System.out.println("\n--- Scénario 3: Contrôle technique imminent (dans 5 jours) ---");
      LocalDate lastControl3 = LocalDate.now().minusYears(2).plusDays(5);
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(), lastControl3, 20000,
          "Passé", "Véhicule en bon état");
      LocalDate nextControl3 = controlTechniqueService.calculerDateProchainControle(vehicule);
      long joursRestants3 =
          java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextControl3);
      System.out.println("Date du dernier contrôle: " + lastControl3);
      System.out.println("Date du prochain contrôle: " + nextControl3);
      System.out.println("Jours restants: " + joursRestants3);
      System.out.println("Statut: " + controlTechniqueService.getStatutControleDetaille(vehicule));
      System.out.println("🚨 NOTIFICATION URGENTE: Votre contrôle technique arrive bientôt!");

      // Scénario 4: Contrôle dépassé - URGENT en retard
      // Le dernier contrôle date de plus de 2 ans (2 ans + 15 jours)
      System.out.println("\n--- Scénario 4: Contrôle technique dépassé (en retard de 15 jours) ---");
      LocalDate lastControl4 = LocalDate.now().minusYears(2).minusDays(15);
      controlTechniqueService.enregistrerNouveauControle(vehicule.getId(), lastControl4, 25000,
          "Passé", "Véhicule en bon état");
      LocalDate nextControl4 = controlTechniqueService.calculerDateProchainControle(vehicule);
      System.out.println("Date du dernier contrôle: " + lastControl4);
      System.out.println("Date du prochain contrôle: " + nextControl4);
      if (nextControl4.isBefore(LocalDate.now())) {
        long joursDepasses4 =
            java.time.temporal.ChronoUnit.DAYS.between(nextControl4, LocalDate.now());
        System.out.println("Jours de retard: " + joursDepasses4);
      } else {
        long joursRestants4 =
            java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextControl4);
        System.out.println("Jours restants: " + joursRestants4);
      }
      System.out.println("Statut: " + controlTechniqueService.getStatutControleDetaille(vehicule));
      System.out.println(
          "🚨 NOTIFICATION CRITIQUE: Contrôle technique dépassé! Intervention immédiate requise!");
      System.out
          .println("⚠️  Le véhicule ne peut plus être loué tant que le contrôle n'est pas à jour.");

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
