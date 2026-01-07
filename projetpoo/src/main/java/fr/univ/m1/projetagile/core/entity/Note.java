package fr.univ.m1.projetagile.core.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Note {

  @Column(nullable = false)
  protected Double note1;

  @Column(nullable = false)
  protected Double note2;

  @Column(nullable = false)
  protected Double note3;

  @Column(nullable = false)
  protected LocalDate date;

  // Constructeur sans argument pour JPA
  protected Note() {}

  protected Note(Double note1, Double note2, Double note3) {
    this.note1 = note1;
    this.note2 = note2;
    this.note3 = note3;
    this.date = LocalDate.now();
  }

  // Getters et Setters
  public Double getNote1() {
    return note1;
  }

  public void setNote1(Double note1) {
    this.note1 = note1;
  }

  public Double getNote2() {
    return note2;
  }

  public void setNote2(Double note2) {
    this.note2 = note2;
  }

  public Double getNote3() {
    return note3;
  }

  public void setNote3(Double note3) {
    this.note3 = note3;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Double getNoteMoyenne() {
    if (note1 != null && note2 != null && note3 != null) {
      return (note1 + note2 + note3) / 3.0;
    }
    return null;
  }

  // Méthode abstraite selon UML
  public abstract void Noter();
}
