package fr.univ.m1.projetagile.notes.entity;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Note attribuée par un agent à un loueur.
 *
 * <p>
 * Permet aux agents d'évaluer le comportement et le sérieux des loueurs.
 * </p>
 */
@Entity
@Table(name = "notes_loueurs")
public class NoteLoueur extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "agent_id", nullable = false)
  private Long agentId;

  @Column(name = "loueur_id", nullable = false)
  private Long loueurId;

  protected NoteLoueur() {}

  public NoteLoueur(Agent agent, Loueur loueur, Double note1, Double note2, Double note3) {
    super(note1, note2, note3);
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("Agent ne peut pas être null ou sans ID");
    }
    if (loueur == null || loueur.getIdU() == null) {
      throw new IllegalArgumentException("Loueur ne peut pas être null ou sans ID");
    }
    this.agentId = agent.getIdU();
    this.loueurId = loueur.getIdU();
  }

  public NoteLoueur(Long loueurId, Long agentId) {
    super();
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    if (agentId == null) {
      throw new IllegalArgumentException("Agent ID ne peut pas être null");
    }
    this.loueurId = loueurId;
    this.agentId = agentId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  public Long getLoueurId() {
    return loueurId;
  }

  public void setLoueurId(Long loueurId) {
    this.loueurId = loueurId;
  }

  @Override
  public String toString() {
    return "NoteLoueur [id=" + id + ", agent=" + agentId + ", loueur=" + loueurId + ", moyenne="
        + getNoteMoyenne() + ", date=" + date + "]";
  }
}
