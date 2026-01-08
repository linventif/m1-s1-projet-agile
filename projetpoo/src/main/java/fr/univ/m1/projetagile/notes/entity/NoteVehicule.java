package fr.univ.m1.projetagile.notes.entity;

import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  protected NoteVehicule() {}

  public NoteVehicule(Vehicule vehicule, Loueur loueur, Double note1, Double note2, Double note3) {
    super(note1, note2, note3);
    if (vehicule == null) {
      throw new IllegalArgumentException("Vehicule ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Loueur ne peut pas être null");
    }
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Vehicule getVehicule() {
    return vehicule;
  }

  public void setVehicule(Vehicule vehicule) {
    this.vehicule = vehicule;
  }

  public Loueur getLoueur() {
    return loueur;
  }

  public void setLoueur(Loueur loueur) {
    this.loueur = loueur;
  }

  @Override
  public String toString() {
    return "NoteVehicule [id=" + id + ", vehicule=" + (vehicule != null ? vehicule.getId() : "null")
        + ", loueur=" + (loueur != null ? loueur.getIdU() : "null") + ", moyenne="
        + getNoteMoyenne() + ", date=" + date + "]";
  }
}
