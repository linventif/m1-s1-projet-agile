package fr.univ.m1.projetagile.notes;

import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.CritereNoteVehicule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes_vehicules")
public class NoteV extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "vehicule_id", nullable = false)
  private Vehicule vehicule;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  // Constructeur sans argument pour JPA
  protected NoteV() {
    super();
  }

  public static NoteV create() {
    return new NoteV();
  }

  public NoteV(Double note1, Double note2, Double note3, Vehicule vehicule, Loueur loueur) {
    super(note1, note2, note3);
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  // =======================
  // Association aux critères (#23)
  // =======================

  /** Critère 1 : Propreté */
  public Double getProprete() {
    return note1;
  }

  /** Critère 2 : Confort */
  public Double getConfort() {
    return note2;
  }

  /** Critère 3 : Conformité à l’annonce */
  public Double getConformiteAnnonce() {
    return note3;
  }

  /** Définition explicite des critères */
  public static CritereNoteVehicule getCritere1() {
    return CritereNoteVehicule.PROPRETE;
  }

  public static CritereNoteVehicule getCritere2() {
    return CritereNoteVehicule.CONFORT;
  }

  public static CritereNoteVehicule getCritere3() {
    return CritereNoteVehicule.CONFORMITE_ANNONCE;
  }

  // =======================
  // Getters / Setters
  // =======================

  public Long getId() {
    return id;
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

  // =======================
  // Méthodes métier
  // =======================

  @Override
  public void Noter() {
    // Méthode métier : possibilité future de validation,
    // règles supplémentaires, etc.
  }

  public static NoteV NoterVehicule(Vehicule vehicule, Loueur loueur, Double note1, Double note2,
      Double note3) {
    return new NoteV(note1, note2, note3, vehicule, loueur);
  }
}
