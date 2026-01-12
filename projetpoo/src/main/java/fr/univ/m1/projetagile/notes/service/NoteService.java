package fr.univ.m1.projetagile.notes.service;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.persistence.NoteAgentRepository;
import fr.univ.m1.projetagile.notes.persistence.NoteLoueurRepository;
import fr.univ.m1.projetagile.notes.persistence.NoteVehiculeRepository;

/**
 * Service pour gérer les notes (agents, loueurs, véhicules).
 *
 * <p>
 * Ce service fournit une couche métier pour créer et gérer les notes avec validation automatique et
 * sauvegarde en base de données.
 * </p>
 */
public class NoteService {

  private final NoteAgentRepository noteAgentRepository;
  private final NoteLoueurRepository noteLoueurRepository;
  private final NoteVehiculeRepository noteVehiculeRepository;

  public NoteService() {
    this.noteAgentRepository = new NoteAgentRepository();
    this.noteLoueurRepository = new NoteLoueurRepository();
    this.noteVehiculeRepository = new NoteVehiculeRepository();
  }

  public NoteService(NoteAgentRepository noteAgentRepo, NoteLoueurRepository noteLoueurRepo,
      NoteVehiculeRepository noteVehiculeRepo) {
    this.noteAgentRepository = noteAgentRepo;
    this.noteLoueurRepository = noteLoueurRepo;
    this.noteVehiculeRepository = noteVehiculeRepo;
  }

  // ==================== NOTES AGENT ====================

  /**
   * Permet à un loueur de noter un agent.
   */
  public NoteAgent noterAgent(Loueur loueur, Agent agent, Double note1, Double note2,
      Double note3) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null");
    }

    NoteAgent note = new NoteAgent(agent, loueur, note1, note2, note3);
    return noteAgentRepository.save(note);
  }

  public List<NoteAgent> getNotesAgent(Agent agent) {
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("Agent invalide");
    }
    return noteAgentRepository.findByAgentId(agent.getIdU());
  }

  public Double getMoyenneAgent(Agent agent) {
    if (agent == null || agent.getIdU() == null) {
      throw new IllegalArgumentException("Agent invalide");
    }
    return noteAgentRepository.getMoyenneByAgentId(agent.getIdU());
  }

  // ==================== NOTES LOUEUR ====================

  /**
   * Permet à un agent de noter un loueur.
   */
  public NoteLoueur noterLoueur(Agent agent, Loueur loueur, Double note1, Double note2,
      Double note3) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }

    NoteLoueur note = new NoteLoueur(agent, loueur, note1, note2, note3);
    return noteLoueurRepository.save(note);
  }

  public List<NoteLoueur> getNotesLoueur(Loueur loueur) {
    if (loueur == null || loueur.getIdU() == null) {
      throw new IllegalArgumentException("Loueur invalide");
    }
    return noteLoueurRepository.findByLoueurId(loueur.getIdU());
  }

  public Double getMoyenneLoueur(Loueur loueur) {
    if (loueur == null || loueur.getIdU() == null) {
      throw new IllegalArgumentException("Loueur invalide");
    }
    return noteLoueurRepository.getMoyenneByLoueurId(loueur.getIdU());
  }

  // ==================== NOTES VEHICULE ====================

  /**
   * Permet à un loueur de noter un véhicule.
   */
  public NoteVehicule noterVehicule(Loueur loueur, Vehicule vehicule, Double note1, Double note2,
      Double note3) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }

    NoteVehicule note = new NoteVehicule(vehicule, loueur, note1, note2, note3);
    return noteVehiculeRepository.save(note);
  }

  public List<NoteVehicule> getNotesVehicule(Vehicule vehicule) {
    if (vehicule == null || vehicule.getId() == null) {
      throw new IllegalArgumentException("Véhicule invalide");
    }
    return noteVehiculeRepository.findByVehiculeId(vehicule.getId());
  }

  public Double getMoyenneVehicule(Vehicule vehicule) {
    if (vehicule == null || vehicule.getId() == null) {
      throw new IllegalArgumentException("Véhicule invalide");
    }
    return noteVehiculeRepository.getMoyenneByVehiculeId(vehicule.getId());
  }

  // ==================== SUPPRESSIONS ====================

  public void supprimerNoteAgent(NoteAgent note) {
    if (note == null || note.getId() == null) {
      throw new IllegalArgumentException("Note invalide");
    }
    noteAgentRepository.delete(note.getId());
  }

  public void supprimerNoteLoueur(NoteLoueur note) {
    if (note == null || note.getId() == null) {
      throw new IllegalArgumentException("Note invalide");
    }
    noteLoueurRepository.delete(note.getId());
  }

  public void supprimerNoteVehicule(NoteVehicule note) {
    if (note == null || note.getId() == null) {
      throw new IllegalArgumentException("Note invalide");
    }
    noteVehiculeRepository.delete(note.getId());
  }
}
