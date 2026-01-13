package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.service.CritereService;

public class CritereDemoSimple {

  public static void main(String[] args) {
    try {
      DatabaseConnection.init();

      CritereService critereService = new CritereService();

      Critere c1 = critereService.creerCritere("Ponctualité", 8.5);
      Critere c2 = critereService.creerCritere("Communication", 9.0);
      Critere c3 = critereService.creerCritere("Professionnalisme", 8.0);
      Critere c4 = critereService.creerCritere("Qualité du véhicule", 9.5);

      System.out.println("Critères créés :");
      System.out.println(c1);
      System.out.println(c2);
      System.out.println(c3);
      System.out.println(c4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
