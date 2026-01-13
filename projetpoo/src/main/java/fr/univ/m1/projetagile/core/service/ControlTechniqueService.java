package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import fr.univ.m1.projetagile.core.entity.ControleTechnique;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.ControleTechniqueRepository;

public class ControlTechniqueService {

  private ControleTechniqueRepository repository;

  public ControlTechniqueService(ControleTechniqueRepository repository) {
    this.repository = repository;
  }

  public ControleTechnique enregistrerControle(Vehicule vehicule, LocalDate dateControle,
      LocalDate dateExpiration, boolean valide) {
    ControleTechnique ct = new ControleTechnique(vehicule, dateControle, dateExpiration, valide);
    repository.save(ct);
    return ct;
  }
}
