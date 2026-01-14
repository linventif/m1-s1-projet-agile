package fr.univ.m1.projetagile.options.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.entity.SouscriptionOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SouscriptionOptionRepository {

  private static final ThreadLocal<EntityManager> CONTEXT_EM = new ThreadLocal<>();

  public SouscriptionOptionRepository() {}

  /**
   * Sauvegarde une souscription d'option (création ou mise à jour).
   */
  public SouscriptionOption save(SouscriptionOption souscription) {
    EntityManager em = acquireEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean ownsTx = !tx.isActive();
    boolean ownsEm = ownsEntityManager();

    try {
      if (ownsTx) {
        tx.begin();
      }

      if (souscription.getId() == null) {
        em.persist(souscription);
      } else {
        souscription = em.merge(souscription);
      }

      if (ownsTx) {
        tx.commit();
      }
      return souscription;
    } catch (RuntimeException e) {
      if (ownsTx && tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Recherche une souscription par son identifiant.
   */
  public SouscriptionOption findById(Long id) {
    EntityManager em = acquireEntityManager();
    boolean ownsEm = ownsEntityManager();
    try {
      return em.find(SouscriptionOption.class, id);
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Recherche une option par son identifiant.
   */
  public Options findOptionById(Long optionId) {
    EntityManager em = acquireEntityManager();
    boolean ownsEm = ownsEntityManager();
    try {
      return em.find(Options.class, optionId);
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Recherche une option par son nom.
   */
  public Options findOptionByNom(String nomOption) {
    EntityManager em = acquireEntityManager();
    boolean ownsEm = ownsEntityManager();
    try {
      List<Options> options =
          em.createQuery("SELECT o FROM Options o WHERE o.nomOption = :nom", Options.class)
              .setParameter("nom", nomOption).getResultList();
      return options.isEmpty() ? null : options.get(0);
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Sauvegarde une option (création ou mise à jour).
   */
  public Options saveOption(Options option) {
    EntityManager em = acquireEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean ownsTx = !tx.isActive();
    boolean ownsEm = ownsEntityManager();

    try {
      if (ownsTx) {
        tx.begin();
      }

      if (option.getId() == null) {
        em.persist(option);
      } else {
        option = em.merge(option);
      }

      if (ownsTx) {
        tx.commit();
      }
      return option;
    } catch (RuntimeException e) {
      if (ownsTx && tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Recherche un utilisateur par son identifiant. Cherche parmi tous les types concrets
   * d'utilisateurs (Agent, Loueur, Entretien).
   *
   * @param utilisateurId l'ID de l'utilisateur
   * @return l'utilisateur trouvé, ou null si non trouvé
   */
  public fr.univ.m1.projetagile.core.entity.Utilisateur findUtilisateurById(Long utilisateurId) {
    EntityManager em = acquireEntityManager();
    boolean ownsEm = ownsEntityManager();

    try {
      // Essayer de trouver parmi les Agents (inclut AgentParticulier et AgentProfessionnel)
      fr.univ.m1.projetagile.core.entity.Utilisateur user = em
          .createQuery("SELECT a FROM Agent a WHERE a.idU = :id",
              fr.univ.m1.projetagile.core.entity.Agent.class)
          .setParameter("id", utilisateurId).getResultList().stream().findFirst().orElse(null);
      if (user != null)
        return user;

      // Essayer de trouver parmi les Loueurs
      user = em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, utilisateurId);
      if (user != null)
        return user;

      // Essayer de trouver parmi les Entretiens
      return em.find(fr.univ.m1.projetagile.entretienVehicule.entity.Entretien.class,
          utilisateurId);
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Liste toutes les souscriptions d'un utilisateur.
   */
  public List<SouscriptionOption> findByUtilisateur(Long utilisateurId) {
    EntityManager em = acquireEntityManager();
    boolean ownsEm = ownsEntityManager();
    try {
      return em.createQuery("SELECT s FROM SouscriptionOption s " + "WHERE s.utilisateurId = :id",
          SouscriptionOption.class).setParameter("id", utilisateurId).getResultList();
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Supprime physiquement une souscription d'option.
   */
  public void delete(SouscriptionOption souscription) {
    EntityManager em = acquireEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean ownsTx = !tx.isActive();
    boolean ownsEm = ownsEntityManager();

    try {
      if (ownsTx) {
        tx.begin();
      }

      SouscriptionOption managed = souscription;
      if (!em.contains(souscription)) {
        managed = em.merge(souscription);
      }
      em.remove(managed);

      if (ownsTx) {
        tx.commit();
      }
    } catch (RuntimeException e) {
      if (ownsTx && tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      if (ownsEm) {
        em.close();
      }
    }
  }

  /**
   * Exécute une sauvegarde avec gestion de transaction.
   */
  public SouscriptionOption saveTransactional(SouscriptionOption souscription) {
    return save(souscription);
  }

  /**
   * Exécute une action en la entourant d'une transaction.
   */
  public void runInTransaction(Runnable action) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction tx = em.getTransaction();

    CONTEXT_EM.set(em);
    try {
      tx.begin();
      action.run();
      tx.commit();
    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      CONTEXT_EM.remove();
      em.close();
    }
  }

  private EntityManager acquireEntityManager() {
    EntityManager contextual = CONTEXT_EM.get();
    if (contextual != null) {
      return contextual;
    }
    return DatabaseConnection.getEntityManager();
  }

  private boolean ownsEntityManager() {
    return CONTEXT_EM.get() == null;
  }
}
