// package fr.univ.m1.projetagile._demo;

// import java.util.List;
// import fr.univ.m1.projetagile.core.DatabaseConnection;
// import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
// import fr.univ.m1.projetagile.core.entity.AgentParticulier;
// import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
// import fr.univ.m1.projetagile.core.entity.Vehicule;
// import fr.univ.m1.projetagile.core.persistence.AgentRepository;
// import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
// import fr.univ.m1.projetagile.core.service.AgentService;
// import fr.univ.m1.projetagile.core.service.VehiculeService;
// import fr.univ.m1.projetagile.enums.TypeV;

// /**
//  * Démonstration complète de la gestion des véhicules Couvre toutes les fonctionnalités du
//  * VehiculeService avec exemples et validations
//  */
// public class VehiculeDemo {
//   public static void main(String[] args) {
//     try {
//       DatabaseConnection.init();
//       System.out.println("✓ DB connectée\n");

//       AgentService agentService = new AgentService(new AgentRepository());
//       VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());

//       // ========================================
//       // 1. CRÉATION DES PROPRIÉTAIRES
//       // ========================================
//       System.out.println("╔════════════════════════════════════════╗");
//       System.out.println("║   1. CRÉATION DES PROPRIÉTAIRES       ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       AgentParticulier bob = (AgentParticulier) agentService.findByEmail("bob.maurise@gmail.com");
//       if (bob == null) {
//         bob = agentService.createAgentParticulier("Maurise", "Bob", "bob.maurise@gmail.com",
//             "p@ssw0rd", "33601020304");
//         System.out.println("✓ Agent créé: " + bob.getPrenom() + " " + bob.getNom());
//       } else {
//         System.out.println("✓ Agent existant: " + bob.getPrenom() + " " + bob.getNom());
//       }

//       AgentParticulier alice =
//           (AgentParticulier) agentService.findByEmail("alice.dupont@gmail.com");
//       if (alice == null) {
//         alice = agentService.createAgentParticulier("Dupont", "Alice", "alice.dupont@gmail.com",
//             "p@ssw0rd", "33605060708");
//         System.out.println("✓ Agent créé: " + alice.getPrenom() + " " + alice.getNom());
//       } else {
//         System.out.println("✓ Agent existant: " + alice.getPrenom() + " " + alice.getNom());
//       }

//       AgentProfessionnel locaSmart =
//           (AgentProfessionnel) agentService.findByEmail("contact@locasmart.fr");
//       if (locaSmart == null) {
//         locaSmart = agentService.createAgentProfessionnel("contact@locasmart.fr", "p@ssw0rd",
//             "12345678901234", "LocaSmart");
//         System.out.println("✓ Agent créé: " + locaSmart.getNomEntreprise());
//       } else {
//         System.out.println("✓ Agent existant: " + locaSmart.getNomEntreprise());
//       }

//       // ========================================
//       // 2. CRÉATION DE VÉHICULES (VOITURES)
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   2. CRÉATION DE VOITURES             ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       Vehicule v1 = vehiculeService.createVehicule(TypeV.voiture, "Peugeot", "308", "Blanc",
//           "Paris", 45.0, bob);
//       System.out.println("✓ Créé: " + v1.getMarque() + " " + v1.getModele() + " - "
//           + v1.getCouleur() + " (" + v1.getPrixJ() + "€/jour) à " + v1.getVille());

//       Vehicule v2 = vehiculeService.createVehicule(TypeV.voiture, "Renault", "Clio", "Rouge",
//           "Lyon", 35.0, alice);
//       System.out.println("✓ Créé: " + v2.getMarque() + " " + v2.getModele() + " - "
//           + v2.getCouleur() + " (" + v2.getPrixJ() + "€/jour) à " + v2.getVille());

//       Vehicule v3 = vehiculeService.createVehicule(TypeV.voiture, "Mercedes", "Classe A", "Noir",
//           "Toulouse", 65.0, locaSmart);
//       System.out.println("✓ Créé: " + v3.getMarque() + " " + v3.getModele() + " - "
//           + v3.getCouleur() + " (" + v3.getPrixJ() + "€/jour) à " + v3.getVille());

//       Vehicule v4 = vehiculeService.createVehicule(TypeV.voiture, "BMW", "Serie 3", "Bleu", "Nice",
//           70.0, bob);
//       System.out.println("✓ Créé: " + v4.getMarque() + " " + v4.getModele() + " - "
//           + v4.getCouleur() + " (" + v4.getPrixJ() + "€/jour) à " + v4.getVille());

//       // ========================================
//       // 3. CRÉATION DE MOTOS
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   3. CRÉATION DE MOTOS                ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       Vehicule m1 = vehiculeService.createVehicule(TypeV.moto, "Yamaha", "MT-07", "Noir", "Lyon",
//           35.0, alice);
//       System.out.println("✓ Créé: " + m1.getMarque() + " " + m1.getModele() + " - "
//           + m1.getCouleur() + " (" + m1.getPrixJ() + "€/jour) à " + m1.getVille());

//       Vehicule m2 = vehiculeService.createVehicule(TypeV.moto, "Honda", "CB500X", "Rouge",
//           "Marseille", 40.0, bob);
//       System.out.println("✓ Créé: " + m2.getMarque() + " " + m2.getModele() + " - "
//           + m2.getCouleur() + " (" + m2.getPrixJ() + "€/jour) à " + m2.getVille());

//       Vehicule m3 = vehiculeService.createVehicule(TypeV.moto, "Kawasaki", "Z900", "Vert",
//           "Bordeaux", 50.0, locaSmart);
//       System.out.println("✓ Créé: " + m3.getMarque() + " " + m3.getModele() + " - "
//           + m3.getCouleur() + " (" + m3.getPrixJ() + "€/jour) à " + m3.getVille());

//       // ========================================
//       // 4. CRÉATION DE CAMIONS
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   4. CRÉATION DE CAMIONS              ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       Vehicule c1 = vehiculeService.createVehicule(TypeV.camion, "Renault", "Master", "Blanc",
//           "Marseille", 80.0, locaSmart);
//       System.out.println("✓ Créé: " + c1.getMarque() + " " + c1.getModele() + " - "
//           + c1.getCouleur() + " (" + c1.getPrixJ() + "€/jour) à " + c1.getVille());

//       Vehicule c2 = vehiculeService.createVehicule(TypeV.camion, "Fiat", "Ducato", "Gris", "Paris",
//           75.0, bob);
//       System.out.println("✓ Créé: " + c2.getMarque() + " " + c2.getModele() + " - "
//           + c2.getCouleur() + " (" + c2.getPrixJ() + "€/jour) à " + c2.getVille());

//       // ========================================
//       // 5. LISTE DE TOUS LES VÉHICULES
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   5. LISTE DE TOUS LES VÉHICULES      ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       List<VehiculeDTO> allVehicules = vehiculeService.getVehicules();
//       System.out.println("Nombre total de véhicules: " + allVehicules.size());
//       for (VehiculeDTO dto : allVehicules) {
//         System.out.println("  • " + dto.getType() + " - " + dto.getMarque() + " " + dto.getModele()
//             + " (" + dto.getPrixJ() + "€/jour) à " + dto.getVille());
//       }

//       // ========================================
//       // 6. VÉHICULES PAR AGENT
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   6. VÉHICULES PAR AGENT              ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       System.out.println("--- Véhicules de Bob ---");
//       List<VehiculeDTO> vehiculesBob = vehiculeService.getVehiculesByAgent(bob);
//       System.out.println("Nombre: " + vehiculesBob.size());
//       for (VehiculeDTO dto : vehiculesBob) {
//         System.out.println("  • " + dto.getMarque() + " " + dto.getModele() + " (" + dto.getType()
//             + ") - " + dto.getVille());
//       }

//       System.out.println("\n--- Véhicules d'Alice ---");
//       List<VehiculeDTO> vehiculesAlice = vehiculeService.getVehiculesByAgent(alice);
//       System.out.println("Nombre: " + vehiculesAlice.size());
//       for (VehiculeDTO dto : vehiculesAlice) {
//         System.out.println("  • " + dto.getMarque() + " " + dto.getModele() + " (" + dto.getType()
//             + ") - " + dto.getVille());
//       }

//       System.out.println("\n--- Véhicules de LocaSmart ---");
//       List<VehiculeDTO> vehiculesLocaSmart = vehiculeService.getVehiculesByAgent(locaSmart);
//       System.out.println("Nombre: " + vehiculesLocaSmart.size());
//       for (VehiculeDTO dto : vehiculesLocaSmart) {
//         System.out.println("  • " + dto.getMarque() + " " + dto.getModele() + " (" + dto.getType()
//             + ") - " + dto.getVille());
//       }

//       // ========================================
//       // 7. RECHERCHE AVEC FILTRES
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   7. RECHERCHE AVEC FILTRES           ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       System.out.println("--- Filtre: Type = voiture ---");
//       List<VehiculeDTO> voitures = vehiculeService.searchVehiculesWithFilters(null, null, null,
//           null, null, null, null, null, TypeV.voiture);
//       System.out.println("Nombre de voitures: " + voitures.size());
//       for (VehiculeDTO dto : voitures) {
//         System.out
//             .println("  • " + dto.getMarque() + " " + dto.getModele() + " à " + dto.getVille());
//       }

//       System.out.println("\n--- Filtre: Type = moto ---");
//       List<VehiculeDTO> motos = vehiculeService.searchVehiculesWithFilters(null, null, null, null,
//           null, null, null, null, TypeV.moto);
//       System.out.println("Nombre de motos: " + motos.size());
//       for (VehiculeDTO dto : motos) {
//         System.out
//             .println("  • " + dto.getMarque() + " " + dto.getModele() + " à " + dto.getVille());
//       }

//       System.out.println("\n--- Filtre: Ville = Lyon ---");
//       List<VehiculeDTO> vehiculesLyon = vehiculeService.searchVehiculesWithFilters(null, null,
//           "Lyon", null, null, null, null, null, null);
//       System.out.println("Nombre de véhicules à Lyon: " + vehiculesLyon.size());
//       for (VehiculeDTO dto : vehiculesLyon) {
//         System.out
//             .println("  • " + dto.getType() + " - " + dto.getMarque() + " " + dto.getModele());
//       }

//       System.out.println("\n--- Filtre: Prix entre 30€ et 50€ ---");
//       List<VehiculeDTO> vehiculesPrix = vehiculeService.searchVehiculesWithFilters(null, null, null,
//           null, null, null, 30.0, 50.0, null);
//       System.out.println("Nombre de véhicules: " + vehiculesPrix.size());
//       for (VehiculeDTO dto : vehiculesPrix) {
//         System.out.println(
//             "  • " + dto.getMarque() + " " + dto.getModele() + " - " + dto.getPrixJ() + "€/jour");
//       }

//       System.out.println("\n--- Filtre: Marque = Renault ---");
//       List<VehiculeDTO> renault = vehiculeService.searchVehiculesWithFilters(null, null, null,
//           "Renault", null, null, null, null, null);
//       System.out.println("Nombre de véhicules Renault: " + renault.size());
//       for (VehiculeDTO dto : renault) {
//         System.out
//             .println("  • " + dto.getType() + " - " + dto.getModele() + " à " + dto.getVille());
//       }

//       System.out.println("\n--- Filtre combiné: voiture + Paris + max 60€ ---");
//       List<VehiculeDTO> combine = vehiculeService.searchVehiculesWithFilters(null, null, "Paris",
//           null, null, null, null, 60.0, TypeV.voiture);
//       System.out.println("Nombre de véhicules: " + combine.size());
//       for (VehiculeDTO dto : combine) {
//         System.out.println(
//             "  • " + dto.getMarque() + " " + dto.getModele() + " - " + dto.getPrixJ() + "€/jour");
//       }

//       // ========================================
//       // 8. SUPPRESSION DE VÉHICULE
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   8. SUPPRESSION DE VÉHICULE          ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       // Créer un véhicule temporaire pour le supprimer
//       Vehicule temp = vehiculeService.createVehicule(TypeV.voiture, "Test", "Model", "Blanc",
//           "Paris", 50.0, bob);
//       System.out.println("✓ Véhicule temporaire créé (ID: " + temp.getId() + ")");

//       vehiculeService.deleteVehiculeForAgent(bob, temp.getId());
//       System.out.println("✓ Véhicule supprimé");

//       // Vérifier les véhicules restants de Bob
//       List<VehiculeDTO> bobApres = vehiculeService.getVehiculesByAgent(bob);
//       System.out.println("Véhicules de Bob après suppression: " + bobApres.size());

//       // ========================================
//       // 9. TESTS DE VALIDATION
//       // ========================================
//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   9. TESTS DE VALIDATION              ║");
//       System.out.println("╚════════════════════════════════════════╝\n");

//       System.out.println("--- Test: Type null ---");
//       try {
//         vehiculeService.createVehicule(null, "Test", "Model", "Blanc", "Paris", 50.0, bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Marque vide ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "", "Model", "Blanc", "Paris", 50.0, bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Modèle null ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "Test", null, "Blanc", "Paris", 50.0, bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Prix négatif ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "Test", "Model", "Blanc", "Paris", -10.0,
//             bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Prix = 0 ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "Test", "Model", "Blanc", "Paris", 0.0, bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Ville vide ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "Test", "Model", "Blanc", "   ", 50.0, bob);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Propriétaire null ---");
//       try {
//         vehiculeService.createVehicule(TypeV.voiture, "Test", "Model", "Blanc", "Paris", 50.0,
//             null);
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n--- Test: Suppression par mauvais propriétaire ---");
//       try {
//         // Tenter de supprimer un véhicule de Bob avec le compte d'Alice
//         vehiculeService.deleteVehiculeForAgent(alice, v1.getId());
//       } catch (IllegalArgumentException e) {
//         System.out.println("✓ Exception attendue: " + e.getMessage());
//       }

//       System.out.println("\n╔════════════════════════════════════════╗");
//       System.out.println("║   ✓ DÉMONSTRATION TERMINÉE            ║");
//       System.out.println("╚════════════════════════════════════════╝");

//     } catch (Exception e) {
//       System.err.println("\n✗ ERREUR: " + e.getMessage());
//       e.printStackTrace();
//     } finally {
//       DatabaseConnection.close();
//     }
//   }
// }
