package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.Options;
import fr.univ.m1.projetagile.core.entity.SouscriptionOption;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.OptionRepository;

public class OptionService {

  private final OptionRepository repo = new OptionRepository();

  /**
   * Souscrire une option payante pour un véhicule
   */
  public void souscrireOption(Long vehiculeId, Long optionId) {

    Vehicule vehicule = repo.findVehiculeById(vehiculeId);
    Options option = repo.findOptionById(optionId);

    if (vehicule == null || option == null)
      throw new IllegalArgumentException("Vehicule ou option introuvable");

    SouscriptionOption so = new SouscriptionOption(vehicule, option);
    repo.saveSouscription(so);
  }

  /**
   * Annuler une option
   */
  public void annulerOption(SouscriptionOption so) {
    repo.deleteSouscription(so);
  }
}
