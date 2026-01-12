package fr.univ.m1.projetagile.notes.service;

import java.util.Arrays;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.notes.entity.Critere;
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
 * sauvegarde en base de données. Supporte les critères d'évaluation personnalisables.
 * </p>
 *
 * @author Projet Agile M1
 * @version 2.0
 * @since 1.0
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
   * Permet à un loueur de noter un agent avec une liste de critères personnalisés.
   *
   * @param loueur le loueur qui note
   * @param agent l'agent noté
   * @param criteres la liste des critères d'évaluation
   * @return la note créée et sauvegardée
   * @throws IllegalArgumentException si les paramètres sont invalides
   */
  public NoteAgent noterAgent(Loueur loueur, Agent agent, List<Critere> criteres) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null");
    }

    NoteAgent note = new NoteAgent(agent, loueur, criteres);
    return noteAgentRepository.save(note);
  }

  /**
   * Permet à un loueur de noter un agent avec 3 notes (compatibilité ancienne version).
   *
   * @param loueur le loueur qui note
   * @param agent l'agent noté
   * @param note1 première note (entre 0 et 10)
   * @param note2 deuxième note (entre 0 et 10)
   * @param note3 troisième note (entre 0 et 10)
   * @return la note créée et sauvegardée
   */
  public NoteAgent noterAgent(Loueur loueur, Agent agent, Double note1, Double note2,
      Double note3) {
    List<Critere> criteres = Arrays.asList(new Critere("Critère 1", note1),
        new Critere("Critère 2", note2), new Critere("Critère 3", note3));
    return noterAgent(loueur, agent, criteres);
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
   * Permet à un agent de noter un loueur avec une liste de critères personnalisés.
   *
   * @param agent l'agent qui note
   * @param loueur le loueur noté
   * @param criteres la liste des critères d'évaluation
   * @return la note créée et sauvegardée
   * @throws IllegalArgumentException si les paramètres sont invalides
   */
  public NoteLoueur noterLoueur(Agent agent, Loueur loueur, List<Critere> criteres) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null");
    }
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }

    NoteLoueur note = new NoteLoueur(agent, loueur, criteres);
    return noteLoueurRepository.save(note);
  }

  /**
   * Permet à un agent de noter un loueur avec 3 notes (compatibilité ancienne version).
   *
   * @param agent l'agent qui note
   * @param loueur le loueur noté
   * @param note1 première note (entre 0 et 10)
   * @param note2 deuxième note (entre 0 et 10)
   * @param note3 troisième note (entre 0 et 10)
   * @return la note créée et sauvegardée
   */
  public NoteLoueur noterLoueur(Agent agent, Loueur loueur, Double note1, Double note2,
      Double note3) {
    List<Critere> criteres = Arrays.asList(new Critere("Critère 1", note1),
        new Critere("Critère 2", note2), new Critere("Critère 3", note3));
    return noterLoueur(agent, loueur, criteres);
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
   * Permet à un loueur de noter un véhicule avec une liste de critères personnalisés.
   *
   * @param loueur le loueur qui note
   * @param vehicule le véhicule noté
   * @param criteres la liste des critères d'évaluation
   * @return la note créée et sauvegardée
   * @throws IllegalArgumentException si les paramètres sont invalides
   */
  public NoteVehicule noterVehicule(Loueur loueur, Vehicule vehicule, List<Critere> criteres) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être null");
    }
    if (vehicule == null) {
      throw new IllegalArgumentException("Le véhicule ne peut pas être null");
    }

    NoteVehicule note = new NoteVehicule(vehicule, loueur, criteres);
    return noteVehiculeRepository.save(note);
  }

  /**
   * Permet à un loueur de noter un véhicule avec 3 notes (compatibilité ancienne version).
   *
   * @param loueur le loueur qui note
   * @param vehicule le véhicule noté
   * @param note1 première note (entre 0 et 10)
   * @param note2 deuxième note (entre 0 et 10)
   * @param note3 troisième note (entre 0 et 10)
   * @return la note créée et sauvegardée
   */
  public NoteVehicule noterVehicule(Loueur loueur, Vehicule vehicule, Double note1, Double note2,
      Double note3) {
    List<Critere> criteres = Arrays.asList(new Critere("Critère 1", note1),
        new Critere("Critère 2", note2), new Critere("Critère 3", note3));
    return noterVehicule(loueur, vehicule, criteres);
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
