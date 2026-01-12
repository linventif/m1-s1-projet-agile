package fr.univ.m1.projetagile.core.interfaces;

/**
 * Interface représentant un lieu de restitution de véhicule
 */
public interface LieuRestitution {

  /**
   * Retourne l'adresse du lieu de restitution
   *
   * @return l'adresse du lieu de restitution
   */
  String getAdresse();

  /**
   * Retourne le coût supplémentaire associé à ce lieu de restitution
   *
   * @return le coût supplémentaire
   */
  Double getCoutSupp();
}
