package fr.univ.m1.projetagile.parrainage.entity;

import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente le crédit d'un utilisateur de la plateforme.
 *
 * <p>
 * Un crédit est associé à un utilisateur (Agent ou Loueur) et stocke le montant de crédit
 * disponible pour cet utilisateur. Le crédit peut être utilisé pour diverses opérations sur la
 * plateforme.
 * </p>
 *
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 *
 * @author Projet Agile M1
 * @version 1.0
 */
@Entity
@Table(name = "credits")
public class Crédit {

  /**
   * Identifiant unique du crédit (généré automatiquement).
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ID de l'utilisateur propriétaire du crédit (Agent ou Loueur).
   */
  @Column(name = "utilisateur_id", nullable = false)
  private Long utilisateurId;

  /**
   * Montant du crédit disponible pour l'utilisateur.
   */
  @Column(nullable = false)
  private Double credit;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected Crédit() {}

  /**
   * Crée un nouveau crédit pour un utilisateur avec un montant initial.
   *
   * @param utilisateur l'utilisateur propriétaire du crédit (Agent ou Loueur)
   * @param credit le montant initial du crédit
   * @throws IllegalArgumentException si l'utilisateur est null, n'a pas d'ID, ou si le crédit est
   *         négatif
   */
  public Crédit(Utilisateur utilisateur, Double credit) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    if (utilisateur.getIdU() == null) {
      throw new IllegalArgumentException("L'utilisateur doit avoir un ID");
    }
    if (credit == null) {
      throw new IllegalArgumentException("Le crédit ne peut pas être null");
    }
    if (credit < 0) {
      throw new IllegalArgumentException("Le crédit ne peut pas être négatif");
    }
    this.utilisateurId = utilisateur.getIdU();
    this.credit = credit;
  }

  /**
   * Retourne l'identifiant unique du crédit.
   *
   * @return l'ID du crédit, ou null si le crédit n'a pas encore été persisté
   */
  public Long getId() {
    return id;
  }

  /**
   * Définit l'identifiant unique du crédit.
   *
   * @param id l'ID du crédit
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retourne l'ID de l'utilisateur propriétaire du crédit.
   *
   * @return l'ID de l'utilisateur
   */
  public Long getUtilisateurId() {
    return utilisateurId;
  }

  /**
   * Définit l'ID de l'utilisateur propriétaire du crédit.
   *
   * @param utilisateurId l'ID de l'utilisateur
   */
  public void setUtilisateurId(Long utilisateurId) {
    if (utilisateurId == null) {
      throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
    }
    this.utilisateurId = utilisateurId;
  }

  /**
   * Retourne le montant du crédit disponible.
   *
   * @return le montant du crédit
   */
  public Double getCredit() {
    return credit;
  }

  /**
   * Définit le montant du crédit disponible.
   *
   * @param credit le nouveau montant du crédit
   * @throws IllegalArgumentException si le crédit est null ou négatif
   */
  public void setCredit(Double credit) {
    if (credit == null) {
      throw new IllegalArgumentException("Le crédit ne peut pas être null");
    }
    if (credit < 0) {
      throw new IllegalArgumentException("Le crédit ne peut pas être négatif");
    }
    this.credit = credit;
  }

  /**
   * Définit l'utilisateur propriétaire du crédit.
   *
   * @param utilisateur l'utilisateur propriétaire
   */
  public void setUtilisateur(Utilisateur utilisateur) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
    }
    if (utilisateur.getIdU() == null) {
      throw new IllegalArgumentException("L'utilisateur doit avoir un ID");
    }
    this.utilisateurId = utilisateur.getIdU();
  }

  /**
   * Ajoute un montant au crédit existant.
   *
   * @param montant le montant à ajouter (doit être positif)
   * @throws IllegalArgumentException si le montant est null ou négatif
   */
  public void ajouterCredit(Double montant) {
    if (montant == null) {
      throw new IllegalArgumentException("Le montant ne peut pas être null");
    }
    if (montant < 0) {
      throw new IllegalArgumentException("Le montant à ajouter ne peut pas être négatif");
    }
    this.credit += montant;
  }

  /**
   * Retire un montant du crédit existant.
   *
   * @param montant le montant à retirer (doit être positif)
   * @throws IllegalArgumentException si le montant est null ou négatif
   * @throws IllegalStateException si le crédit est insuffisant
   */
  public void retirerCredit(Double montant) {
    if (montant == null) {
      throw new IllegalArgumentException("Le montant ne peut pas être null");
    }
    if (montant < 0) {
      throw new IllegalArgumentException("Le montant à retirer ne peut pas être négatif");
    }
    if (this.credit < montant) {
      throw new IllegalStateException("Crédit insuffisant. Crédit disponible : " + this.credit
          + ", montant demandé : " + montant);
    }
    this.credit -= montant;
  }

  /**
   * Vérifie si le crédit est suffisant pour un montant donné.
   *
   * @param montant le montant à vérifier
   * @return true si le crédit est suffisant, false sinon
   */
  public boolean creditSuffisant(Double montant) {
    if (montant == null || montant < 0) {
      return false;
    }
    return this.credit >= montant;
  }

  @Override
  public String toString() {
    return "Crédit [id=" + id + ", utilisateurId=" + utilisateurId + ", credit=" + credit + "]";
  }
}
