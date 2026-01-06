package fr.univ.m1.projetagile.core.domain;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Agent extends Utilisateur { 
    
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<Vehicule> vehicules = new ArrayList<>();
    
    // Constructeurs
    protected Agent() {
        super();
    }
    
    protected Agent(String email, String motDePasse) {
        super(email, motDePasse);
    }
    
    // Getters et Setters
    public List<Vehicule> getVehicules() { 
        return vehicules; 
    }
    
    // Méthodes métier
    public void ajouterVehicule(Vehicule vehicule) {
        vehicule.setProprietaire(this);
        this.vehicules.add(vehicule);
    }
    
    public boolean supprimerVehicule(Long vehiculeId) {
        return vehicules.removeIf(v -> v.getId().equals(vehiculeId));
    }
    
    // Méthode abstraite à implémenter par les sous-classes
    public abstract boolean estProfessionnel();
}


