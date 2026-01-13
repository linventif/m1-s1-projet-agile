package fr.univ.m1.projetagile._demo;

public class _RunAllDemosSimple {

  public static void main(String[] args) {
    run("UtilisateurDemoSimple", () -> UtilisateurDemoSimple.main(new String[0]));
    run("AgentDemoSimple", () -> AgentDemoSimple.main(new String[0]));
    run("LoueurDemoSimple", () -> LoueurDemoSimple.main(new String[0]));
    run("VehiculeDemoSimple", () -> VehiculeDemoSimple.main(new String[0]));
    run("DisponibiliteDemoSimple", () -> DisponibiliteDemoSimple.main(new String[0]));
    run("LocationDemoSimple", () -> LocationDemoSimple.main(new String[0]));
    run("VerificationDemoSimple", () -> VerificationDemoSimple.main(new String[0]));
    run("CritereDemoSimple", () -> CritereDemoSimple.main(new String[0]));
    run("NoteDemoSimple", () -> NoteDemoSimple.main(new String[0]));
    run("MessagerieDemoSimple", () -> MessagerieDemoSimple.main(new String[0]));
  }

  private static void run(String name, Runnable action) {
    System.out.println("\n==== " + name + " ====");
    try {
      action.run();
    } catch (Exception e) {
      System.err.println("✗ " + name + " a échoué: " + e.getMessage());
      e.printStackTrace(System.err);
    }
    System.out.println("==== Fin " + name + " ====");
  }
}
