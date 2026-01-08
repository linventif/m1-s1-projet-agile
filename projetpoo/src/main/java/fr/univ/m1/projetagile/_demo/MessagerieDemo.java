package fr.univ.m1.projetagile._demo;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.persistence.AgentRepository;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.core.service.AgentService;
import fr.univ.m1.projetagile.core.service.LoueurService;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;
import fr.univ.m1.projetagile.messagerie.service.MessagerieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Démonstration complète du système de messagerie Couvre toutes les fonctionnalités du
 * MessagerieService avec exemples et validations
 */
public class MessagerieDemo {
  public static void main(String[] args) {
    try {
      DatabaseConnection.init();
      System.out.println("✓ DB connectée\n");

      // Initialiser les services
      AgentService agentService = new AgentService(new AgentRepository());
      LoueurService loueurService = new LoueurService(new LoueurRepository());
      MessagerieService messagerieService = new MessagerieService(new MessageRepository());

      // ========================================
      // 1. CRÉATION DES UTILISATEURS
      // ========================================
      System.out.println("╔════════════════════════════════════════╗");
      System.out.println("║   1. CRÉATION DES UTILISATEURS        ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      // Créer ou récupérer les utilisateurs
      AgentParticulier agent1 = (AgentParticulier) agentService.findByEmail("alice@example.com");
      if (agent1 == null) {
        agent1 = agentService.createAgentParticulier("Smith", "Alice", "alice@example.com",
            "password123", "0612345678");
        System.out
            .println("✓ Agent particulier créé: " + agent1.getPrenom() + " " + agent1.getNom());
      } else {
        System.out
            .println("✓ Agent particulier existant: " + agent1.getPrenom() + " " + agent1.getNom());
      }

      AgentProfessionnel agent2 =
          (AgentProfessionnel) agentService.findByEmail("contact@prorent.fr");
      if (agent2 == null) {
        agent2 = agentService.createAgentProfessionnel("contact@prorent.fr", "password123",
            "12345678901234", "ProRent");
        System.out.println("✓ Agent professionnel créé: " + agent2.getNomEntreprise());
      } else {
        System.out.println("✓ Agent professionnel existant: " + agent2.getNomEntreprise());
      }

      Loueur loueur1 = loueurService.findByEmail("john@example.com");
      if (loueur1 == null) {
        loueur1 = loueurService.createLoueur("Doe", "John", "john@example.com", "password123");
        System.out.println("✓ Loueur créé: " + loueur1.getPrenom() + " " + loueur1.getNom());
      } else {
        System.out.println("✓ Loueur existant: " + loueur1.getPrenom() + " " + loueur1.getNom());
      }

      Loueur loueur2 = loueurService.findByEmail("maria@example.com");
      if (loueur2 == null) {
        loueur2 = loueurService.createLoueur("Garcia", "Maria", "maria@example.com", "password123");
        System.out.println("✓ Loueur créé: " + loueur2.getPrenom() + " " + loueur2.getNom());
      } else {
        System.out.println("✓ Loueur existant: " + loueur2.getPrenom() + " " + loueur2.getNom());
      }

      // ========================================
      // 2. ENVOI DE MESSAGES
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   2. ENVOI DE MESSAGES                ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      // Conversation Loueur1 <-> Agent1
      System.out.println("--- Conversation: John <-> Alice ---");
      Message msg1 = messagerieService.envoyerMessage(loueur1, agent1,
          "Bonjour, je suis intéressé par votre véhicule Peugeot 308.");
      System.out.println("✓ " + loueur1.getPrenom() + " → " + agent1.getPrenom() + ": "
          + msg1.getContenu().substring(0, 30) + "...");

      Message msg2 = messagerieService.envoyerMessage(agent1, loueur1,
          "Bonjour John ! Le véhicule est disponible. Quelles dates vous intéressent ?");
      System.out.println("✓ " + agent1.getPrenom() + " → " + loueur1.getPrenom() + ": "
          + msg2.getContenu().substring(0, 30) + "...");

      Message msg3 = messagerieService.envoyerMessage(loueur1, agent1,
          "Je souhaite le louer du 15 au 20 décembre.");
      System.out.println(
          "✓ " + loueur1.getPrenom() + " → " + agent1.getPrenom() + ": " + msg3.getContenu());

      Message msg4 = messagerieService.envoyerMessage(agent1, loueur1,
          "Parfait ! Je vous prépare le contrat.");
      System.out.println(
          "✓ " + agent1.getPrenom() + " → " + loueur1.getPrenom() + ": " + msg4.getContenu());

      // Conversation Loueur2 <-> Agent2
      System.out.println("\n--- Conversation: Maria <-> ProRent ---");
      Message msg5 = messagerieService.envoyerMessage(loueur2, agent2,
          "Bonjour, avez-vous des véhicules utilitaires disponibles ?");
      System.out.println("✓ " + loueur2.getPrenom() + " → " + agent2.getNomEntreprise() + ": "
          + msg5.getContenu().substring(0, 30) + "...");

      Message msg6 = messagerieService.envoyerMessage(agent2, loueur2,
          "Oui, nous avons plusieurs camions disponibles. Quelle capacité recherchez-vous ?");
      System.out.println("✓ " + agent2.getNomEntreprise() + " → " + loueur2.getPrenom() + ": "
          + msg6.getContenu().substring(0, 30) + "...");

      Message msg7 = messagerieService.envoyerMessage(loueur2, agent2,
          "Un camion d'environ 20m³ pour un déménagement.");
      System.out.println("✓ " + loueur2.getPrenom() + " → " + agent2.getNomEntreprise() + ": "
          + msg7.getContenu());

      // ========================================
      // 3. RÉCUPÉRATION DES MESSAGES
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   3. RÉCUPÉRATION DES MESSAGES        ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      // Messages d'un utilisateur (envoyés + reçus)
      System.out.println("--- Tous les messages de John ---");
      List<Message> messagesJohn = messagerieService.getMessagesUtilisateur(loueur1);
      for (Message msg : messagesJohn) {
        String direction = msg.getExpediteurId().equals(loueur1.getIdU()) ? "→ Envoyé" : "← Reçu";
        System.out.println(direction + ": "
            + msg.getContenu().substring(0, Math.min(50, msg.getContenu().length())));
      }

      // Messages envoyés
      System.out.println("\n--- Messages envoyés par Maria ---");
      List<Message> envoyesMaria = messagerieService.getMessagesEnvoyes(loueur2);
      for (Message msg : envoyesMaria) {
        System.out.println("→ " + msg.getContenu());
      }

      // Messages reçus
      System.out.println("\n--- Messages reçus par Alice ---");
      List<Message> recusAlice = messagerieService.getMessagesRecus(agent1);
      for (Message msg : recusAlice) {
        System.out.println("← " + msg.getContenu());
      }

      // ========================================
      // 4. CONVERSATIONS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   4. CONVERSATIONS                    ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Conversation complète: John <-> Alice ---");
      List<Message> conv1 = messagerieService.getConversation(loueur1, agent1);
      for (Message msg : conv1) {
        String expediteur = msg.getExpediteurId().equals(loueur1.getIdU()) ? "John" : "Alice";
        System.out.println("[" + expediteur + "] " + msg.getContenu());
      }

      System.out.println("\n--- Conversation complète: Maria <-> ProRent ---");
      List<Message> conv2 = messagerieService.getConversation(loueur2, agent2);
      for (Message msg : conv2) {
        String expediteur = msg.getExpediteurId().equals(loueur2.getIdU()) ? "Maria" : "ProRent";
        System.out.println("[" + expediteur + "] " + msg.getContenu());
      }

      // ========================================
      // 5. STATISTIQUES ET COMPTEURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   5. STATISTIQUES                     ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Statistiques John ---");
      System.out
          .println("Messages envoyés: " + messagerieService.getMessagesEnvoyes(loueur1).size());
      System.out.println("Messages reçus: " + messagerieService.getMessagesRecus(loueur1).size());
      System.out
          .println("Total messages: " + messagerieService.getMessagesUtilisateur(loueur1).size());
      System.out.println(
          "Messages avec Alice: " + messagerieService.compterMessagesConversation(loueur1, agent1));
      System.out.println(
          "A échangé avec Alice: " + messagerieService.ontEchangeMessages(loueur1, agent1));

      System.out.println("\n--- Statistiques Maria ---");
      System.out
          .println("Messages envoyés: " + messagerieService.getMessagesEnvoyes(loueur2).size());
      System.out.println("Messages reçus: " + messagerieService.getMessagesRecus(loueur2).size());
      System.out.println("Messages avec ProRent: "
          + messagerieService.compterMessagesConversation(loueur2, agent2));

      // ========================================
      // 6. RÉCUPÉRATION PAR ID
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   6. RÉCUPÉRATION PAR ID              ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      Message retrieved = messagerieService.getMessageById(msg1.getId());
      if (retrieved != null) {
        System.out
            .println("✓ Message récupéré (ID " + msg1.getId() + "): " + retrieved.getContenu());
      }

      // ========================================
      // 7. SUPPRESSION DE MESSAGES
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   7. SUPPRESSION DE MESSAGES          ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      // Créer un message temporaire pour le supprimer
      Message msgTemp = messagerieService.envoyerMessage(loueur1, agent1, "Message à supprimer");
      System.out.println("✓ Message créé (ID " + msgTemp.getId() + ")");

      messagerieService.supprimerMessage(msgTemp.getId());
      System.out.println("✓ Message supprimé");

      Message verif = messagerieService.getMessageById(msgTemp.getId());
      System.out.println(
          "Vérification après suppression: " + (verif == null ? "✓ NULL" : "✗ Existe encore"));

      // ========================================
      // 8. VALIDATIONS ET ERREURS
      // ========================================
      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   8. TESTS DE VALIDATION              ║");
      System.out.println("╚════════════════════════════════════════╝\n");

      System.out.println("--- Test: Contenu vide ---");
      try {
        messagerieService.envoyerMessage(loueur1, agent1, "");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Contenu null ---");
      try {
        messagerieService.envoyerMessage(loueur1, agent1, null);
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Expéditeur null ---");
      try {
        messagerieService.envoyerMessage(null, agent1, "Test");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Destinataire null ---");
      try {
        messagerieService.envoyerMessage(loueur1, null, "Test");
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n--- Test: Message trop long (>1000 caractères) ---");
      try {
        String longMessage = "A".repeat(1001);
        messagerieService.envoyerMessage(loueur1, agent1, longMessage);
      } catch (IllegalArgumentException e) {
        System.out.println("✓ Exception attendue: " + e.getMessage());
      }

      System.out.println("\n╔════════════════════════════════════════╗");
      System.out.println("║   ✓ DÉMONSTRATION TERMINÉE            ║");
      System.out.println("╚════════════════════════════════════════╝");

    } catch (Exception e) {
      System.err.println("\n✗ ERREUR: " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseConnection.close();
    }
  }}

  // Correction des valeurs type_agent dans la base si nécessaire
  corrigerTypeAgent();

  // Utiliser un seul EntityManager réutilisé par thread
  EntityManager em = DatabaseConnection.getEntityManager();em.getTransaction().begin();

  // Get or create Loueur
  String loueurEmail = "jdoe@example.com";
  TypedQuery<Loueur> loueurQuery = em.createQuery("SELECT l FROM Loueur l WHERE l.email = :email",
      Loueur.class);loueurQuery.setParameter("email",loueurEmail);
  Loueur loueur = loueurQuery.getResultList().stream().findFirst().orElse(null);

  if(loueur==null)
  {
    loueur = new Loueur("Doe", "John", loueurEmail, "securePassword");
    em.persist(loueur);
    System.out.println("✓ Loueur créé avec l'ID: " + loueur.getIdU());
  }else
  {
    System.out.println("✓ Loueur existant trouvé avec l'ID: " + loueur.getIdU());
  }

  // Get or create Agent
  String agentEmail = "asmith@example.com";
  TypedQuery<AgentParticulier> agentQuery =
      em.createQuery("SELECT a FROM AgentParticulier a WHERE a.email = :email",
          AgentParticulier.class);agentQuery.setParameter("email",agentEmail);
  AgentParticulier agent = agentQuery.getResultList().stream().findFirst().orElse(null);

  if(agent==null)
  {
    agent = new AgentParticulier("Smith", "Alice", agentEmail, "securePassword", "0612345678");
    em.persist(agent);
    System.out.println("✓ Agent créé avec l'ID: " + agent.getIdU());
  }else
  {
    System.out.println("✓ Agent existant trouvé avec l'ID: " + agent.getIdU());
  }

  em.getTransaction().commit();

  // ======== Utilisation du MessagerieService ========
  System.out.println("\n=== Démonstration du MessagerieService ===\n");

  MessagerieService service = new MessagerieService();

  // Envoi de messages via le service (sauvegarde automatique)
  System.out.println("📧 Envoi de messages...");
  Message msg1 = service.envoyerMessage(loueur, agent,
      "Bonjour, je suis intéressé par votre véhicule Peugeot 208.");System.out.println("✓ Message 1 envoyé et sauvegardé (ID: "+msg1.getId()+")");

  Message msg2 = service.envoyerMessage(agent, loueur,
      "Bonjour ! Oui, il est disponible. Quand souhaitez-vous le louer ?");System.out.println("✓ Message 2 envoyé et sauvegardé (ID: "+msg2.getId()+")");

  Message msg3 = service.envoyerMessage(loueur, agent,
      "Je souhaiterais le louer du 15 au 20 décembre.");System.out.println("✓ Message 3 envoyé et sauvegardé (ID: "+msg3.getId()+")");

  Message msg4 = service.envoyerMessage(agent, loueur,
      "Parfait ! Je vais préparer le contrat de location.");System.out.println("✓ Message 4 envoyé et sauvegardé (ID: "+msg4.getId()+")");

  // Utilisation via les méthodes de Utilisateur
  System.out.println("\n📧 Envoi via méthode Utilisateur.envoyerMessage()...");
  Message msg5 = loueur.envoyerMessage(agent,
      "Merci beaucoup !");System.out.println("✓ Message 5 envoyé (ID: "+msg5.getId()+")");

  // Récupération des messages du loueur
  System.out.println("\n=== Messages du Loueur (via service) ===");
  List<Message> messagesLoueur = service.getMessagesUtilisateur(loueur);for(
  Message msg:messagesLoueur)
  {
    String direction = msg.getExpediteurId().equals(loueur.getIdU()) ? "→ Envoyé" : "← Reçu";
    System.out.println(direction + ": " + msg.getContenu());
  }

  // Récupération de la conversation
  System.out.println("\n=== Conversation complète (via service) ===");
  List<Message> conversation = service.getConversation(loueur, agent);for(
  Message msg:conversation)
  {
    String expediteur = msg.getExpediteurId().equals(loueur.getIdU()) ? "Loueur" : "Agent";
    System.out.println("[" + expediteur + "] " + msg.getContenu());
  }

  // Utilisation via méthode de Utilisateur
  System.out.println("\n=== Conversation via Utilisateur.getConversationAvec() ===");
  List<Message> conversationViaUtilisateur = loueur.getConversationAvec(
      agent);System.out.println("Nombre de messages échangés: "+conversationViaUtilisateur.size());

  // Statistiques
  System.out.println("\n=== Statistiques ===");System.out.println("Messages envoyés par le loueur: "+service.getMessagesEnvoyes(loueur).size());System.out.println("Messages reçus par le loueur: "+service.getMessagesRecus(loueur).size());System.out.println("Total messages du loueur: "+service.getMessagesUtilisateur(loueur).size());System.out.println("Messages dans la conversation: "+service.compterMessagesConversation(loueur,agent));System.out.println("Ont échangé des messages: "+service.ontEchangeMessages(loueur,agent));

  // Récupération de la conversation complète
  System.out.println("\n=== Conversation entre Loueur et Agent ===");
  List<Message> conversationComplete = service.getConversation(loueur, agent);for(
  Message msg:conversationComplete)
  {
    System.out.println("[" + msg.getDateEnvoi() + "] " + msg.getContenu());
  }

  System.out.println("\n✓ Tous les éléments ont été sauvegardés avec succès!");

  }catch(
  Exception e)
  {
    System.err.println("✗ Erreur: " + e.getMessage());
    e.printStackTrace();

    // Rollback si transaction active
    try {
      EntityManager em = DatabaseConnection.getEntityManager();
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
    } catch (Exception rollbackEx) {
      System.err.println("Erreur lors du rollback: " + rollbackEx.getMessage());
    }

  }finally
  {
    // Ferme l'EntityManager du thread et l'EntityManagerFactory
    DatabaseConnection.close();
  }
  }

  /**
   * Corrige les valeurs type_agent dans la base pour correspondre à l'enum TypeAgent Change
   * "AgentParticulier" en "PARTICULIER" et "AgentProfessionnel" en "PROFESSIONNEL"
   */
  private static void corrigerTypeAgent() {
    EntityManager em = DatabaseConnection.getEntityManager();
    try {
      em.getTransaction().begin();

      // Correction pour AgentParticulier -> PARTICULIER
      int updated1 = em
          .createNativeQuery(
              "UPDATE agents SET type_agent = 'PARTICULIER' WHERE type_agent = 'AgentParticulier'")
          .executeUpdate();

      // Correction pour AgentProfessionnel -> PROFESSIONNEL
      int updated2 = em.createNativeQuery(
          "UPDATE agents SET type_agent = 'PROFESSIONNEL' WHERE type_agent = 'AgentProfessionnel'")
          .executeUpdate();

      em.getTransaction().commit();

      if (updated1 > 0 || updated2 > 0) {
        System.out.println("✓ Correction des type_agent: " + updated1 + " particuliers, " + updated2
            + " professionnels\n");
      }

    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      // Ignorer silencieusement si déjà corrigé ou si erreur
    }
  }
}
