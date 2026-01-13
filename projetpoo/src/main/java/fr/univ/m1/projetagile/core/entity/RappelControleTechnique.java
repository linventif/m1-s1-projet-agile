package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rappel_controle_technique")
public class RappelControleTechnique {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Vehicule vehicule;

  @ManyToOne(optional = false)
  private Agent agent;

  private LocalDate dateRappel;

  private boolean envoye;

  // =====================
  // Constructeurs
  // =====================

  public RappelControleTechnique() {}

  public RappelControleTechnique(Vehicule vehicule, Agent agent, LocalDate dateRappel) {
    this.vehicule = vehicule;
    this.agent = agent;
    this.dateRappel = dateRappel;
    this.envoye = false;
  }

  // =====================
  // Getters & Setters
  // =====================

  public Long getId() {
    return id;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  public Agent getAgent() {
    return agent;
  }

  public void setAgent(Agent agent) {
    this.agent = agent;
  }

  public LocalDate getDateRappel() {
    return dateRappel;
  }

  public void setDateRappel(LocalDate dateRappel) {
    this.dateRappel = dateRappel;
  }

  public boolean isEnvoye() {
    return envoye;
  }

  public void setEnvoye(boolean envoye) {
    this.envoye = envoye;
  }

  // =====================
  // Méthodes métier utiles
  // =====================

  public void marquerCommeEnvoye() {
    this.envoye = true;
  }

  @Override
  public String toString() {
    return "RappelControleTechnique{" + "id=" + id + ", vehicule=" + vehicule + ", agent=" + agent
        + ", dateRappel=" + dateRappel + ", envoye=" + envoye + '}';
  }
}
