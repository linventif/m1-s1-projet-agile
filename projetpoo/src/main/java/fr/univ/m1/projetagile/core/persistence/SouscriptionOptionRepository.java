package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDateTime;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
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
   * Recherche une location par son identifiant.
   */
  public Location findLocationById(Long locationId) {
    return em.find(Location.class, locationId);
  }

  /**
   * Liste toutes les souscriptions NON ANNULEES associées à une location.
   */
  public List<SouscriptionOption> findByLocation(Long locationId) {
    return em
        .createQuery("SELECT s FROM SouscriptionOption s "
            + "WHERE s.location.id = :id AND s.annulee = false", SouscriptionOption.class)
        .setParameter("id", locationId).getResultList();
  }

  /**
   * Supprime physiquement une souscription d’option (optionnel, si tu en as besoin).
   */
  public void delete(SouscriptionOption souscription) {
    SouscriptionOption managed = souscription;
    if (!em.contains(souscription)) {
      managed = em.merge(souscription);
    }
    em.remove(managed);
  }

  /**
   * Récupère toutes les souscriptions NON ANNULEES dont la location commence dans une période.
   */
  public List<SouscriptionOption> findByLocationDateBetween(LocalDateTime debutInclus,
      LocalDateTime finExclu) {

    return em
        .createQuery(
            "SELECT s FROM SouscriptionOption s " + "WHERE s.location.dateDebut >= :debut "
                + "AND s.location.dateDebut < :fin " + "AND s.annulee = false",
            SouscriptionOption.class)
        .setParameter("debut", debutInclus).setParameter("fin", finExclu).getResultList();
  }
}
