package fr.univ.m1.projetagile.options.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import fr.univ.m1.projetagile.options.entity.Options;
import fr.univ.m1.projetagile.options.entity.SouscriptionOption;
import fr.univ.m1.projetagile.options.persistence.SouscriptionOptionRepository;

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

    if (aOption(utilisateur, option)) {
      throw new IllegalArgumentException("Souscription déjà existante pour cette option");
    }

    return repository.saveTransactional(
        new SouscriptionOption(utilisateur, option, periodicite, renouvellement));
  }

  /**
   * Liste toutes les souscriptions d'options actives pour un utilisateur.
   */
  public List<SouscriptionOption> listerOptionsUtilisateur(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'identifiant utilisateur est obligatoire");
    }
    return repository.findByUtilisateur(utilisateurId);
  }

  /**
   * Annule une souscription d'option.
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

  /**
   * Recherche une option par son nom.
   */
  public Options findOptionByNom(String nomOption) {
    if (nomOption == null || nomOption.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'option est obligatoire");
    }
    return repository.findOptionByNom(nomOption);
  }

  /**
   * Crée ou met à jour une option.
   */
  public Options saveOption(Options option) {
    if (option == null) {
      throw new IllegalArgumentException("L'option est obligatoire");
    }
    if (option.getNomOption() == null || option.getNomOption().trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de l'option est obligatoire");
    }
    if (option.getPrix() == null || option.getPrix() < 0) {
      throw new IllegalArgumentException("Le prix de l'option doit être positif");
    }
    return repository.saveOption(option);
  }

  /**
   * Vérifie si un utilisateur a souscrit à une option spécifique.
   *
   * @param utilisateur l'utilisateur à vérifier
   * @param option l'option à vérifier
   * @return true si l'utilisateur a souscrit à cette option, false sinon
   */
  public boolean aOption(Utilisateur utilisateur, Options option) {
    if (utilisateur == null || option == null) {
      return false;
    }
    if (utilisateur.getIdU() == null) {
      return false;
    }
    return repository.findByUtilisateur(utilisateur.getIdU()).stream()
        .anyMatch(so -> option.equals(so.getOption()));
  }

  /**
   * Récupère toutes les options actives d'un utilisateur.
   *
   * @param utilisateur l'utilisateur dont on veut récupérer les options
   * @return la liste des souscriptions d'options actives
   */
  public List<SouscriptionOption> getOptionsActives(Utilisateur utilisateur) {
    if (utilisateur == null || utilisateur.getIdU() == null) {
      throw new IllegalArgumentException("Utilisateur invalide");
    }
    return repository.findByUtilisateur(utilisateur.getIdU());
  }
}
