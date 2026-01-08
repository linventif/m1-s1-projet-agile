package fr.univ.m1.projetagile.notes.entity;

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
 * Note attribuée par un loueur à un agent.
 *
 * <p>
 * Permet aux loueurs d'évaluer la qualité du service fourni par les agents.
 * </p>
 */
@Entity
@Table(name = "notes_agents")
public class NoteAgent extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  protected NoteAgent() {}

  public NoteAgent(Agent agent, Loueur loueur, Double note1, Double note2, Double note3) {
    super(note1, note2, note3);
    if (agent == null) {
      throw new IllegalArgumentException("Agent ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Loueur ne peut pas être null");
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
    return "NoteAgent [id=" + id + ", agent=" + (agent != null ? agent.getIdU() : "null")
        + ", loueur=" + (loueur != null ? loueur.getIdU() : "null") + ", moyenne="
        + getNoteMoyenne() + ", date=" + date + "]";
  }
}
