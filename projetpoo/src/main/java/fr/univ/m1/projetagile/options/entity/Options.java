package fr.univ.m1.projetagile.options.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "options")
public class Options {

  // ID de l'option "Accepter les contrats manuellement" en base de données
  public static final Long ACCEPTATION_MANUELLE_OPTION_ID = 5L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, name = "nomOption")
  private String nomOption;

  @Column(nullable = false)
  private Double prix;

  @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
  private List<SouscriptionOption> souscriptions = new ArrayList<>();

  // Constructeur sans argument pour JPA
  protected Options() {}

  public Options(String nomOption, Double prix) {
    this.nomOption = nomOption;
    this.prix = prix;
  }

  // Getters et Setters
  public Long getId() {
    return id;
  }

  public String getNomOption() {
    return nomOption;
  }

  public void setNomOption(String nomOption) {
    this.nomOption = nomOption;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    this.prix = prix;
  }

  public List<SouscriptionOption> getSouscriptions() {
    return Collections.unmodifiableList(souscriptions);
  }
}
