package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "grilles_tarifs")
public class GrilleTarif {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "grilleTarif", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TarifVehicule> tarifVehi = new ArrayList<>();

  @OneToMany(mappedBy = "grilleTarif", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TarifOption> tarifOptions = new ArrayList<>();

  @OneToMany(mappedBy = "grille", cascade = CascadeType.ALL)
  private List<Assurance> assurances = new ArrayList<>();

  // Constructeur sans argument pour JPA
  protected GrilleTarif() {}

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public List<TarifVehicule> getTarifVehi() {
    return Collections.unmodifiableList(tarifVehi);
  }

  public void ajouterTarifVehicule(TarifVehicule tarif) {
    if (tarif != null) {
      tarifVehi.add(tarif);
      tarif.setGrilleTarif(this);
    }
  }

  public List<TarifOption> getTarifOptions() {
    return Collections.unmodifiableList(tarifOptions);
  }

  public void ajouterTarifOption(TarifOption tarif) {
    if (tarif != null) {
      tarifOptions.add(tarif);
      tarif.setGrilleTarif(this);
    }
  }

  public List<Assurance> getAssurances() {
    return Collections.unmodifiableList(assurances);
  }
}

