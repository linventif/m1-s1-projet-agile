package fr.univ.m1.projetagile.notes;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.enums.CritereNoteAgent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes_agents")
public class NoteA extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  protected NoteA() {
    super();
  }

  public static NoteA create() {
    return new NoteA();
  }

  public NoteA(Double note1, Double note2, Double note3, Agent agent, Loueur loueur) {
    super(note1, note2, note3);
    this.agent = agent;
    this.loueur = loueur;
  }

  // =======================
  // Association aux critères (#23)
  // =======================

  public Double getPonctualite() {
    return note1;
  }

  public Double getCommunication() {
    return note2;
  }

  public Double getSerieux() {
    return note3;
  }

  public static CritereNoteAgent getCritere1() {
    return CritereNoteAgent.PONCTUALITE;
  }

  public static CritereNoteAgent getCritere2() {
    return CritereNoteAgent.COMMUNICATION;
  }

  public static CritereNoteAgent getCritere3() {
    return CritereNoteAgent.SERIEUX;
  }

  // =======================
  // Getters / Setters
  // =======================

  public Long getId() {
    return id;
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

  // =======================
  // Méthodes métier
  // =======================

  /**
   * Méthode métier : finaliser/valider la note. (On évite @Override pour ne pas casser la
   * compilation si la signature dans Note diffère.)
   */
  public void noter() {
    // Exemple : validations supplémentaires si besoin
  }

  /**
   * Compatibilité si ton UML / ancien code appelle Noter()
   */
  public void Noter() {
    noter();
  }

  public static NoteA NoterAgent(Agent agent, Loueur loueur, Double note1, Double note2,
      Double note3) {
    return new NoteA(note1, note2, note3, agent, loueur);
  }
}
