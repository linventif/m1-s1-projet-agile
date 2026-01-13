package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.EntityManager;

public class SouscriptionOptionRepository {

  private final EntityManager em;

  public SouscriptionOptionRepository(EntityManager em) {
    this.em = em;
  }

  /**
   * Sauvegarde une souscription d’option (création ou mise à jour).
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
   */
  public Utilisateur findUtilisateurById(Long utilisateurId) {
    return em.find(Utilisateur.class, utilisateurId);
  }

  /**
   * Liste toutes les souscriptions ACTIVES d’un utilisateur.
   */
  public List<SouscriptionOption> findByUtilisateur(Long utilisateurId) {
    return em
        .createQuery("SELECT s FROM SouscriptionOption s "
            + "WHERE s.utilisateur.idU = :id AND s.annulee = false", SouscriptionOption.class)
        .setParameter("id", utilisateurId).getResultList();
  }

  /**
   * Supprime physiquement une souscription d’option.
   */
  public void delete(SouscriptionOption souscription) {
    SouscriptionOption managed = souscription;
    if (!em.contains(souscription)) {
      managed = em.merge(souscription);
    }
    em.remove(managed);
  }
}
