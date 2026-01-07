package fr.univ.m1.projetagile.core.entity;

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

  public NoteV(Double note1, Double note2, Double note3, Vehicule vehicule, Loueur loueur) {
    super(note1, note2, note3);
    this.vehicule = vehicule;
    this.loueur = loueur;
  }

  // Getters et Setters
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

  // Méthode selon UML
  @Override
  public void Noter() {
    // Implémente la notation d'un véhicule
    // Cette méthode peut être utilisée pour valider ou finaliser la note
  }

  public static NoteV NoterVehicule(Vehicule vehicule, Loueur loueur, Double note1, Double note2,
      Double note3) {
    // Méthode statique pour créer une note de véhicule
    return new NoteV(note1, note2, note3, vehicule, loueur);
  }
}
