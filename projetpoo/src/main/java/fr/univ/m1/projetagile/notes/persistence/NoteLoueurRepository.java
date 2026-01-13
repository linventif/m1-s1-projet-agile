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

      // Recharger l'agent et le loueur pour avoir des instances gérées
      var agentManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Agent.class, note.getAgent().getIdU());
      var loueurManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, note.getLoueur().getIdU());

      // Sauvegarder les critères originaux
      List<fr.univ.m1.projetagile.notes.entity.Critere> criteresOriginaux =
          new ArrayList<>(note.getCriteres());

      // Créer une nouvelle note SANS critères d'abord
      NoteLoueur noteToSave = new NoteLoueur(agentManaged, loueurManaged, new ArrayList<>());
      em.persist(noteToSave);
      em.flush(); // Force l'insertion de la note pour obtenir son ID

      // Maintenant ajouter les critères un par un
      for (fr.univ.m1.projetagile.notes.entity.Critere critere : criteresOriginaux) {
        fr.univ.m1.projetagile.notes.entity.Critere critereManaged;
        if (critere.getId() == null) {
          em.persist(critere);
          em.flush();
          critereManaged = critere;
        } else {
          critereManaged =
              em.find(fr.univ.m1.projetagile.notes.entity.Critere.class, critere.getId());
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
