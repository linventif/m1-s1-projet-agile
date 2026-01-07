package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "souscription_assurances")
public class SouscriptionAssurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "souscription_assurance_options", joinColumns = @JoinColumn(name = "souscription_id"))
    @Column(name = "option")
    private List<String> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "assurance_id", nullable = false)
    private Assurance assurance;

    // Constructeur sans argument pour JPA
    protected SouscriptionAssurance() {
    }

    public SouscriptionAssurance(Location location, Assurance assurance) {
        this.location = location;
        this.assurance = assurance;
    }

    // Méthode selon UML
    public static SouscriptionAssurance souscrire(Location location, Assurance assurance, List<String> options) {
        SouscriptionAssurance souscription = new SouscriptionAssurance(location, assurance);
        if (options != null) {
            souscription.options.addAll(options);
        }
        return souscription;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void ajouterOption(String option) {
        if (option != null) {
            options.add(option);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Assurance getAssurance() {
        return assurance;
    }

    public void setAssurance(Assurance assurance) {
        this.assurance = assurance;
    }

    // Méthode selon UML
    public Double calculerPrix() {
        // Calcule le prix de la souscription d'assurance
        // TODO: Implémenter le calcul basé sur la grille tarifaire et les options
        Double prixBase = 0.0;
        if (assurance != null && assurance.getGrille() != null) {
            // Calculer selon la grille tarifaire
            // Le prix dépendra des options sélectionnées et du type de véhicule
        }
        return prixBase;
    }
}

