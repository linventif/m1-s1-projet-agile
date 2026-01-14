package fr.univ.m1.projetagile.commentaire.service;

import java.util.List;
import java.util.Optional;
import fr.univ.m1.projetagile.commentaire.entity.Commentaire;
import fr.univ.m1.projetagile.commentaire.persistence.CommentaireRepository;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.EntityManager;

public class CommentaireService {

  private final CommentaireRepository commentaireRepository;
  private final EntityManager em;

  public CommentaireService(EntityManager em) {
    this.em = em;
    this.commentaireRepository = new CommentaireRepository(em);
  }

  /**
   * Poster un commentaire sur un profil
   */
  public Commentaire posterCommentaire(Utilisateur auteur, Utilisateur profil, String contenu,
      int note) {
    if (auteur == null || profil == null) {
      throw new IllegalArgumentException("L'auteur et le profil doivent être définis");
    }

    // Vérifier que l'utilisateur ne commente pas son propre profil
    if (auteur.getIdU().equals(profil.getIdU())) {
      throw new IllegalArgumentException("Impossible de commenter son propre profil");
    }

    // Vérifier qu'un commentaire n'existe pas déjà
    Optional<Commentaire> existant =
        commentaireRepository.findByAuteurAndProfil(auteur.getIdU(), profil.getIdU());
    if (existant.isPresent()) {
      throw new IllegalArgumentException(
          "Vous avez déjà commenté ce profil. Modifiez votre commentaire existant.");
    }

    Commentaire commentaire = new Commentaire(auteur.getIdU(), profil.getIdU(), contenu, note);

    em.getTransaction().begin();
    try {
      commentaireRepository.save(commentaire);
      em.getTransaction().commit();
      return commentaire;
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw new RuntimeException("Erreur lors de la création du commentaire", e);
    }
  }

  /**
   * Répondre à un commentaire (droit de réponse du profil concerné)
   */
  public Commentaire repondreACommentaire(Utilisateur auteur, Long commentaireId, String contenu) {
    if (auteur == null) {
      throw new IllegalArgumentException("L'auteur doit être défini");
    }

    Commentaire commentaireOriginal = commentaireRepository.findById(commentaireId)
        .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

    // Vérifier que c'est bien le propriétaire du profil qui répond
    if (!commentaireOriginal.getProfilId().equals(auteur.getIdU())) {
      throw new IllegalArgumentException(
          "Seul le propriétaire du profil peut répondre à ce commentaire");
    }

    // Vérifier qu'une réponse n'existe pas déjà
    List<Commentaire> reponses = commentaireRepository.findReponsesByCommentaireId(commentaireId);
    if (!reponses.isEmpty()) {
      throw new IllegalArgumentException("Une réponse existe déjà pour ce commentaire");
    }

    Commentaire reponse = new Commentaire(auteur.getIdU(), commentaireId, contenu);

    em.getTransaction().begin();
    try {
      commentaireRepository.save(reponse);
      em.getTransaction().commit();
      return reponse;
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw new RuntimeException("Erreur lors de la création de la réponse", e);
    }
  }

  /**
   * Modifier un commentaire (seulement par son auteur)
   */
  public Commentaire modifierCommentaire(Utilisateur auteur, Long commentaireId,
      String nouveauContenu) {
    if (auteur == null) {
      throw new IllegalArgumentException("L'auteur doit être défini");
    }

    Commentaire commentaire = commentaireRepository.findById(commentaireId)
        .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

    if (!commentaire.getAuteurId().equals(auteur.getIdU())) {
      throw new IllegalArgumentException("Seul l'auteur peut modifier son commentaire");
    }

    em.getTransaction().begin();
    try {
      commentaire.modifierContenu(nouveauContenu);
      commentaireRepository.save(commentaire);
      em.getTransaction().commit();
      return commentaire;
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw new RuntimeException("Erreur lors de la modification du commentaire", e);
    }
  }

  /**
   * Supprimer un commentaire (seulement par son auteur ou le propriétaire du profil)
   */
  public void supprimerCommentaire(Utilisateur utilisateur, Long commentaireId) {
    if (utilisateur == null) {
      throw new IllegalArgumentException("L'utilisateur doit être défini");
    }

    Commentaire commentaire = commentaireRepository.findById(commentaireId)
        .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

    boolean estAuteur = commentaire.getAuteurId().equals(utilisateur.getIdU());
    boolean estProprietaire =
        commentaire.getProfilId() != null && commentaire.getProfilId().equals(utilisateur.getIdU());

    if (!estAuteur && !estProprietaire) {
      throw new IllegalArgumentException(
          "Seul l'auteur ou le propriétaire du profil peut supprimer ce commentaire");
    }

    em.getTransaction().begin();
    try {
      // Supprimer aussi les réponses associées
      if (!commentaire.isReponse()) {
        List<Commentaire> reponses =
            commentaireRepository.findReponsesByCommentaireId(commentaireId);
        for (Commentaire reponse : reponses) {
          commentaireRepository.delete(reponse);
        }
      }
      commentaireRepository.delete(commentaire);
      em.getTransaction().commit();
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw new RuntimeException("Erreur lors de la suppression du commentaire", e);
    }
  }

  /**
   * Récupérer tous les commentaires d'un profil avec leurs réponses
   */
  public List<Commentaire> getCommentairesProfil(Long profilId) {
    return commentaireRepository.findByProfilId(profilId);
  }

  /**
   * Récupérer les réponses d'un commentaire
   */
  public List<Commentaire> getReponses(Long commentaireId) {
    return commentaireRepository.findReponsesByCommentaireId(commentaireId);
  }

  /**
   * Obtenir la moyenne des notes d'un profil
   */
  public double getMoyenneNotes(Long profilId) {
    return commentaireRepository.getMoyenneNotes(profilId);
  }

  /**
   * Compter le nombre de commentaires d'un profil
   */
  public long countCommentaires(Long profilId) {
    return commentaireRepository.countCommentaires(profilId);
  }
}
