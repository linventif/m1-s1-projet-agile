package fr.univ.m1.projetagile.notes.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;

/**
 * Classe abstraite représentant une note générique.
 *
 * <p>
 * Une note est composée d'une liste de critères et d'une date de notation. Chaque critère possède
 * un nom et une note entre 0 et 10.
 * </p>
 *
 * @author Projet Agile M1
 * @version 2.0
 * @since 1.0
 */
@MappedSuperclass
public abstract class Note {

  /**
   * Liste des critères d'évaluation avec leurs notes.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "note_criteres", joinColumns = @JoinColumn(name = "note_id"),
      inverseJoinColumns = @JoinColumn(name = "critere_id"))
  protected List<Critere> criteres = new ArrayList<>();

  /**
   * Date de la notation.
   */
  @Column(nullable = false, name = "date_note")
  protected LocalDate date;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected Note() {}

  /**
   * Crée une nouvelle note avec une liste de critères. La date est automatiquement définie à
   * aujourd'hui.
   *
   * @param criteres la liste des critères d'évaluation
   * @throws IllegalArgumentException si la liste de critères est null ou vide
   */
  protected Note(List<Critere> criteres) {
    if (criteres == null || criteres.isEmpty()) {
      throw new IllegalArgumentException("La liste de critères ne peut pas être vide");
    }
    this.criteres = new ArrayList<>(criteres);
    this.date = LocalDate.now();
  }

  /**
   * Retourne une copie de la liste des critères d'évaluation.
   *
   * @return la liste des critères
   */
  public List<Critere> getCriteres() {
    return new ArrayList<>(criteres);
  }

  /**
   * Ajoute un nouveau critère à la note.
   *
   * @param critere le critère à ajouter
   * @throws IllegalArgumentException si le critère est null
   */
  public void ajouterCritere(Critere critere) {
    if (critere == null) {
      throw new IllegalArgumentException("Le critère ne peut pas être null");
    }
    this.criteres.add(critere);
  }

  /**
   * Remplace tous les critères par une nouvelle liste.
   *
   * @param criteres la nouvelle liste de critères
   * @throws IllegalArgumentException si la liste est null ou vide
   */
  public void setCriteres(List<Critere> criteres) {
    if (criteres == null || criteres.isEmpty()) {
      throw new IllegalArgumentException("La liste de critères ne peut pas être vide");
    }
    this.criteres = new ArrayList<>(criteres);
  }

  /**
   * Retourne la date de notation.
   *
   * @return la date de notation
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Définit la date de notation.
   *
   * @param date la nouvelle date de notation
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Calcule la moyenne des notes de tous les critères.
   *
   * @return la moyenne arrondie à 2 décimales, ou 0.0 si aucun critère
   */
  public Double getNoteMoyenne() {
    if (criteres.isEmpty()) {
      return 0.0;
    }
    double somme = criteres.stream().mapToDouble(Critere::getNote).sum();
    double moyenne = somme / criteres.size();
    return Math.round(moyenne * 100.0) / 100.0;
  }
}
