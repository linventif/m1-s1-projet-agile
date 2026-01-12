package fr.univ.m1.projetagile.parrainage.entity;

import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente une relation de parrainage entre deux utilisateurs de la plateforme.
 *
 * <p>
 * Un parrainage lie un utilisateur parrain (celui qui parraine) à un utilisateur parrainé (celui
 * qui est parrainé). Les deux utilisateurs peuvent être de type Agent ou Loueur.
 * </p>
 *
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur
 *
 * @author Projet Agile M1
 * @version 1.0
 */
@Entity
@Table(name = "parrainages")
public class Parrainage {

  /**
   * Identifiant unique du parrainage (généré automatiquement).
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ID de l'utilisateur parrain (celui qui parraine).
   */
  @Column(name = "parrain_id", nullable = false)
  private Long parrainId;

  /**
   * ID de l'utilisateur parrainé (celui qui est parrainé).
   */
  @Column(name = "parraine_id", nullable = false)
  private Long parraineId;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected Parrainage() {}

  /**
   * Crée un nouveau parrainage entre un utilisateur parrain et un utilisateur parrainé.
   *
   * @param parrain l'utilisateur qui parraine (Agent ou Loueur)
   * @param parraine l'utilisateur qui est parrainé (Agent ou Loueur)
   */
  public Parrainage(Utilisateur parrain, Utilisateur parraine) {
    if (parrain == null || parraine == null) {
      throw new IllegalArgumentException("Le parrain et le parrainé ne peuvent pas être null");
    }
    if (parrain.getIdU() == null || parraine.getIdU() == null) {
      throw new IllegalArgumentException("Le parrain et le parrainé doivent avoir un ID");
    }
    if (parrain.getIdU().equals(parraine.getIdU())) {
      throw new IllegalArgumentException("Un utilisateur ne peut pas se parrainer lui-même");
    }
    this.parrainId = parrain.getIdU();
    this.parraineId = parraine.getIdU();
  }

  /**
   * Retourne l'identifiant unique du parrainage.
   *
   * @return l'ID du parrainage, ou null si le parrainage n'a pas encore été persisté
   */
  public Long getId() {
    return id;
  }

  /**
   * Définit l'identifiant unique du parrainage.
   *
   * @param id l'ID du parrainage
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retourne l'ID de l'utilisateur parrain.
   *
   * @return l'ID du parrain
   */
  public Long getParrainId() {
    return parrainId;
  }

  /**
   * Définit l'ID de l'utilisateur parrain.
   *
   * @param parrainId l'ID du parrain
   */
  public void setParrainId(Long parrainId) {
    this.parrainId = parrainId;
  }

  /**
   * Retourne l'ID de l'utilisateur parrainé.
   *
   * @return l'ID du parrainé
   */
  public Long getParraineId() {
    return parraineId;
  }

  /**
   * Définit l'ID de l'utilisateur parrainé.
   *
   * @param parraineId l'ID du parrainé
   */
  public void setParraineId(Long parraineId) {
    this.parraineId = parraineId;
  }

  /**
   * Retourne l'utilisateur parrain. Charge l'utilisateur depuis la base de données.
   *
   * @return l'utilisateur parrain, ou null si non trouvé
   */
  public Utilisateur getParrain() {
    if (parrainId == null) {
      return null;
    }
    var em = DatabaseConnection.getEntityManager();
    // Essayer de charger comme Loueur d'abord
    var loueur = em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, parrainId);
    if (loueur != null) {
      return loueur;
    }
    // Sinon, essayer comme Agent
    var agent = em.find(fr.univ.m1.projetagile.core.entity.Agent.class, parrainId);
    return agent;
  }

  /**
   * Définit le parrain du parrainage.
   *
   * @param parrain l'utilisateur parrain
   */
  public void setParrain(Utilisateur parrain) {
    if (parrain == null) {
      throw new IllegalArgumentException("Le parrain ne peut pas être null");
    }
    if (parrain.getIdU() == null) {
      throw new IllegalArgumentException("Le parrain doit avoir un ID");
    }
    this.parrainId = parrain.getIdU();
  }

  /**
   * Retourne l'utilisateur parrainé. Charge l'utilisateur depuis la base de données.
   *
   * @return l'utilisateur parrainé, ou null si non trouvé
   */
  public Utilisateur getParraine() {
    if (parraineId == null) {
      return null;
    }
    var em = DatabaseConnection.getEntityManager();
    // Essayer de charger comme Loueur d'abord
    var loueur = em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, parraineId);
    if (loueur != null) {
      return loueur;
    }
    // Sinon, essayer comme Agent
    var agent = em.find(fr.univ.m1.projetagile.core.entity.Agent.class, parraineId);
    return agent;
  }

  /**
   * Définit le parrainé du parrainage.
   *
   * @param parraine l'utilisateur parrainé
   */
  public void setParraine(Utilisateur parraine) {
    if (parraine == null) {
      throw new IllegalArgumentException("Le parrainé ne peut pas être null");
    }
    if (parraine.getIdU() == null) {
      throw new IllegalArgumentException("Le parrainé doit avoir un ID");
    }
    if (parrainId != null && parrainId.equals(parraine.getIdU())) {
      throw new IllegalArgumentException("Un utilisateur ne peut pas se parrainer lui-même");
    }
    this.parraineId = parraine.getIdU();
  }

  @Override
  public String toString() {
    return "Parrainage [id=" + id + ", parrainId=" + parrainId + ", parraineId=" + parraineId + "]";
  }
}
