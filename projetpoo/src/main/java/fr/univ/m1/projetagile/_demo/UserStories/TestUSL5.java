package fr.univ.m1.projetagile._demo.UserStories;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;

/**
 * US.L.5 Je veux pouvoir contacter un autre loueur par l'intermédiaire de la plateforme. (1)
 */
public class TestUSL5 {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée");

      LoueurService loueurService = new LoueurService(new LoueurRepository());
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());

      // Ensure we have test data
      Loueur loueur1 = loueurService.findById(1L);
      if (loueur1 == null) {
        Long idLoueur1 = loueurService
            .createLoueur("Martin", "Sophie", "sophie.martin@example.com", "motdepasse123")
            .getIdU();
        loueur1 = loueurService.findById(idLoueur1);
        System.out.println("✓ Loueur 1 créé avec ID: " + idLoueur1);
      }

      Loueur loueur2 = loueurService.findById(2L);
      if (loueur2 == null) {
        Long idLoueur2 = loueurService
            .createLoueur("Dubois", "Marie", "marie.dubois@example.com", "motdepasse123")
            .getIdU();
        loueur2 = loueurService.findById(idLoueur2);
        System.out.println("✓ Loueur 2 créé avec ID: " + idLoueur2);
      }

      // Test US.L.5
      System.out.println("\n=== US.L.5: Contact d'un autre loueur par messagerie ===");
      messagerieService.envoyerMessage(loueur1, loueur2,
          "Bonjour, je voudrais savoir si vous avez déjà loué un véhicule chez cet agent ?");

      List<Message> messages = messagerieService.getMessagesUtilisateur(loueur2);
      for (Message message : messages) {
        System.out.println("Message reçu par le loueur:");
        System.out.println(" - Contenu: " + message.getContenu());
        System.out.println(" - Date: " + message.getDateEnvoi());
      }

    } catch (Exception e) {
      System.err.println("✗ Erreur: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }
}
