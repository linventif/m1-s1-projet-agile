package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "entretiens_vehicules")
public class EntretienVehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean automatique;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "entretien_id", nullable = false)
    private Entretien entretien;

    // Constructeur sans argument pour JPA
    protected EntretienVehicule() {
    }

    public EntretienVehicule(Boolean automatique, Vehicule vehicule, Entretien entretien) {
        this.automatique = automatique;
        this.vehicule = vehicule;
        this.entretien = entretien;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public Boolean getAutomatique() {
        return automatique;
    }

    public void setAutomatique(Boolean automatique) {
        this.automatique = automatique;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Entretien getEntretien() {
        return entretien;
    }

    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }
}

