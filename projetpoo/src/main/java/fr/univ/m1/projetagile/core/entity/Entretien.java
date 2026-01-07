package fr.univ.m1.projetagile.core.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "entretiens")
public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL)
    private List<PrixEntretien> prixEntretiens = new ArrayList<>();

    @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL)
    private List<EntretienVehicule> entretienVehicules = new ArrayList<>();

    // Constructeur sans argument pour JPA
    protected Entretien() {
    }

    public Entretien(String nom) {
        this.nom = nom;
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

    public List<PrixEntretien> getPrixEntretiens() {
        return Collections.unmodifiableList(prixEntretiens);
    }

    public List<EntretienVehicule> getEntretienVehicules() {
        return Collections.unmodifiableList(entretienVehicules);
    }

    // Méthodes selon UML
    public PrixEntretien definirTarif(TypeV typeVehi, String modeleVehi, Double prix) {
        // Définit un tarif pour un type de véhicule et un modèle
        PrixEntretien prixEntretien = new PrixEntretien(typeVehi, modeleVehi, prix, this);
        prixEntretiens.add(prixEntretien);
        return prixEntretien;
    }

    public void importerTarif(String fichier) {
        // Importe un tarif depuis un fichier
        // TODO: Implémenter l'import depuis fichier (CSV, JSON, etc.)
    }

    public Double calculerTarif(Vehicule vehicule) {
        // Calcule le tarif d'entretien pour un véhicule donné
        for (PrixEntretien prixEntretien : prixEntretiens) {
            if (prixEntretien.getTypeVehi() == vehicule.getType() && 
                prixEntretien.getModeleVehi().equals(vehicule.getModele())) {
                return prixEntretien.getPrix();
            }
        }
        return null; // Aucun tarif trouvé
    }

    public EntretienVehicule planifierEntretien(Vehicule vehicule, boolean automatique) {
        // Planifie un entretien pour un véhicule
        EntretienVehicule ev = new EntretienVehicule(automatique, vehicule, this);
        entretienVehicules.add(ev);
        return ev;
    }
}

