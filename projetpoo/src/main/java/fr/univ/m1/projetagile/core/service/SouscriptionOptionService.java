package fr.univ.m1.projetagile.core.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
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
   * Permet à un utilisateur de souscrire une option payante.
   */
  public SouscriptionOption souscrireOption(Long utilisateurId, Long optionId, int periodicite,
      boolean renouvellement) {

    if (utilisateurId == null || optionId == null) {
      throw new IllegalArgumentException("Utilisateur et option obligatoires");
    }

    Utilisateur utilisateur = repository.findUtilisateurById(utilisateurId);
    Options option = repository.findOptionById(optionId);

    if (utilisateur == null || option == null) {
      throw new IllegalArgumentException("Utilisateur ou option introuvable");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      SouscriptionOption souscription =
          new SouscriptionOption(utilisateur, option, periodicite, renouvellement);

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
   * Liste toutes les souscriptions d’options actives pour un utilisateur.
   */
  public List<SouscriptionOption> listerOptionsUtilisateur(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'identifiant utilisateur est obligatoire");
    }
    return repository.findByUtilisateur(utilisateurId);
  }

  /**
   * Annule une souscription d’option.
   */
  public void annulerSouscription(Long souscriptionId) {
    if (souscriptionId == null) {
      throw new IllegalArgumentException("L'identifiant de la souscription est obligatoire");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      SouscriptionOption souscription = repository.findById(souscriptionId);
      if (souscription == null) {
        throw new IllegalArgumentException("Souscription introuvable");
      }

      souscription.annulerOption();

      repository.save(souscription);

      tx.commit();

    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }
}
