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

      // Recharger tous les critères depuis la base pour avoir des instances gérées
      List<fr.univ.m1.projetagile.notes.entity.Critere> criteresManagedList =
          new java.util.ArrayList<>();
      for (fr.univ.m1.projetagile.notes.entity.Critere critere : note.getCriteres()) {
        if (critere.getId() != null) {
          // Les critères doivent déjà exister en base
          fr.univ.m1.projetagile.notes.entity.Critere managed =
              em.find(fr.univ.m1.projetagile.notes.entity.Critere.class, critere.getId());
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

      // Créer une NOUVELLE instance de NoteVehicule avec les critères gérés
      NoteVehicule noteToSave =
          new NoteVehicule(note.getVehicule(), note.getLoueur(), criteresManagedList);

      // Persister la note
      em.persist(noteToSave);

      // Forcer le flush pour générer l'ID avant la table de jointure
      em.flush();

      transaction.commit();
      return noteToSave;

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
