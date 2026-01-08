package fr.univ.m1.projetagile.core.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import fr.univ.m1.projetagile.core.persistence.SouscriptionOptionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SouscriptionOptionService {

  private final EntityManager em;
  private final SouscriptionOptionRepository repository;

  public SouscriptionOptionService(EntityManager em) {
    this.em = em;
    this.repository = new SouscriptionOptionRepository(em);
  }

  /**
   * Permet à un agent de souscrire une option payante à une location.
   */
  public SouscriptionOption souscrireOption(Long optionId, Long locationId) {

    if (optionId == null || locationId == null) {
      throw new IllegalArgumentException("Option et location obligatoires");
    }

    Options option = repository.findOptionById(optionId);
    Location location = repository.findLocationById(locationId);

    if (option == null || location == null) {
      throw new IllegalArgumentException("Option ou location introuvable");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      SouscriptionOption souscription = new SouscriptionOption(option, location);
      repository.save(souscription);

      tx.commit();
      return souscription;

    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /**
   * Liste toutes les souscriptions d’options pour une location.
   */
  public List<SouscriptionOption> listerOptionsPourLocation(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location est obligatoire");
    }
    return repository.findByLocation(locationId);
  }

  /**
   * Annule (supprime) une souscription d’option.
   */
  public void annulerSouscription(Long souscriptionId) {
    if (souscriptionId == null) {
      throw new IllegalArgumentException("L'identifiant de la souscription est obligatoire");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      SouscriptionOption souscription = em.find(SouscriptionOption.class, souscriptionId);
      if (souscription == null) {
        throw new IllegalArgumentException("Souscription d'option introuvable");
      }

      repository.delete(souscription);

      tx.commit();

    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }
}
