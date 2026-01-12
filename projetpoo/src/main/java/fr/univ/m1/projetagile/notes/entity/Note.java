package fr.univ.m1.projetagile.notes.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Classe abstraite représentant une note générique.
 *
 * <p>
 * Une note est composée de 3 notes partielles et d'une date de notation.
 * </p>
 */
@MappedSuperclass
public abstract class Note {

  @Column(nullable = false)
  protected Double note1;

  @Column(nullable = false)
  protected Double note2;

  @Column(nullable = false)
  protected Double note3;

  @Column(nullable = false, name = "date_note")
  protected LocalDate date;

  protected Note() {}

  protected Note(Double note1, Double note2, Double note3) {
    validateNote(note1, "note1");
    validateNote(note2, "note2");
    validateNote(note3, "note3");

    this.note1 = note1;
    this.note2 = note2;
    this.note3 = note3;
    this.date = LocalDate.now();
  }

  private void validateNote(Double note, String nomNote) {
    if (note == null) {
      throw new IllegalArgumentException(nomNote + " ne peut pas être null");
    }
    if (note < 0.0 || note > 10.0) {
      throw new IllegalArgumentException(
          nomNote + " doit être entre 0 et 10 (valeur: " + note + ")");
    }
  }

  public Double getNote1() {
    return note1;
  }

  public void setNote1(Double note1) {
    validateNote(note1, "note1");
    this.note1 = note1;
  }

  public Double getNote2() {
    return note2;
  }

  public void setNote2(Double note2) {
    validateNote(note2, "note2");
    this.note2 = note2;
  }

  public Double getNote3() {
    return note3;
  }

  public void setNote3(Double note3) {
    validateNote(note3, "note3");
    this.note3 = note3;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Calcule la moyenne des 3 notes.
   *
   * @return la moyenne arrondie à 2 décimales
   */
  public Double getNoteMoyenne() {
    return Math.round((note1 + note2 + note3) / 3.0 * 100.0) / 100.0;
  }
}
