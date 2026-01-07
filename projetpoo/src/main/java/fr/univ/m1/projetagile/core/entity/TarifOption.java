package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarifs_options")
public class TarifOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nomOption")
    private String nomOption;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "grille_tarif_id", nullable = false)
    private GrilleTarif grilleTarif;

    // Constructeur sans argument pour JPA
    protected TarifOption() {
    }

    public TarifOption(String nomOption, Double prix, GrilleTarif grilleTarif) {
        this.nomOption = nomOption;
        this.prix = prix;
        this.grilleTarif = grilleTarif;
    }

    public TarifOption(String nomOption, String description, Double prix, GrilleTarif grilleTarif) {
        this.nomOption = nomOption;
        this.description = description;
        this.prix = prix;
        this.grilleTarif = grilleTarif;
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

    public GrilleTarif getGrilleTarif() {
        return grilleTarif;
    }

    public void setGrilleTarif(GrilleTarif grilleTarif) {
        this.grilleTarif = grilleTarif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

