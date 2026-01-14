package fr.univ.m1.projetagile.core.service;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Ville;


public class VilleService {
  private final List<Ville> toutesLesVilles;

  public VilleService(List<Ville> toutesLesVilles) {
    this.toutesLesVilles = toutesLesVilles;
  }

  // =======================
  // Haversine: calculate distance between two cities in km
  // =======================
  private double distanceKm(Ville v1, Ville v2) {
    final int R = 6371;
    double latDistance = Math.toRadians(v2.getLatitude() - v1.getLatitude());
    double lonDistance = Math.toRadians(v2.getLongitude() - v1.getLongitude());
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(v1.getLatitude())) * Math.cos(Math.toRadians(v2.getLatitude()))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  // =======================
  // retourner la liste des villes dans un rayon de X km autour d'une ville donnée
  // =======================
  public List<Ville> getVillesAutour(String villeDepartNom, double xKm) {
    if (villeDepartNom == null || villeDepartNom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de la ville de départ ne peut pas être nul ou vide");
    }
    if (xKm <= 0) {
      throw new IllegalArgumentException("Le rayon en kilomètres doit être strictement positif: " + xKm);
    }
    Ville villeDepart = null;

    // trouver l'objet ville de départ
    for (Ville v : toutesLesVilles) {
      if (v.getNom().equalsIgnoreCase(villeDepartNom)) {
        villeDepart = v;
        break;
      }
    }

    if (villeDepart == null) {
      throw new IllegalArgumentException("Ville de départ inconnue: " + villeDepartNom);
    }

    List<Ville> resultat = new ArrayList<>();
    for (Ville v : toutesLesVilles) {
      if (!v.equals(villeDepart) && distanceKm(villeDepart, v) <= xKm) {
        resultat.add(v);
      }
    }

    return resultat;
  }
}
