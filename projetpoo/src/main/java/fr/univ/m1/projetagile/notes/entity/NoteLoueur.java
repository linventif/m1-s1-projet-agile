package fr.univ.m1.projetagile.notes.entity;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Note attribuée par un agent à un loueur.
 *
 * <p>
 * Permet aux agents d'évaluer le comportement et le sérieux des loueurs selon plusieurs critères
 * personnalisables.
 * </p>
 *
 * @author Projet Agile M1
 * @version 2.0
 * @since 1.0
 */
@Entity
@Table(name = "notes_loueurs")
public class NoteLoueur extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  /**
   * Constructeur sans argument pour JPA. Ne pas utiliser directement.
   */
  protected NoteLoueur() {}

  /**
   * Crée une nouvelle note pour un loueur avec une liste de critères.
   *
   * @param agent l'agent qui évalue
   * @param loueur le loueur évalué
   * @param criteres la liste des critères d'évaluation
   * @throws IllegalArgumentException si l'agent, le loueur ou les critères sont invalides
   */
  public NoteLoueur(Agent agent, Loueur loueur, List<Critere> criteres) {
    super(criteres);
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }
    this.agent = agent;
    this.loueur = loueur;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Agent getAgent() {
    return agent;
  }

  public void setAgent(Agent agent) {
    this.agent = agent;
  }

  public Loueur getLoueur() {
    return loueur;
  }

  public void setLoueur(Loueur loueur) {
    this.loueur = loueur;
  }

  @Override
  public String toString() {
    return "NoteLoueur [id=" + id + ", agent=" + (agent != null ? agent.getIdU() : "null")
        + ", loueur=" + (loueur != null ? loueur.getIdU() : "null") + ", moyenne="
        + getNoteMoyenne() + "/10, criteres=" + criteres.size() + ", date=" + date + "]";
  }
}
