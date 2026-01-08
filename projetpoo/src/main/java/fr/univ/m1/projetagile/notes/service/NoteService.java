package fr.univ.m1.projetagile.notes.service;

import java.util.List;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import fr.univ.m1.projetagile.notes.persistence.NoteAgentRepository;
import fr.univ.m1.projetagile.notes.persistence.NoteLoueurRepository;
import fr.univ.m1.projetagile.notes.persistence.NoteVehiculeRepository;

public class NoteService {

  private NoteAgentRepository noteAgentRepository;
  private NoteLoueurRepository noteLoueurRepository;
  private NoteVehiculeRepository noteVehiculeRepository;

  public NoteService(NoteAgentRepository noteAgentRepository,
      NoteLoueurRepository noteLoueurRepository, NoteVehiculeRepository noteVehiculeRepository) {
    this.noteAgentRepository = noteAgentRepository;
    this.noteLoueurRepository = noteLoueurRepository;
    this.noteVehiculeRepository = noteVehiculeRepository;
  }

  // ===== NoteAgent Methods =====

  public NoteAgent noterAgent(Long agentId, Long loueurId, double note1, double note2,
      double note3) {
    if (agentId == null || loueurId == null) {
      throw new IllegalArgumentException("Agent ID et Loueur ID ne peuvent pas être null");
    }

    NoteAgent note = new NoteAgent(agentId, loueurId);
    note.setNote1(note1);
    note.setNote2(note2);
    note.setNote3(note3);

    return noteAgentRepository.save(note);
  }

  public List<NoteAgent> getNotesAgent(Long agentId) {
    if (agentId == null) {
      throw new IllegalArgumentException("Agent ID ne peut pas être null");
    }
    return noteAgentRepository.findByAgentId(agentId);
  }

  public List<NoteAgent> getNotesAgentParLoueur(Long loueurId) {
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    return noteAgentRepository.findByLoueurId(loueurId);
  }

  public Double getMoyenneAgent(Long agentId) {
    if (agentId == null) {
      throw new IllegalArgumentException("Agent ID ne peut pas être null");
    }
    return noteAgentRepository.getMoyenneByAgentId(agentId);
  }

  public void supprimerNoteAgent(Long noteId) {
    if (noteId == null) {
      throw new IllegalArgumentException("Note ID ne peut pas être null");
    }
    noteAgentRepository.delete(noteId);
  }

  // ===== NoteLoueur Methods =====

  public NoteLoueur noterLoueur(Long loueurId, Long agentId, double note1, double note2,
      double note3) {
    if (loueurId == null || agentId == null) {
      throw new IllegalArgumentException("Loueur ID et Agent ID ne peuvent pas être null");
    }

    NoteLoueur note = new NoteLoueur(loueurId, agentId);
    note.setNote1(note1);
    note.setNote2(note2);
    note.setNote3(note3);

    return noteLoueurRepository.save(note);
  }

  public List<NoteLoueur> getNotesLoueur(Long loueurId) {
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    return noteLoueurRepository.findByLoueurId(loueurId);
  }

  public List<NoteLoueur> getNotesLoueurParAgent(Long agentId) {
    if (agentId == null) {
      throw new IllegalArgumentException("Agent ID ne peut pas être null");
    }
    return noteLoueurRepository.findByAgentId(agentId);
  }

  public Double getMoyenneLoueur(Long loueurId) {
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    return noteLoueurRepository.getMoyenneByLoueurId(loueurId);
  }

  public void supprimerNoteLoueur(Long noteId) {
    if (noteId == null) {
      throw new IllegalArgumentException("Note ID ne peut pas être null");
    }
    noteLoueurRepository.delete(noteId);
  }

  // ===== NoteVehicule Methods =====

  public NoteVehicule noterVehicule(Long vehiculeId, Long loueurId, double note1, double note2,
      double note3) {
    if (vehiculeId == null || loueurId == null) {
      throw new IllegalArgumentException("Vehicule ID et Loueur ID ne peuvent pas être null");
    }

    NoteVehicule note = new NoteVehicule(vehiculeId, loueurId);
    note.setNote1(note1);
    note.setNote2(note2);
    note.setNote3(note3);

    return noteVehiculeRepository.save(note);
  }

  public List<NoteVehicule> getNotesVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("Vehicule ID ne peut pas être null");
    }
    return noteVehiculeRepository.findByVehiculeId(vehiculeId);
  }

  public List<NoteVehicule> getNotesVehiculeParLoueur(Long loueurId) {
    if (loueurId == null) {
      throw new IllegalArgumentException("Loueur ID ne peut pas être null");
    }
    return noteVehiculeRepository.findByLoueurId(loueurId);
  }

  public Double getMoyenneVehicule(Long vehiculeId) {
    if (vehiculeId == null) {
      throw new IllegalArgumentException("Vehicule ID ne peut pas être null");
    }
    return noteVehiculeRepository.getMoyenneByVehiculeId(vehiculeId);
  }

  public void supprimerNoteVehicule(Long noteId) {
    if (noteId == null) {
      throw new IllegalArgumentException("Note ID ne peut pas être null");
    }
    noteVehiculeRepository.delete(noteId);
  }
}
