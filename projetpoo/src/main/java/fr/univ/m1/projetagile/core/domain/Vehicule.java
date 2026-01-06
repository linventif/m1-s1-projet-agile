package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicules")
public class Vehicule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type; // voiture, moto, camion

  @Column(nullable = false)
  private String marque; // Peugeot, Mercedes

  @Column(nullable = false)
  private String modele;

  private String couleur;

  private boolean disponible = true;

  @Column(nullable = false)
  private Double prixJ; // Prix journalier

  @ElementCollection
  @CollectionTable(name = "vehicule_periodes_dispo", joinColumns = @JoinColumn(name = "vehicule_id"))
  private List<PeriodeDisponibilite> datesDisponibilite = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "proprietaire_id")
  private Agent proprietaire;

  

  // JPA exige un constructeur sans arguments
  protected Vehicule() {
  }

  public Vehicule(String type, String marque, String modele, String couleur) {
    this.type = type;
    this.marque = marque;
    this.modele = modele;
    this.couleur = couleur;
  }

  // Getters (et setters si besoin)
  public Long getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getMarque() {
    return marque;
  }

  public String getModele() {
    return modele;
  }

  public String getCouleur() {
    return couleur;
  }

  public boolean isDisponible() {
    return disponible;
  }

  public void setDisponible(boolean disponible) {
    this.disponible = disponible;
  }

  public Agent getProprietaire() {
    return proprietaire;
  }

  public void setProprietaire(Agent proprietaire) {
    this.proprietaire = proprietaire;
  }

  public Double getPrixJ() {
    return prixJ;
  }

  public void setPrixJ(Double prixJ) {
    this.prixJ = prixJ;
  }

  public List<PeriodeDisponibilite> getDatesDisponibilite() {
    return datesDisponibilite;
  }

  public void setDatesDisponibilite(List<PeriodeDisponibilite> datesDisponibilite) {
    this.datesDisponibilite = datesDisponibilite;
  }

  public void ajouterPeriodeDisponibilite(LocalDate dateDebut, LocalDate dateFin) {
    this.datesDisponibilite.add(new PeriodeDisponibilite(dateDebut, dateFin));
  }

  // Classe embarquée pour représenter une période de disponibilité
  @Embeddable
  public static class PeriodeDisponibilite {
    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    protected PeriodeDisponibilite() {
    }

    public PeriodeDisponibilite(LocalDate dateDebut, LocalDate dateFin) {
      this.dateDebut = dateDebut;
      this.dateFin = dateFin;
    }

    public LocalDate getDateDebut() {
      return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
      this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
      return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
      this.dateFin = dateFin;
    }
  }
}
