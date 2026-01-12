

package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.univ.m1.projetagile.core.dto.FactureOptionsMensuelleDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
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
   * Liste toutes les souscriptions d’options actives pour une location.
   */
  public List<SouscriptionOption> listerOptionsPourLocation(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("L'identifiant de la location est obligatoire");
    }
    // on ne retourne que les souscriptions non annulées
    return repository.findByLocation(locationId);
  }

  /**
   * Annule une souscription d’option (marque comme annulée).
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
        throw new IllegalArgumentException("Souscription d'option introuvable");
      }

      // logique métier dans l'entité (annulee = true, dateAnnulation = now)
      souscription.annulerOption();

      // sauvegarde de l’état annulé
      repository.save(souscription);

      tx.commit();

    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  // ===== FACTURATION MENSUELLE =====

  public List<FactureOptionsMensuelleDTO> genererFacturationMensuelle(int annee, int mois) {
    if (mois < 1 || mois > 12) {
      throw new IllegalArgumentException("Le mois doit être compris entre 1 et 12.");
    }

    // bornes du mois
    LocalDate debutMois = LocalDate.of(annee, mois, 1);
    LocalDate debutMoisSuivant = debutMois.plusMonths(1);

    LocalDateTime debut = debutMois.atStartOfDay();
    LocalDateTime fin = debutMoisSuivant.atStartOfDay();

    // Récupérer toutes les souscriptions (non annulées) dont la location commence dans ce mois
    List<SouscriptionOption> souscriptions = repository.findByLocationDateBetween(debut, fin);

    // Regrouper par loueur
    Map<Loueur, Double> totalParLoueur = new HashMap<>();

    for (SouscriptionOption s : souscriptions) {
      Location location = s.getLocation();
      Loueur loueur = location.getLoueur(); // suppose Location.getLoueur()

      if (loueur == null) {
        continue; // ou lever une exception, selon ta logique
      }

      Double prix = s.getOption().getPrix();
      if (prix == null) {
        prix = 0.0;
      }

      totalParLoueur.merge(loueur, prix, Double::sum);
    }

    // Transformer en DTO
    List<FactureOptionsMensuelleDTO> factures = new ArrayList<>();

    for (Map.Entry<Loueur, Double> entry : totalParLoueur.entrySet()) {
      Loueur loueur = entry.getKey();
      Double montant = entry.getValue();

      FactureOptionsMensuelleDTO dto = new FactureOptionsMensuelleDTO();
      dto.setAnnee(annee);
      dto.setMois(mois);
      dto.setLoueurId(loueur.getIdU()); // ou getId(), selon ton entité
      dto.setNomLoueur(loueur.getNom() + " " + loueur.getPrenom());
      dto.setMontantTotalOptions(montant);

      factures.add(dto);
    }

    return factures;
  }
}
