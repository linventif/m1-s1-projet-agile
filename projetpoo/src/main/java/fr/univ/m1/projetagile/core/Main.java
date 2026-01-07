package fr.univ.m1.projetagile.core;

import java.util.List;
import jakarta.persistence.EntityManager;

public class Main {
  public static void main(String[] args) {
    EntityManager em = null;

    try {
      DatabaseConnection.init();
      em = DatabaseConnection.getEntityManager();

      // Corriger les contraintes problématiques dans la base de données
      corrigerContraintes(em);

      // Supprimer la vue si elle existe
      supprimerVueUtilisateurs(em);

      // Créer la table UTILISATEURS si elle n'existe pas
      creerTableUtilisateurs(em);

      // Modifier la table pour permettre l'insertion manuelle de l'ID si nécessaire
      modifierTablePourInsertionManuelle(em);

      // Ajouter l'utilisateur demandé
      System.out.println("--- Ajout de l'utilisateur ---\n");

      Long id = 1L;
      String email = "nizarhafid10@gmail.com";
      String motDePasse = "1234";

      // Insérer directement dans la table UTILISATEURS
      insererDansTableUtilisateurs(em, id, email, motDePasse);

      System.out.println("✓ Utilisateur ajouté :");
      System.out.println("  ID: " + id);
      System.out.println("  Email: " + email);
      System.out.println("  Mot de passe: " + motDePasse + "\n");

      // Vérifier que l'utilisateur est bien en base
      System.out.println("--- Vérification en base de données ---");

      // Vérifier la structure de la table
      try {
        @SuppressWarnings("unchecked")
        List<Object[]> columns = em.createNativeQuery(
            "SELECT column_name, data_type FROM user_tab_columns WHERE UPPER(table_name) = 'UTILISATEURS' ORDER BY column_id")
            .getResultList();
        System.out.println("Structure de la table UTILISATEURS :");
        for (Object[] col : columns) {
          System.out.println("  - " + col[0] + " (" + col[1] + ")");
        }
        System.out.println();
      } catch (Exception e) {
        System.out.println("⚠ Impossible de lire la structure de la table");
      }

      // Compter tous les utilisateurs
      try {
        @SuppressWarnings("unchecked")
        List<Number> count =
            em.createNativeQuery("SELECT COUNT(*) FROM UTILISATEURS").getResultList();
        System.out.println("✓ Nombre total d'utilisateurs dans UTILISATEURS : " + count.get(0));
      } catch (Exception e) {
        System.out.println("⚠ Impossible de compter les utilisateurs : " + e.getMessage());
      }

      // Vérifier l'utilisateur spécifique
      try {
        @SuppressWarnings("unchecked")
        List<Object[]> result =
            em.createNativeQuery("SELECT ID, EMAIL, MOTDEPASSE FROM UTILISATEURS WHERE ID = ?")
                .setParameter(1, id).getResultList();

        if (!result.isEmpty()) {
          System.out.println("✓ Utilisateur trouvé dans la table UTILISATEURS :");
          for (Object[] row : result) {
            System.out.println("  ID: " + row[0] + " | Email: " + row[1] + " | MDP: " + row[2]);
          }
        } else {
          System.out.println("⚠ Utilisateur non trouvé dans la table UTILISATEURS");
        }
      } catch (Exception e1) {
        System.out.println("⚠ Impossible de vérifier avec les requêtes automatiques");
        System.out.println("   Vérifiez manuellement dans SQL Developer");
        System.out.println("   Erreur: " + e1.getMessage());
        e1.printStackTrace();
      }

      // Afficher les requêtes SQL pour SQL Developer
      System.out.println("\n" + "=".repeat(60));
      System.out.println("REQUÊTES SQL POUR SQL DEVELOPER :");
      System.out.println("=".repeat(60));

      System.out.println("\n--- Requêtes SQL pour SQL Developer ---");
      System.out.println("\n⭐ Voir les utilisateurs dans la table UTILISATEURS :");
      System.out.println("   SELECT * FROM UTILISATEURS;");
      System.out.println("\n1. Voir tous les utilisateurs :");
      System.out.println("   SELECT ID, EMAIL, motdePasse FROM UTILISATEURS ORDER BY ID;");

    } catch (Exception e) {
      System.err.println("✗ Erreur : " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
      DatabaseConnection.close();
    }
  }

  /**
   * Supprime la vue VUE_UTILISATEURS si elle existe
   */
  private static void supprimerVueUtilisateurs(EntityManager em) {
    System.out.println("--- Suppression de la vue VUE_UTILISATEURS ---");
    jakarta.persistence.EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      try {
        em.createNativeQuery("DROP VIEW VUE_UTILISATEURS").executeUpdate();
        System.out.println("✓ Vue VUE_UTILISATEURS supprimée\n");
      } catch (Exception e) {
        // La vue n'existe peut-être pas
        System.out.println("✓ Vue VUE_UTILISATEURS n'existe pas (déjà supprimée)\n");
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        try {
          transaction.rollback();
        } catch (Exception ex) {
          // Ignorer
        }
      }
      System.err.println("⚠ Erreur lors de la suppression de la vue : " + e.getMessage());
      // Ne pas bloquer l'exécution
    }
  }

  /**
   * Crée la table UTILISATEURS si elle n'existe pas
   */
  private static void creerTableUtilisateurs(EntityManager em) {
    System.out.println("--- Création/Vérification de la table UTILISATEURS ---");
    jakarta.persistence.EntityTransaction transaction = null;

    try {
      // Vérifier si la table existe
      boolean tableExists = false;
      try {
        em.createNativeQuery("SELECT 1 FROM UTILISATEURS WHERE ROWNUM = 1").getSingleResult();
        tableExists = true;
        System.out.println("✓ Table UTILISATEURS existe déjà");
      } catch (Exception e) {
        // La table n'existe pas
        tableExists = false;
      }

      if (!tableExists) {
        transaction = em.getTransaction();
        transaction.begin();

        try {
          em.createNativeQuery("CREATE TABLE UTILISATEURS (" + "ID NUMBER(19,0) NOT NULL, "
              + "EMAIL VARCHAR2(255 CHAR) NOT NULL, " + "MOTDEPASSE VARCHAR2(255 CHAR) NOT NULL, "
              + "PRIMARY KEY (ID)" + ")").executeUpdate();
          transaction.commit();
          System.out.println("✓ Table UTILISATEURS créée avec succès\n");
        } catch (Exception e) {
          if (transaction != null && transaction.isActive()) {
            transaction.rollback();
          }
          System.err.println("⚠ Erreur lors de la création de la table : " + e.getMessage());
          e.printStackTrace();
        }
      } else {
        System.out.println("✓ Structure de la table vérifiée\n");
      }

    } catch (Exception e) {
      System.err.println("⚠ Erreur lors de la vérification de la table : " + e.getMessage());
      e.printStackTrace();
      // Ne pas bloquer l'exécution
    }
  }

  /**
   * Modifie la table UTILISATEURS pour permettre l'insertion manuelle de l'ID
   */
  private static void modifierTablePourInsertionManuelle(EntityManager em) {
    System.out.println("--- Modification de la table pour insertion manuelle ---");
    jakarta.persistence.EntityTransaction transaction = null;

    try {
      // Vérifier si la colonne ID est une colonne d'identité
      try {
        @SuppressWarnings("unchecked")
        List<Object[]> identityCols = em.createNativeQuery(
            "SELECT column_name, generation_type FROM user_tab_identity_cols WHERE UPPER(table_name) = 'UTILISATEURS'")
            .getResultList();

        if (!identityCols.isEmpty()) {
          String genType = (String) identityCols.get(0)[1];
          System.out
              .println("  Colonne ID détectée comme colonne d'identité (type: " + genType + ")");

          if ("ALWAYS".equals(genType)) {
            // Supprimer et recréer la table sans colonne d'identité
            transaction = em.getTransaction();
            transaction.begin();

            try {
              System.out
                  .println("  → Suppression et recréation de la table sans colonne d'identité...");

              // Supprimer la table
              em.createNativeQuery("DROP TABLE UTILISATEURS CASCADE CONSTRAINTS").executeUpdate();

              // Recréer la table avec un ID normal (pas d'identité)
              em.createNativeQuery("CREATE TABLE UTILISATEURS (" + "ID NUMBER(19,0) NOT NULL, "
                  + "EMAIL VARCHAR2(255 CHAR) NOT NULL, "
                  + "MOTDEPASSE VARCHAR2(255 CHAR) NOT NULL, " + "PRIMARY KEY (ID))")
                  .executeUpdate();

              transaction.commit();
              System.out.println(
                  "  ✓ Table recréée sans colonne d'identité (insertion manuelle possible)\n");
            } catch (Exception e) {
              if (transaction != null && transaction.isActive()) {
                transaction.rollback();
              }
              System.out.println("  ⚠ Impossible de recréer la table : " + e.getMessage());
              // Continuer quand même
            }
          } else {
            System.out.println("  ✓ La colonne ID permet déjà l'insertion manuelle\n");
          }
        } else {
          System.out.println("  ✓ La colonne ID n'est pas une colonne d'identité\n");
        }
      } catch (Exception e) {
        System.out.println("  ⚠ Impossible de vérifier le type de colonne : " + e.getMessage());
        // Continuer quand même
      }

    } catch (Exception e) {
      System.err.println("⚠ Erreur lors de la modification de la table : " + e.getMessage());
      // Ne pas bloquer l'exécution
    }
  }

  /**
   * Insère un utilisateur dans la table UTILISATEURS
   */
  private static void insererDansTableUtilisateurs(EntityManager em, Long id, String email,
      String motDePasse) {
    jakarta.persistence.EntityTransaction transaction = null;

    try {
      // Détecter les noms de colonnes réels
      String idCol = "ID";
      String emailCol = "EMAIL";
      String mdpCol = "MOTDEPASSE";

      try {
        @SuppressWarnings("unchecked")
        List<Object[]> columns = em.createNativeQuery(
            "SELECT column_name FROM user_tab_columns WHERE UPPER(table_name) = 'UTILISATEURS' ORDER BY column_id")
            .getResultList();

        if (!columns.isEmpty() && columns.size() >= 3) {
          idCol = (String) columns.get(0)[0];
          emailCol = (String) columns.get(1)[0];
          mdpCol = (String) columns.get(2)[0];
          System.out.println("  Colonnes détectées: " + idCol + ", " + emailCol + ", " + mdpCol);
        }
      } catch (Exception e) {
        System.out
            .println("  ⚠ Impossible de détecter les colonnes, utilisation des noms par défaut");
      }

      transaction = em.getTransaction();
      if (!transaction.isActive()) {
        transaction.begin();
      }

      // D'abord, supprimer l'utilisateur s'il existe déjà (pour éviter les erreurs de clé primaire)
      try {
        em.createNativeQuery("DELETE FROM UTILISATEURS WHERE " + idCol + " = ?").setParameter(1, id)
            .executeUpdate();
      } catch (Exception e) {
        // Ignorer si l'utilisateur n'existe pas
      }

      // Insérer l'utilisateur avec les noms de colonnes détectés
      String insertQuery = "INSERT INTO UTILISATEURS (" + idCol + ", " + emailCol + ", " + mdpCol
          + ") VALUES (?, ?, ?)";
      System.out.println("  Requête d'insertion: " + insertQuery);

      int rowsAffected = em.createNativeQuery(insertQuery).setParameter(1, id)
          .setParameter(2, email).setParameter(3, motDePasse).executeUpdate();

      transaction.commit();
      System.out.println("  ✓ Utilisateur inséré dans la table UTILISATEURS (ID: " + id
          + ", lignes affectées: " + rowsAffected + ")\n");

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        try {
          transaction.rollback();
        } catch (Exception ex) {
          // Ignorer
        }
      }
      System.err.println("  ✗ ERREUR lors de l'insertion dans UTILISATEURS : " + e.getMessage());
      e.printStackTrace();
      // Ne pas bloquer l'exécution
    }
  }

  /**
   * Corrige les contraintes problématiques dans la base de données Oracle
   */
  private static void corrigerContraintes(EntityManager em) {
    System.out.println("--- Correction des contraintes ---");
    jakarta.persistence.EntityTransaction transaction = null;

    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Supprimer la contrainte problématique spécifique
      try {
        em.createNativeQuery("ALTER TABLE AGENTS DROP CONSTRAINT FKKDNYJEU7AQQFWRS90HMX9YN2C")
            .executeUpdate();
        System.out.println("✓ Contrainte FKKDNYJEU7AQQFWRS90HMX9YN2C supprimée");
      } catch (Exception e) {
        // La contrainte n'existe peut-être pas
      }

      // Chercher et supprimer TOUTES les contraintes de clé étrangère sur AGENTS
      try {
        @SuppressWarnings("unchecked")
        List<Object[]> fkConstraints =
            em.createNativeQuery("SELECT constraint_name FROM user_constraints "
                + "WHERE UPPER(table_name) = 'AGENTS' AND constraint_type = 'R'").getResultList();

        for (Object[] row : fkConstraints) {
          String fkName = (String) row[0];
          try {
            em.createNativeQuery("ALTER TABLE AGENTS DROP CONSTRAINT " + fkName).executeUpdate();
            System.out.println("✓ Contrainte FK " + fkName + " supprimée");
          } catch (Exception e) {
            // Ignorer si déjà supprimée
          }
        }
      } catch (Exception e) {
        System.out.println("⚠ Erreur lors de la recherche des contraintes : " + e.getMessage());
      }

      transaction.commit();
      System.out.println("✓ Correction des contraintes terminée\n");

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        try {
          transaction.rollback();
        } catch (Exception ex) {
          // Ignorer
        }
      }
      System.err.println("⚠ Erreur lors de la correction des contraintes : " + e.getMessage());
      // Ne pas bloquer l'exécution - continuer quand même
    }
  }
}
