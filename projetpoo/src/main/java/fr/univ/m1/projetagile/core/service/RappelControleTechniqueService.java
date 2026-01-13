package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.ControleTechnique;
import fr.univ.m1.projetagile.core.entity.RappelControleTechnique;
import fr.univ.m1.projetagile.core.persistence.RappelControleTechniqueRepository;

public class RappelControleTechniqueService {

  private final RappelControleTechniqueRepository repository;

  public RappelControleTechniqueService(RappelControleTechniqueRepository repository) {
    this.repository = repository;
  }

  /**
   * Crée un rappel de contrôle technique si la date d'expiration approche. Règle métier : - rappel
   * créé 30 jours avant expiration - un seul rappel par véhicule / agent
   */
  public void verifierEtCreerRappel(ControleTechnique controleTechnique, Agent agent) {
    LocalDate dateExpiration = controleTechnique.getDateExpiration();
    LocalDate dateRappel = dateExpiration.minusDays(30);

    if (LocalDate.now().isAfter(dateRappel)) {

      boolean rappelExiste = repository.existeRappel(controleTechnique.getVehicule(), agent);

      if (!rappelExiste) {
        RappelControleTechnique rappel =
            new RappelControleTechnique(controleTechnique.getVehicule(), agent, dateRappel);
        repository.save(rappel);
      }
    }
  }

  /**
   * Retourne tous les rappels non envoyés dont la date est atteinte
   */
  public List<RappelControleTechnique> getRappelsAEnvoyer() {
    return repository.findRappelsNonEnvoyes(LocalDate.now());
  }

  /**
   * Marque un rappel comme envoyé (après notification)
   */
  public void marquerRappelEnvoye(RappelControleTechnique rappel) {
    rappel.marquerCommeEnvoye();
    repository.update(rappel);
  }
}
