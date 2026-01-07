package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "assurances")
public class Assurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "grille_tarif_id", nullable = false)
    private GrilleTarif grille;

    @OneToMany(mappedBy = "assurance", cascade = CascadeType.ALL)
    private List<SouscriptionAssurance> souscriptions = new ArrayList<>();

    // Constructeur sans argument pour JPA
    protected Assurance() {
    }

    public Assurance(String nom, GrilleTarif grille) {
        this.nom = nom;
        this.grille = grille;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public GrilleTarif getGrille() {
        return grille;
    }

    public void setGrille(GrilleTarif grille) {
        this.grille = grille;
    }

    public List<SouscriptionAssurance> getSouscriptions() {
        return Collections.unmodifiableList(souscriptions);
    }

    // Méthode selon UML
    public void importerGrille(GrilleTarif nouvelleGrille) {
        // Importe une nouvelle grille tarifaire
        if (nouvelleGrille != null) {
            this.grille = nouvelleGrille;
        }
    }
}

