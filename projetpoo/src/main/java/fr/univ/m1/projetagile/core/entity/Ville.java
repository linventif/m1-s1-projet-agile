package fr.univ.m1.projetagile.core.entity;

public class Ville {


  private String nom;
  private double latitude;
  private double longitude;

  public Ville(String nom, double latitude, double longitude) {
    this.nom = nom;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getNom() {
    return nom;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }
}
