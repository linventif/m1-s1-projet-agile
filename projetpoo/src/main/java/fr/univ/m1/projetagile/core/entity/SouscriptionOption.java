package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "souscription_options")
public class SouscriptionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Options option;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    // Constructeur sans argument pour JPA
    protected SouscriptionOption() {
    }

    public SouscriptionOption(Options option, Vehicule vehicule) {
        this.option = option;
        this.vehicule = vehicule;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    // Méthode selon UML
    public void annulerOption() {
        // Annule la souscription d'option
        // La suppression sera gérée par le service/repository qui appellera cette méthode
        // Cette méthode marque simplement l'intention d'annulation
        // TODO: Implémenter la logique de suppression si nécessaire
    }
}

