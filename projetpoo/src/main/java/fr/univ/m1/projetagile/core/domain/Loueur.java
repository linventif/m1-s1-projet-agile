package fr.univ.m1.projetagile.core.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loueurs")
@PrimaryKeyJoinColumn(name = "id")
public class Loueur extends Utilisateur {

	private String telephone;

	private LocalDate dateInscription = LocalDate.now();

	protected Loueur() {
		super();
	}

	public Loueur(String nom, String prenom, String email, String motDePasse, String telephone) {
		super(nom, prenom, email, motDePasse);
		this.telephone = telephone;
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
}
