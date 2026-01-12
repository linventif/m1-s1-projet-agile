package fr.univ.m1.projetagile.notes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente un critère d'évaluation avec un nom et une note.
 *
 * <p>
 * Un critère est une composante d'une note globale, permettant d'évaluer différents aspects (ex:
 * ponctualité, propreté, communication, etc.).
 * </p>
 *
 * @author Projet Agile M1
 * @version 2.0
 * @since 1.0
 */
@Entity
@Table(name = "criteres")
public class Critere {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Nom du critère (ex: "Ponctualité", "Propreté", "Communication").
   */
  @Column(nullable = false, length = 100)
  private String nom;

  /**
   * Note attribuée pour ce critère (entre 0 et 10).
   */
  @Column(nullable = false)
  private Double note;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected Critere() {}

  /**
   * Crée un nouveau critère avec un nom et une note.
   *
   * @param nom le nom du critère (max 100 caractères)
   * @param note la note du critère (entre 0 et 10)
   * @throws IllegalArgumentException si le nom est vide ou si la note est invalide
   */
  public Critere(String nom, Double note) {
    validateNom(nom);
    validateNote(note);
    this.nom = nom;
    this.note = note;
  }

  /**
   * Valide le nom du critère.
   *
   * @param nom le nom à valider
   * @throws IllegalArgumentException si le nom est null ou vide
   */
  private void validateNom(String nom) {
    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom du critère ne peut pas être vide");
    }
  }

  /**
   * Valide la note du critère.
   *
   * @param note la note à valider
   * @throws IllegalArgumentException si la note est null ou hors de l'intervalle [0, 10]
   */
  private void validateNote(Double note) {
    if (note == null) {
      throw new IllegalArgumentException("La note ne peut pas être null");
    }
    if (note < 0.0 || note > 10.0) {
      throw new IllegalArgumentException("La note doit être entre 0 et 10 (valeur: " + note + ")");
    }
  }

  /**
   * Retourne l'identifiant unique du critère.
   *
   * @return l'ID du critère
   */
  public Long getId() {
    return id;
  }

  /**
   * Définit l'identifiant unique du critère.
   *
   * @param id l'ID du critère
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retourne le nom du critère.
   *
   * @return le nom du critère
   */
  public String getNom() {
    return nom;
  }

  /**
   * Définit le nom du critère.
   *
   * @param nom le nouveau nom du critère
   * @throws IllegalArgumentException si le nom est vide
   */
  public void setNom(String nom) {
    validateNom(nom);
    this.nom = nom;
  }

  /**
   * Retourne la note du critère.
   *
   * @return la note du critère
   */
  public Double getNote() {
    return note;
  }

  /**
   * Définit la note du critère.
   *
   * @param note la nouvelle note du critère
   * @throws IllegalArgumentException si la note est invalide
   */
  public void setNote(Double note) {
    validateNote(note);
    this.note = note;
  }

  @Override
  public String toString() {
    return nom + ": " + note + "/10";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Critere critere = (Critere) o;
    return id != null && id.equals(critere.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
