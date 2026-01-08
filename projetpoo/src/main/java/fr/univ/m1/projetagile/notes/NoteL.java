package fr.univ.m1.projetagile.notes;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes_loueurs")
public class NoteL extends Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "loueur_id", nullable = false)
  private Loueur loueur;

  // Constructeur sans argument pour JPA
  protected NoteL() {
    super();
  }

  public static NoteL create() {
    return new NoteL();
  }

  public NoteL(Double note1, Double note2, Double note3, Agent agent, Loueur loueur) {
    super(note1, note2, note3);
    this.agent = agent;
    this.loueur = loueur;
  }

  // Getters et Setters
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

  // Méthode selon UML
  @Override
  public void Noter() {
    // Implémente la notation d'un loueur
    // Cette méthode peut être utilisée pour valider ou finaliser la note
  }

  public static NoteL NoterLoueur(Agent agent, Loueur loueur, Double note1, Double note2,
      Double note3) {
    // Méthode statique pour créer une note de loueur
    return new NoteL(note1, note2, note3, agent, loueur);
  }
}
