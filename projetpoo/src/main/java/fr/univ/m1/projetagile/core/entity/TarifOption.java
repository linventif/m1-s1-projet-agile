package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a tariff for an option with specific periodicity and price.
 * Allows defining different pricing schemes for the same option.
 */
@Entity
@Table(name = "tarif_options")
public class TarifOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int periodicite; // en mois (1 = mensuel, 12 = annuel, etc.)

  @Column(nullable = false)
  private double prix;

  /**
   * Multiple tariffs can exist for the same option
   */
  @ManyToOne
  @JoinColumn(name = "option_id", nullable = false)
  private Options option;

  // Constructeur JPA
  protected TarifOption() {}

  public TarifOption(Options option, int periodicite, double prix) {
    this.option = option;
    this.periodicite = periodicite;
    this.prix = prix;
  }

  // =====================
  // Getters
  // =====================

  public Long getId() {
    return id;
  }

  public int getPeriodicite() {
    return periodicite;
  }

  public double getPrix() {
    return prix;
  }

  public Options getOption() {
    return option;
  }
}
