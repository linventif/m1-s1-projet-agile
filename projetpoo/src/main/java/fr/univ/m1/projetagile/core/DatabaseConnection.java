package fr.univ.m1.projetagile.core;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Database connection manager
 */
public class DatabaseConnection {

  private static EntityManagerFactory emf;

  /**
   * Initialize the EntityManagerFactory
   */
  public static void init() {
    if (emf == null) {
      emf = Persistence.createEntityManagerFactory("default");
    }
  }

  /**
   * Get an EntityManager instance
   */
  public static EntityManager getEntityManager() {
    if (emf == null) {
      init();
    }
    return emf.createEntityManager();
  }

  /**
   * Close the EntityManagerFactory
   */
  public static void close() {
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }
}
