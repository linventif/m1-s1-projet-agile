package fr.univ.m1.projetagile._demo;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.core.service.VehiculeService;
import fr.univ.m1.projetagile.enums.TypeV;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.service.CritereService;
import fr.univ.m1.projetagile.notes.service.NoteService;

public class NoteDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      VehiculeService vehiculeService = new VehiculeService(new VehiculeRepository());
      CritereService critereService = new CritereService();
      NoteService noteService = new NoteService();

      AgentParticulier agent = agentService.createAgentParticulier("Note", "Agent",
          "noteagent" + suffix + "@demo", "pass", "341" + String.format("%08d", suffix));
      Loueur loueur =
          loueurService.createLoueur("Note", "Loueur", "noteloueur" + suffix + "@demo", "pass");
      Vehicule vehicule = vehiculeService.createVehicule(TypeV.voiture, "Opel", "Corsa", "Blanc",
          "Rennes", 42.0, agent);

      List<Critere> critAgent = critereService.getOrCreateCriteres(
          new String[] {"Ponctualité", "Communication"}, new Double[] {8.0, 9.0});
      List<Critere> critLoueur = critereService.getOrCreateCriteres(
          new String[] {"Respect du véhicule", "Ponctualité"}, new Double[] {9.0, 8.5});
      List<Critere> critVehicule = critereService.getOrCreateCriteres(
          new String[] {"Propreté", "Confort", "État mécanique"}, new Double[] {8.5, 8.0, 9.0});

      // 4 notes simples
      NoteAgent noteAgent = noteService.noterAgent(loueur, agent, critAgent);
      NoteAgent noteAgentLegacy = noteService.noterAgent(loueur, agent, 7.5, 8.0, 8.5);
      NoteLoueur noteLoueur = noteService.noterLoueur(agent, loueur, critLoueur);
      NoteVehicule noteVehicule = noteService.noterVehicule(loueur, vehicule, critVehicule);

      System.out.println("Notes créées :");
      System.out.println(noteAgent);
      System.out.println(noteAgentLegacy);
      System.out.println(noteLoueur);
      System.out.println(noteVehicule);
    } finally {
      DatabaseConnection.close();
    }
  }
}
