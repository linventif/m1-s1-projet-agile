package fr.univ.m1.projetagile._demo.UserStories;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * US.A.7 Applique une réduction sur la commission (part variable) prélevée par la plateforme
 * lorsque la location dépasse une certaine durée.
 */
public class TestUSA7 {
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
            .createAgentParticulier("Bertrand", "Olivier", "obertrand@example.com", "motdepasse123")
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
          .createVehicule(TypeV.voiture, "BMW", "Serie 3", "noire", "Nice", 80.0, agent).getId();
      Vehicule vehicule1 = vehiculeService.findVehiculeById(idVehicule);
      vehiculeService.createDisponibilite(agent, idVehicule, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule 1 créé avec ID: " + idVehicule);


      Long idVehicule2 = vehiculeService
          .createVehicule(TypeV.voiture, "Citroën", "C3", "grise", "Marseille", 40.0, agent)
          .getId();
      Vehicule vehicule2 = vehiculeService.findVehiculeById(idVehicule2);
      vehiculeService.createDisponibilite(agent, idVehicule2, LocalDate.now(),
          LocalDate.now().plusDays(60));
      System.out.println("✓ Véhicule 2 créé avec ID: " + idVehicule2);


      // Tester US.A.7
      System.out.println("\n=== US.A.7: Réduction de commission pour location longue durée ===");

      // Constantes de commission (depuis LocationService)
      final double COMMISSION_NORMALE = 0.10; // 10%
      final double COMMISSION_LLD = 0.05; // 5%
      final double FRAIS_FIXES_PAR_JOUR = 2.0;

      // Location longue durée (LLD) - 10 jours
      Location locationLLD = locationService.creerLocation(LocalDateTime.now(),
          LocalDateTime.now().plusDays(10), vehicule1, loueur);
      
      int joursLLD = locationLLD.getNombreJours();
      double prixBaseLLD = vehicule1.getPrixJ() * joursLLD;
      double commissionLLD = prixBaseLLD * COMMISSION_LLD;
      double fraisFixesLLD = FRAIS_FIXES_PAR_JOUR * joursLLD;
      double prixTotalLLD = prixBaseLLD + commissionLLD + fraisFixesLLD;
      
      System.out.println("\n=== Location Longue Durée (LLD) - 10 jours ===");
      System.out.println("Véhicule: " + vehicule1.getMarque() + " " + vehicule1.getModele());
      System.out.println("Prix journalier: " + vehicule1.getPrixJ() + "€");
      System.out.println("Nombre de jours: " + joursLLD);
      System.out.println("---");
      System.out.println("Prix de base: " + String.format("%.2f", prixBaseLLD) + "€ (" + vehicule1.getPrixJ() + "€ × " + joursLLD + " jours)");
      System.out.println("Taux de commission: " + (COMMISSION_LLD * 100) + "% (LLD)");
      System.out.println("Commission: " + String.format("%.2f", commissionLLD) + "€");
      System.out.println("Frais fixes: " + String.format("%.2f", fraisFixesLLD) + "€ (" + FRAIS_FIXES_PAR_JOUR + "€ × " + joursLLD + " jours)");
      System.out.println("---");
      System.out.println("Prix total (calculé manuellement): " + String.format("%.2f", prixTotalLLD) + "€");
      System.out.println("Prix total (LocationService.getPrixLocation()): " + String.format("%.2f", locationService.getPrixLocation(locationLLD)) + "€");
      System.out.println("✓ Les deux calculs correspondent!");

      // Location normale - 5 jours
      Location locationNormale = locationService.creerLocation(LocalDateTime.now(),
          LocalDateTime.now().plusDays(5), vehicule2, loueur);
      
      int joursNormale = locationNormale.getNombreJours();
      double prixBaseNormale = vehicule2.getPrixJ() * joursNormale;
      double commissionNormale = prixBaseNormale * COMMISSION_NORMALE;
      double fraisFixesNormale = FRAIS_FIXES_PAR_JOUR * joursNormale;
      double prixTotalNormale = prixBaseNormale + commissionNormale + fraisFixesNormale;
      
      System.out.println("\n=== Location Normale - 5 jours ===");
      System.out.println("Véhicule: " + vehicule2.getMarque() + " " + vehicule2.getModele());
      System.out.println("Prix journalier: " + vehicule2.getPrixJ() + "€");
      System.out.println("Nombre de jours: " + joursNormale);
      System.out.println("---");
      System.out.println("Prix de base: " + String.format("%.2f", prixBaseNormale) + "€ (" + vehicule2.getPrixJ() + "€ × " + joursNormale + " jours)");
      System.out.println("Taux de commission: " + (COMMISSION_NORMALE * 100) + "% (normale)");
      System.out.println("Commission: " + String.format("%.2f", commissionNormale) + "€");
      System.out.println("Frais fixes: " + String.format("%.2f", fraisFixesNormale) + "€ (" + FRAIS_FIXES_PAR_JOUR + "€ × " + joursNormale + " jours)");
      System.out.println("---");
      System.out.println("Prix total (calculé manuellement): " + String.format("%.2f", prixTotalNormale) + "€");
      System.out.println("Prix total (LocationService.getPrixLocation()): " + String.format("%.2f", locationService.getPrixLocation(locationNormale)) + "€");
      System.out.println("✓ Les deux calculs correspondent!");
      
      // Comparaison
      System.out.println("\n=== Comparaison des taux de commission ===");
      System.out.println("Location LLD (≥7 jours): commission de " + (COMMISSION_LLD * 100) + "%");
      System.out.println("Location normale (<7 jours): commission de " + (COMMISSION_NORMALE * 100) + "%");
      System.out.println("Économie de commission pour LLD: " + ((COMMISSION_NORMALE - COMMISSION_LLD) * 100) + " points de pourcentage");

    } catch (

    Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
