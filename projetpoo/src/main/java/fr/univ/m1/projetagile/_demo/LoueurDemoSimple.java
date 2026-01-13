package fr.univ.m1.projetagile._demo;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.LoueurService;

public class LoueurDemoSimple {

  public static void main(String[] args) {
    long suffix = System.currentTimeMillis() % 1_000_000L;
    try {
      DatabaseConnection.init();

      LoueurService loueurService = new LoueurService(new LoueurRepository());

      // 4 loueurs simples
      Loueur l1 =
          loueurService.createLoueur("Moreau", "Emma", "emma" + suffix + "@loueur.demo", "pass");
      Loueur l2 =
          loueurService.createLoueur("Bernard", "Louis", "louis" + suffix + "@loueur.demo", "pass");
      Loueur l3 =
          loueurService.createLoueur("Lefevre", "Chloe", "chloe" + suffix + "@loueur.demo", "pass");
      Loueur l4 =
          loueurService.createLoueur("Roux", "Hugo", "hugo" + suffix + "@loueur.demo", "pass");

      System.out.println("Loueurs créés :");
      System.out.println(l1);
      System.out.println(l2);
      System.out.println(l3);
      System.out.println(l4);
    } finally {
      DatabaseConnection.close();
    }
  }
}
