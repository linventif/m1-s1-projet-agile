package fr.univ.m1.projetagile.core.domain;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents")
public class Agent extends Utilisateur { 
 @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<Vehicule> vehicules = new ArrayList<>();
    
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    private List<OptionAgent> options = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAgent type = TypeAgent.PARTICULIER;
    private String siret;
    private String entrepriseNom;
    public enum TypeAgent {
        PARTICULIER,
        PROFESSIONNEL
    }
    
    // Constructeurs
    public Agent() {
        super();
    }
    
    public Agent(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
    }
    
    public Agent(String nom, String prenom, String email, String motDePasse, 
                 String siret, String entrepriseNom) {
        super(nom, prenom, email, motDePasse);
        this.siret = siret;
        this.entrepriseNom = entrepriseNom;
        this.type = TypeAgent.PROFESSIONNEL;
    }
    
    // Getters et Setters
    public List<Vehicule> getVehicules() { 
        return vehicules; 
    }
    
    public List<OptionAgent> getOptions() { 
        return options; 
    }
    public TypeAgent getType() { 
        return type; 
    }
    public void setType(TypeAgent type) { 
        this.type = type; 
    }
    public String getSiret() { 
        return siret; 
    }
    public void setSiret(String siret) { 
        this.siret = siret;
        if (siret != null && !siret.isEmpty()) {
            this.type = TypeAgent.PROFESSIONNEL;
        }
    }
    public String getEntrepriseNom() { 
        return entrepriseNom; 
    }
    public void setEntrepriseNom(String entrepriseNom) { 
        this.entrepriseNom = entrepriseNom; 
    }
    
    // Méthodes métier
    public void ajouterVehicule(Vehicule vehicule) {
        vehicule.setProprietaire(this);
        this.vehicules.add(vehicule);
    }
    
    public boolean supprimerVehicule(Long vehiculeId) {
        return vehicules.removeIf(v -> v.getId().equals(vehiculeId));
    }
    
    public boolean estProfessionnel() {
        return type == TypeAgent.PROFESSIONNEL;
    }
}


