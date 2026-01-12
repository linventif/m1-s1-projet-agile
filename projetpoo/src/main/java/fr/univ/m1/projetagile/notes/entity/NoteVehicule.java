package fr.univ.m1.projetagile.notes.entity;

import java.util.List;
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
 * Permet aux loueurs d'évaluer la qualité et l'état des véhicules loués selon plusieurs critères
 * personnalisables.
 * </p>
 *
 * @author Projet Agile M1
 * @version 2.0
 * @since 1.0
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

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected NoteVehicule() {}

  /**
   * Crée une nouvelle note pour un véhicule avec une liste de critères.
   *
   * @param vehicule le véhicule évalué
   * @param loueur le loueur qui évalue
   * @param criteres la liste des critères d'évaluation
   * @throws IllegalArgumentException si le véhicule, le loueur ou les critères sont invalides
   */
  public NoteVehicule(Vehicule vehicule, Loueur loueur, List<Critere> criteres) {
    super(criteres);
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
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
        + getNoteMoyenne() + "/10, criteres=" + criteres.size() + ", date=" + date + "]";
  }
}
