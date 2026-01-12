package fr.univ.m1.projetagile.notes.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.DatabaseConnection;
import fr.univ.m1.projetagile.notes.entity.NoteVehicule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Repository pour gérer la persistance des notes de véhicules.
 */
public class NoteVehiculeRepository {

  public NoteVehicule save(NoteVehicule note) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

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
      throw new RuntimeException("Erreur lors de l'enregistrement de la note véhicule", e);
    }
  }

  public NoteVehicule findById(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    return em.find(NoteVehicule.class, id);
  }

  public List<NoteVehicule> findByVehiculeId(Long vehiculeId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteVehicule> query = em.createQuery(
        "SELECT n FROM NoteVehicule n WHERE n.vehicule.id = :vehiculeId ORDER BY n.date DESC",
        NoteVehicule.class);
    query.setParameter("vehiculeId", vehiculeId);
    return query.getResultList();
  }

  public List<NoteVehicule> findByLoueurId(Long loueurId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<NoteVehicule> query = em.createQuery(
        "SELECT n FROM NoteVehicule n WHERE n.loueur.idU = :loueurId ORDER BY n.date DESC",
        NoteVehicule.class);
    query.setParameter("loueurId", loueurId);
    return query.getResultList();
  }

  public Double getMoyenneByVehiculeId(Long vehiculeId) {
    EntityManager em = DatabaseConnection.getEntityManager();
    TypedQuery<Double> query = em.createQuery(
        "SELECT AVG((n.note1 + n.note2 + n.note3) / 3.0) FROM NoteVehicule n WHERE n.vehicule.id = :vehiculeId",
        Double.class);
    query.setParameter("vehiculeId", vehiculeId);
    Double result = query.getSingleResult();
    return result != null ? Math.round(result * 100.0) / 100.0 : 0.0;
  }

  public void delete(Long id) {
    EntityManager em = DatabaseConnection.getEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();

      NoteVehicule note = em.find(NoteVehicule.class, id);
      if (note != null) {
        em.remove(note);
      }

      transaction.commit();

    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de la note véhicule", e);
    }
  }
}
