package fr.univ.m1.projetagile.core.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.core.persistence.SouscriptionOptionRepository;

public class SouscriptionOptionService {

  private final SouscriptionOptionRepository repository;

  public SouscriptionOptionService(SouscriptionOptionRepository repository) {
    this.repository = repository;
  }

  public SouscriptionOptionService() {
    this(new SouscriptionOptionRepository());
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

    return repository.saveTransactional(
        new SouscriptionOption(utilisateur, option, periodicite, renouvellement));
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

    repository.runInTransaction(() -> {
      SouscriptionOption souscription = repository.findById(souscriptionId);
      if (souscription == null) {
        throw new IllegalArgumentException("Souscription introuvable");
      }

      souscription.annulerOption();
      repository.save(souscription);
    });
  }
}
