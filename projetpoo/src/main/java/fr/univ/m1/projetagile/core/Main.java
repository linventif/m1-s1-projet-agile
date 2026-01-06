package fr.univ.m1.projetagile.core;

import jakarta.persistence.EntityManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManager em = null;

        try {
            // Initialize database connection
            DatabaseConnection.init();
            em = DatabaseConnection.getEntityManager();

            System.out.println("✓ DB connectée\n");

            // Get all tables from Oracle database
            System.out.println("=== Liste des tables ===");
            String sql = "SELECT table_name FROM user_tables ORDER BY table_name";

            @SuppressWarnings("unchecked")
            List<String> tables = em.createNativeQuery(sql).getResultList();

            if (tables.isEmpty()) {
                System.out.println("  (aucune table trouvée)");
            } else {
                for (String tableName : tables) {
                    System.out.println("  - " + tableName);
                }
            }

            System.out.println("\nNombre total de tables: " + tables.size());

        } catch (Exception e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            DatabaseConnection.close();
        }
    }
}