package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "agents_professionnels")
@PrimaryKeyJoinColumn(name = "id")
public class AgentProfessionnel extends Agent {

    @Column(nullable = false)
    private String siret;

    @Column(nullable = false)
    private String entrepriseNom;

    // Constructeur sans argument pour JPA
    protected AgentProfessionnel() {
        super();
    }

    public AgentProfessionnel(String email, String motDePasse, String siret, String entrepriseNom) {
        super(email, motDePasse);
        this.siret = siret;
        this.entrepriseNom = entrepriseNom;
    }

    // Getters et Setters
    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getEntrepriseNom() {
        return entrepriseNom;
    }

    public void setEntrepriseNom(String entrepriseNom) {
        this.entrepriseNom = entrepriseNom;
    }

    @Override
    public boolean estProfessionnel() {
        return true;
    }
}

