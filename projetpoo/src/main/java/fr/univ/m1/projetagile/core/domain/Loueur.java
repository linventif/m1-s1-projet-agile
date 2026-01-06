package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loueurs")
@PrimaryKeyJoinColumn(name = "id")
public class Loueur extends Utilisateur {

	@Column(nullable = false)
	private String nom;

	@Column(nullable = false)
	private String prenom;

	private String telephone;

	private LocalDate dateInscription = LocalDate.now();

	@OneToMany(mappedBy = "loueur", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Location> locations = new ArrayList<>();

	protected Loueur() {
		super();
	}

	public Loueur(String nom, String prenom, String email, String motDePasse, String telephone) {
		super(email, motDePasse);
		this.nom = nom;
		this.prenom = prenom;
		this.telephone = telephone;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public LocalDate getDateInscription() {
		return dateInscription;
	}

	public List<Location> getLocations() {
		return locations;
	}
}
