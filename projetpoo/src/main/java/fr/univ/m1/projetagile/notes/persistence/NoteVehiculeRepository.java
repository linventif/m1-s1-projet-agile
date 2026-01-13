package fr.univ.m1.projetagile.notes.persistence;

import java.util.ArrayList;
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

      // Recharger le loueur et le véhicule pour avoir des instances gérées
      var loueurManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Loueur.class, note.getLoueur().getIdU());
      var vehiculeManaged =
          em.find(fr.univ.m1.projetagile.core.entity.Vehicule.class, note.getVehicule().getId());

      // Sauvegarder les critères originaux
      List<fr.univ.m1.projetagile.notes.entity.Critere> criteresOriginaux =
          new ArrayList<>(note.getCriteres());

      // Créer une nouvelle note SANS critères d'abord
      NoteVehicule noteToSave = new NoteVehicule(vehiculeManaged, loueurManaged, new ArrayList<>());
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
      throw new RuntimeException("Erreur lors de l'enregistrement de la note vehicule", e);
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
