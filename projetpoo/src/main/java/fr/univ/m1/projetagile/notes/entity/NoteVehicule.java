package fr.univ.m1.projetagile.notes.entity;

import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Note attribuée par un loueur à un véhicule.
 *
 * <p>
 * Permet aux loueurs d'évaluer la qualité et l'état des véhicules loués.
 * </p>
 */
@Entity
@Table(name = "notes_vehicules")
public class NoteVehicule extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "vehicule_id", nullable = false)
  private Long vehiculeId;

  @Column(name = "loueur_id", nullable = false)
  private Long loueurId;

  protected NoteVehicule() {}

  public NoteVehicule(Vehicule vehicule, Loueur loueur, Double note1, Double note2, Double note3) {
    super(note1, note2, note3);
    if (vehicule == null || vehicule.getId() == null) {
      throw new IllegalArgumentException("Vehicule ne peut pas être null ou sans ID");
    }
    if (loueur == null || loueur.getIdU() == null) {
      throw new IllegalArgumentException("Loueur ne peut pas être null ou sans ID");
    }
    this.vehiculeId = vehicule.getId();
    this.loueurId = loueur.getIdU();
  }

  public NoteVehicule(Long vehiculeId, Long loueurId) {
    super();
    if (vehiculeId == null) {
      throw new IllegalArgumentException("Vehicule ID ne peut pas être null");
    }
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    this.vehiculeId = vehiculeId;
    this.loueurId = loueurId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVehiculeId() {
    return vehiculeId;
  }

  public void setVehiculeId(Long vehiculeId) {
    this.vehiculeId = vehiculeId;
  }

  public Long getLoueurId() {
    return loueurId;
  }

  public void setLoueurId(Long loueurId) {
    this.loueurId = loueurId;
  }

  @Override
  public String toString() {
    return "NoteVehicule [id=" + id + ", vehicule=" + vehiculeId + ", loueur=" + loueurId
        + ", moyenne=" + getNoteMoyenne() + ", date=" + date + "]";
  }
}
