package fr.univ.m1.projetagile.commentaire.persistence;

import java.util.List;
import java.util.Optional;
import fr.univ.m1.projetagile.commentaire.entity.Commentaire;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CommentaireRepository {

  private final EntityManager em;

  public CommentaireRepository(EntityManager em) {
    this.em = em;
  }

  public Commentaire save(Commentaire commentaire) {
    if (commentaire.getId() == null) {
      em.persist(commentaire);
      return commentaire;
    } else {
      return em.merge(commentaire);
    }
  }

  public Optional<Commentaire> findById(Long id) {
    return Optional.ofNullable(em.find(Commentaire.class, id));
  }

  public List<Commentaire> findByProfilId(Long profilId) {
    TypedQuery<Commentaire> query = em.createQuery(
        "SELECT c FROM Commentaire c WHERE c.profilId = :profilId AND c.reponseId IS NULL ORDER BY c.dateCreation DESC",
        Commentaire.class);
    query.setParameter("profilId", profilId);
    return query.getResultList();
  }

  public List<Commentaire> findReponsesByCommentaireId(Long commentaireId) {
    TypedQuery<Commentaire> query = em.createQuery(
        "SELECT c FROM Commentaire c WHERE c.reponseId = :commentaireId ORDER BY c.dateCreation ASC",
        Commentaire.class);
    query.setParameter("commentaireId", commentaireId);
    return query.getResultList();
  }

  public Optional<Commentaire> findByAuteurAndProfil(Long auteurId, Long profilId) {
    TypedQuery<Commentaire> query = em.createQuery(
        "SELECT c FROM Commentaire c WHERE c.auteurId = :auteurId AND c.profilId = :profilId",
        Commentaire.class);
    query.setParameter("auteurId", auteurId);
    query.setParameter("profilId", profilId);
    List<Commentaire> results = query.getResultList();
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  public double getMoyenneNotes(Long profilId) {
    TypedQuery<Double> query = em.createQuery(
        "SELECT AVG(c.note) FROM Commentaire c WHERE c.profilId = :profilId AND c.note > 0",
        Double.class);
    query.setParameter("profilId", profilId);
    Double moyenne = query.getSingleResult();
    return moyenne != null ? moyenne : 0.0;
  }

  public long countCommentaires(Long profilId) {
    TypedQuery<Long> query = em.createQuery(
        "SELECT COUNT(c) FROM Commentaire c WHERE c.profilId = :profilId AND c.reponseId IS NULL",
        Long.class);
    query.setParameter("profilId", profilId);
    return query.getSingleResult();
  }

  public void delete(Commentaire commentaire) {
    if (em.contains(commentaire)) {
      em.remove(commentaire);
    } else {
      em.remove(em.merge(commentaire));
    }
  }
}
