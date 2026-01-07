package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prix_entretiens")
public class PrixEntretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "typeVehi")
    private TypeV typeVehi;

    @Column(nullable = false, name = "modeleVehi")
    private String modeleVehi;

    @Column(nullable = false)
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "entretien_id", nullable = false)
    private Entretien entretien;

    // Constructeur sans argument pour JPA
    protected PrixEntretien() {
    }

    public PrixEntretien(TypeV typeVehi, String modeleVehi, Double prix, Entretien entretien) {
        this.typeVehi = typeVehi;
        this.modeleVehi = modeleVehi;
        this.prix = prix;
        this.entretien = entretien;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public TypeV getTypeVehi() {
        return typeVehi;
    }

    public void setTypeVehi(TypeV typeVehi) {
        this.typeVehi = typeVehi;
    }

    public String getModeleVehi() {
        return modeleVehi;
    }

    public void setModeleVehi(String modeleVehi) {
        this.modeleVehi = modeleVehi;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Entretien getEntretien() {
        return entretien;
    }

    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }
}

