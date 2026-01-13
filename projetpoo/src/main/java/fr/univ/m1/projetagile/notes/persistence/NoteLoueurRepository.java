package fr.univ.m1.projetagile.notes.persistence;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.notes.entity.NoteLoueur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des notes de loueurs.
 */
public class NoteLoueurRepository {

  public NoteLoueur save(NoteLoueur note) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      // Recharger tous les critères depuis la base pour avoir des instances gérées
      List<fr.univ.m1.projetagile.notes.entity.Critere> criteresManagedList = new ArrayList<>();
      for (fr.univ.m1.projetagile.notes.entity.Critere critere : note.getCriteres()) {
        if (critere.getId() == null) {
          throw new IllegalStateException(
              "Tous les critères doivent être persistés avant de créer une note");
        }
        fr.univ.m1.projetagile.notes.entity.Critere managed =
            em.find(fr.univ.m1.projetagile.notes.entity.Critere.class, critere.getId());
        if (managed == null) {
          throw new IllegalStateException(
              "Le critère avec l'ID " + critere.getId() + " n'existe pas en base");
        }
        criteresManagedList.add(managed);
      }

      // 1) Persister la note sans critères pour générer l'ID
      NoteLoueur noteToSave = new NoteLoueur(note.getAgent(), note.getLoueur(), new ArrayList<>());
      em.persist(noteToSave);
      em.flush();

      // 2) Attacher les critères gérés et merger
      noteToSave.getCriteres().addAll(criteresManagedList);
      noteToSave = em.merge(noteToSave);
      em.flush();

      transaction.commit();
      return noteToSave;

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de la note loueur", e);
    }
  }

  public NoteLoueur findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    return em.find(NoteLoueur.class, id);
  }

  public List<NoteLoueur> findByLoueurId(Long loueurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteLoueur> query = em.createQuery(
        "SELECT n FROM NoteLoueur n WHERE n.loueur.idU = :loueurId ORDER BY n.date DESC",
        NoteLoueur.class);
    query.setParameter("loueurId", loueurId);
    return query.getResultList();
  }

  public List<NoteLoueur> findByAgentId(Long agentId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteLoueur> query = em.createQuery(
        "SELECT n FROM NoteLoueur n WHERE n.agent.idU = :agentId ORDER BY n.date DESC",
        NoteLoueur.class);
    query.setParameter("agentId", agentId);
    return query.getResultList();
  }

  public Double getMoyenneByLoueurId(Long loueurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<Double> query = em.createQuery(
        "SELECT AVG((n.note1 + n.note2 + n.note3) / 3.0) FROM NoteLoueur n WHERE n.loueur.idU = :loueurId",
        Double.class);
    query.setParameter("loueurId", loueurId);
    Double result = query.getSingleResult();
    return result != null ? Math.round(result * 100.0) / 100.0 : 0.0;
  }

  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      NoteLoueur note = em.find(NoteLoueur.class, id);
      if (note != null) {
        em.remove(note);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la note loueur", e);
    }
  }
}
