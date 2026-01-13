package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "souscription_options")
public class SouscriptionOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime dateDebut;

  @Column(nullable = false)
  private boolean renouvellement;

  @Column(nullable = false)
  private int periodicite; // en mois

  // 🔗 Lien avec l'utilisateur (stocke l'ID au lieu de la référence directe)
  @Column(name = "utilisateur_id", nullable = false)
  private Long utilisateurId;

  // 🔗 Lien avec l'option
  @ManyToOne
  @JoinColumn(name = "option_id", nullable = false)
  private Options option;

  // Constructeur JPA
  protected SouscriptionOption() {}

  public SouscriptionOption(Utilisateur utilisateur, Options option, int periodicite,
      boolean renouvellement) {
    this.utilisateurId = utilisateur.getIdU();
    this.option = option;
    this.periodicite = periodicite;
    this.renouvellement = renouvellement;
    this.dateDebut = LocalDateTime.now();
  }

  // =======================
  // Méthodes métier (UML)
  // =======================

  public void annulerOption() {
    this.renouvellement = false;
  }

  // =======================
  // Getters
  // =======================

  public Long getId() {
    return id;
  }

  public LocalDateTime getDateDebut() {
    return dateDebut;
  }

  public boolean isRenouvellement() {
    return renouvellement;
  }

  public int getPeriodicite() {
    return periodicite;
  }

  public Long getUtilisateurId() {
    return utilisateurId;
  }

  public Options getOption() {
    return option;
  }
}
