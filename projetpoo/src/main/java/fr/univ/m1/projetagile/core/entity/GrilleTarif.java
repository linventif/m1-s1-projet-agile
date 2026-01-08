package fr.univ.m1.projetagile.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.univ.m1.projetagile.enums.TypeV;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "grilles_tarifs")
public class GrilleTarif {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "grilleTarif", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<TarifVehicule> tarifVehi = new ArrayList<>();

  @OneToMany(mappedBy = "grilleTarif", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<TarifOption> tarifOptions = new ArrayList<>();

  @OneToMany(mappedBy = "grille", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Assurance> assurances = new ArrayList<>();

  // ✅ IMPORTANT : public pour pouvoir faire new GrilleTarif() depuis core.service
  public GrilleTarif() {}

  public Long getId() {
    return id;
  }

  // =========================
  // Tarifs véhicules
  // =========================

  public List<TarifVehicule> getTarifVehi() {
    return Collections.unmodifiableList(tarifVehi);
  }

  public void ajouterTarifVehicule(TarifVehicule tarif) {
    if (tarif == null)
      return;

    if (!tarifVehi.contains(tarif)) {
      tarifVehi.add(tarif);
    }

    // garde la relation bidirectionnelle cohérente
    if (tarif.getGrilleTarif() != this) {
      tarif.setGrilleTarif(this);
    }
  }

  /**
   * IMPORTANT (nullable=false) : - on retire de la liste - on NE met PAS grilleTarif à null
   * orphanRemoval=true supprimera l'enfant si nécessaire.
   */
  public void retirerTarifVehicule(TarifVehicule tarif) {
    if (tarif == null)
      return;
    tarifVehi.remove(tarif);
  }

  public TarifVehicule trouverTarifVehicule(TypeV type, String modele) {
    if (type == null || modele == null)
      return null;

    String modeleClean = modele.trim();
    for (TarifVehicule tv : tarifVehi) {
      if (tv.getTypeVehi() == type && modeleClean.equalsIgnoreCase(tv.getModeleVehi().trim())) {
        return tv;
      }
    }
    return null;
  }

  // =========================
  // Tarifs options
  // =========================

  public List<TarifOption> getTarifOptions() {
    return Collections.unmodifiableList(tarifOptions);
  }

  public void ajouterTarifOption(TarifOption tarif) {
    if (tarif == null)
      return;

    if (!tarifOptions.contains(tarif)) {
      tarifOptions.add(tarif);
    }

    if (tarif.getGrilleTarif() != this) {
      tarif.setGrilleTarif(this);
    }
  }

  /**
   * IMPORTANT (nullable=false) : - on retire de la liste - on NE met PAS grilleTarif à null
   */
  public void retirerTarifOption(TarifOption tarif) {
    if (tarif == null)
      return;
    tarifOptions.remove(tarif);
  }

  public TarifOption trouverTarifOption(String nomOption) {
    if (nomOption == null)
      return null;

    String nomClean = nomOption.trim();
    for (TarifOption to : tarifOptions) {
      if (nomClean.equalsIgnoreCase(to.getNomOption().trim())) {
        return to;
      }
    }
    return null;
  }

  // =========================
  // Assurances liées à la grille
  // =========================

  public List<Assurance> getAssurances() {
    return Collections.unmodifiableList(assurances);
  }

  public void ajouterAssurance(Assurance assurance) {
    if (assurance == null)
      return;

    if (!assurances.contains(assurance)) {
      assurances.add(assurance);
    }

    if (assurance.getGrille() != this) {
      assurance.setGrille(this);
    }
  }

  /**
   * IMPORTANT (nullable=false dans Assurance.grille) : - on retire de la liste - on NE met PAS
   * grille à null
   */
  public void retirerAssurance(Assurance assurance) {
    if (assurance == null)
      return;
    assurances.remove(assurance);
  }
}
