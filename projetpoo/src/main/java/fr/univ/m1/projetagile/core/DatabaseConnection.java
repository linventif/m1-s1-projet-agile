package fr.univ.m1.projetagile.core;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Database connection manager with ThreadLocal EntityManager pattern - EntityManagerFactory:
 * singleton, partagé entre tous les threads - EntityManager: un par thread, automatiquement
 * réutilisé
 */
public class DatabaseConnection {

  private static EntityManagerFactory emf;
  private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

  /**
   * Initialize the EntityManagerFactory (thread-safe, singleton)
   */
  public static void init() {
    if (emf == null) {
      emf = Persistence.createEntityManagerFactory("default");
    }
  }

  /**
   * Get an EntityManager instance for the current thread Réutilise le même EntityManager dans un
   * thread si déjà ouvert
   */
  public static EntityManager getEntityManager() {
    if (emf == null) {
      init();
    }

    EntityManager em = threadLocal.get();

    // Si pas d'EM ou EM fermé, on en crée un nouveau
    if (em == null || !em.isOpen()) {
      em = emf.createEntityManager();
      threadLocal.set(em);
    }

    return em;
  }

  /**
   * Crée un nouvel EntityManager (pour les cas où on veut explicitement un nouveau)
   */
  public static EntityManager createEntityManager() {
    if (emf == null) {
      init();
    }
    return emf.createEntityManager();
  }

  /**
   * Ferme l'EntityManager du thread courant
   */
  public static void closeEntityManager() {
    EntityManager em = threadLocal.get();
    if (em != null && em.isOpen()) {
      em.close();
      threadLocal.remove();
    }
  }

  /**
   * Close the EntityManagerFactory (à faire à la fin de l'application)
   */
  public static void close() {
    closeEntityManager(); // Ferme l'EM du thread courant
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }
}
