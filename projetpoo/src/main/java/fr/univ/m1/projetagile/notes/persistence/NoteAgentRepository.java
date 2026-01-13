package fr.univ.m1.projetagile.notes.persistence;

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

      // Recharger tous les critères depuis la base pour avoir des instances gérées
      List<Critere> criteresManagedList = new java.util.ArrayList<>();
      for (Critere critere : note.getCriteres()) {
        if (critere.getId() != null) {
          // Les critères doivent déjà exister en base
          Critere managed = em.find(Critere.class, critere.getId());
          if (managed == null) {
            throw new IllegalStateException(
                "Le critère avec l'ID " + critere.getId() + " n'existe pas en base");
          }
          criteresManagedList.add(managed);
        } else {
          throw new IllegalStateException(
              "Tous les critères doivent être persistés avant de créer une note");
        }
      }

      // Remplacer les critères par les instances gérées
      note.setCriteres(criteresManagedList);

      // Persister ou fusionner la note
      if (note.getId() == null) {
        em.persist(note);
      } else {
        note = em.merge(note);
      }

      transaction.commit();
      return note;

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
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<Double> query = em.createQuery(
        "SELECT AVG((n.note1 + n.note2 + n.note3) / 3.0) FROM NoteAgent n WHERE n.agent.idU = :agentId",
        Double.class);
    query.setParameter("agentId", agentId);
    Double result = query.getSingleResult();
    return result != null ? Math.round(result * 100.0) / 100.0 : 0.0;
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
