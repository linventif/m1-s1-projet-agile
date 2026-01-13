package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SouscriptionOptionRepository {

  private final EntityManager em;

  public SouscriptionOptionRepository(EntityManager em) {
    this.em = em;
  }

  /**
   * Sauvegarde une souscription d'option (création ou mise à jour).
   */
  public SouscriptionOption save(SouscriptionOption souscription) {
    if (souscription.getId() == null) {
      em.persist(souscription);
      return souscription;
    } else {
      return em.merge(souscription);
    }
  }

  /**
   * Recherche une souscription par son identifiant.
   */
  public SouscriptionOption findById(Long id) {
    return em.find(SouscriptionOption.class, id);
  }

  /**
   * Recherche une option par son identifiant.
   */
  public Options findOptionById(Long optionId) {
    return em.find(Options.class, optionId);
  }

  /**
   * Recherche un utilisateur par son identifiant.
   * Cherche parmi tous les types concrets d'utilisateurs (Agent, Loueur, Entretien).
   * 
   * @param utilisateurId l'ID de l'utilisateur
   * @return l'utilisateur trouvé, ou null si non trouvé
   */
  public fr.univ.m1.projetagile.core.entity.Utilisateur findUtilisateurById(Long utilisateurId) {
    // Essayer de trouver parmi les Agents (inclut AgentParticulier et AgentProfessionnel)
    try {
      fr.univ.m1.projetagile.core.entity.Utilisateur user = em.createQuery(
          "SELECT a FROM Agent a WHERE a.idU = :id", 
          fr.univ.m1.projetagile.core.entity.Agent.class)
          .setParameter("id", utilisateurId)
          .getResultList()
          .stream()
          .findFirst()
          .orElse(null);
      if (user != null) return user;
    } catch (Exception ignored) {}
    
    // Essayer de trouver parmi les Loueurs
    try {
      fr.univ.m1.projetagile.core.entity.Utilisateur user = 
          em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, utilisateurId);
      if (user != null) return user;
    } catch (Exception ignored) {}
    
    // Essayer de trouver parmi les Entretiens
    try {
      fr.univ.m1.projetagile.core.entity.Utilisateur user = 
          em.find(fr.univ.m1.projetagile.core.entity.Entretien.class, utilisateurId);
      if (user != null) return user;
    } catch (Exception ignored) {}
    
    return null;
  }

  /**
   * Liste toutes les souscriptions d'un utilisateur.
   */
  public List<SouscriptionOption> findByUtilisateur(Long utilisateurId) {
    return em
        .createQuery("SELECT s FROM SouscriptionOption s "
            + "WHERE s.utilisateurId = :id", SouscriptionOption.class)
        .setParameter("id", utilisateurId).getResultList();
  }

  /**
   * Supprime physiquement une souscription d'option.
   */
  public void delete(SouscriptionOption souscription) {
    SouscriptionOption managed = souscription;
    if (!em.contains(souscription)) {
      managed = em.merge(souscription);
    }
    em.remove(managed);
  }

  /**
   * Exécute une sauvegarde avec gestion de transaction.
   */
  public SouscriptionOption saveTransactional(SouscriptionOption souscription) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      SouscriptionOption managed = save(souscription);
      tx.commit();
      return managed;
    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /**
   * Exécute une action en la entourant d'une transaction.
   */
  public void runInTransaction(Runnable action) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      action.run();
      tx.commit();
    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }
}
