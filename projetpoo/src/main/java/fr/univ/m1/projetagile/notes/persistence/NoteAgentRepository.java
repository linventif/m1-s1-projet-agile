package fr.univ.m1.projetagile.notes.persistence;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.notes.entity.Critere;
import fr.univ.m1.projetagile.notes.entity.NoteAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des notes d'agents.
 */
public class NoteAgentRepository {

  public NoteAgent save(NoteAgent note) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Recharger l'agent et le loueur pour avoir des instances gérées
      var agentManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Agent.class, note.getAgent().getIdU());
      var loueurManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, note.getLoueur().getIdU());

      // Sauvegarder les critères originaux
      List<Critere> criteresOriginaux = new ArrayList<>(note.getCriteres());

      // Créer une nouvelle note SANS critères d'abord
      NoteAgent noteToSave = new NoteAgent(agentManaged, loueurManaged, new ArrayList<>());
      em.persist(noteToSave);
      em.flush(); // Force l'insertion de la note pour obtenir son ID

      // Maintenant ajouter les critères un par un
      for (Critere critere : criteresOriginaux) {
        Critere critereManaged;
        if (critere.getId() == null) {
          // Persister le nouveau critère
          em.persist(critere);
          em.flush();
          critereManaged = critere;
        } else {
          // Récupérer le critère existant
          critereManaged = em.find(Critere.class, critere.getId());
          if (critereManaged == null) {
            throw new IllegalStateException(
                "Le critère avec l'ID " + critere.getId() + " n'existe pas en base");
          }
        }
        noteToSave.ajouterCritere(critereManaged);
      }

      em.flush();
      transaction.commit();
      return noteToSave;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la note agent", e);
    }
  }

  public NoteAgent findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    return em.find(NoteAgent.class, id);
  }

  public List<NoteAgent> findByAgentId(Long agentId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteAgent> query = em.createQuery(
        "SELECT n FROM NoteAgent n WHERE n.agent.idU = :agentId ORDER BY n.date DESC",
        NoteAgent.class);
    query.setParameter("agentId", agentId);
    return query.getResultList();
  }

  public List<NoteAgent> findByLoueurId(Long loueurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteAgent> query = em.createQuery(
        "SELECT n FROM NoteAgent n WHERE n.loueur.idU = :loueurId ORDER BY n.date DESC",
        NoteAgent.class);
    query.setParameter("loueurId", loueurId);
    return query.getResultList();
  }

  public Double getMoyenneByAgentId(Long agentId) {
    List<NoteAgent> notes = findByAgentId(agentId);
    if (notes.isEmpty()) {
      return 0.0;
    }
    double somme = notes.stream().mapToDouble(NoteAgent::getNoteMoyenne).sum();
    double moyenne = somme / notes.size();
    return Math.round(moyenne * 100.0) / 100.0;
  }

  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      NoteAgent note = em.find(NoteAgent.class, id);
      if (note != null) {
        em.remove(note);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la note agent", e);
    }
  }
}
