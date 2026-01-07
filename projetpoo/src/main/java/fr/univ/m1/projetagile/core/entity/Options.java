package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "options")
public class Options {

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
    protected Options() {
    }

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

