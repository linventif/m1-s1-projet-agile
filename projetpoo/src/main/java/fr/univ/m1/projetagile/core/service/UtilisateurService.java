package fr.univ.m1.projetagile.core.service;

import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.AgentProfessionnel;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Service pour gérer les opérations sur les utilisateurs
 */
public class UtilisateurService {

  private EntityManager em;

  public UtilisateurService(EntityManager em) {
    this.em = em;
  }

  /**
   * Enregistre un utilisateur dans la base de données
   *
   * @param utilisateur L'utilisateur à enregistrer
   * @return L'utilisateur avec son ID généré
   */
  public Utilisateur enregistrer(Utilisateur utilisateur) {
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      em.persist(utilisateur);
      transaction.commit();
      return utilisateur;
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur", e);
    }
  }

  /**
   * Crée et enregistre un Agent Particulier
   */
  public AgentParticulier creerAgentParticulier(String nom, String prenom, String email,
      String motDePasse, String telephone) {
    AgentParticulier agent = new AgentParticulier(nom, prenom, email, motDePasse, telephone);
    return (AgentParticulier) enregistrer(agent);
  }

  /**
   * Crée et enregistre un Agent Professionnel
   */
  public AgentProfessionnel creerAgentProfessionnel(String email, String motDePasse, String siret,
      String nom) {
    AgentProfessionnel agent = new AgentProfessionnel(email, motDePasse, siret, nom);
    return (AgentProfessionnel) enregistrer(agent);
  }

  /**
   * Crée et enregistre un Loueur
   */
  public Loueur creerLoueur(String nom, String prenom, String email, String motDePasse) {
    Loueur loueur = new Loueur(nom, prenom, email, motDePasse);
    return (Loueur) enregistrer(loueur);
  }

  /**
   * Trouve un utilisateur par son email Cherche dans AgentParticulier, AgentProfessionnel et Loueur
   */
  public Utilisateur trouverParEmail(String email) {
    // Chercher dans AgentParticulier
    TypedQuery<AgentParticulier> queryAgentPart = em.createQuery(
        "SELECT a FROM AgentParticulier a WHERE a.email = :email", AgentParticulier.class);
    queryAgentPart.setParameter("email", email);
    try {
      return queryAgentPart.getSingleResult();
    } catch (Exception e) {
      // Continue
    }

    // Chercher dans AgentProfessionnel
    TypedQuery<AgentProfessionnel> queryAgentPro = em.createQuery(
        "SELECT a FROM AgentProfessionnel a WHERE a.email = :email", AgentProfessionnel.class);
    queryAgentPro.setParameter("email", email);
    try {
      return queryAgentPro.getSingleResult();
    } catch (Exception e) {
      // Continue
    }

    // Chercher dans Loueur
    TypedQuery<Loueur> queryLoueur =
        em.createQuery("SELECT l FROM Loueur l WHERE l.email = :email", Loueur.class);
    queryLoueur.setParameter("email", email);
    try {
      return queryLoueur.getSingleResult();
    } catch (Exception e) {
      // Aucun utilisateur trouvé
      return null;
    }
  }

  /**
   * Trouve un utilisateur par son ID Cherche dans toutes les tables d'utilisateurs
   */
  public Utilisateur trouverParId(Long id) {
    // Essayer AgentParticulier
    AgentParticulier agentPart = em.find(AgentParticulier.class, id);
    if (agentPart != null)
      return agentPart;

    // Essayer AgentProfessionnel
    AgentProfessionnel agentPro = em.find(AgentProfessionnel.class, id);
    if (agentPro != null)
      return agentPro;

    // Essayer Loueur
    Loueur loueur = em.find(Loueur.class, id);
    if (loueur != null)
      return loueur;

    return null;
  }

  /**
   * Vérifie si un email existe déjà
   */
  public boolean emailExiste(String email) {
    return trouverParEmail(email) != null;
  }

  /**
   * Met à jour un utilisateur
   */
  public Utilisateur mettreAJour(Utilisateur utilisateur) {
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      Utilisateur updated = em.merge(utilisateur);
      transaction.commit();
      return updated;
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
    }
  }

  /**
   * Supprime un utilisateur
   */
  public void supprimer(Utilisateur utilisateur) {
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      em.remove(em.contains(utilisateur) ? utilisateur : em.merge(utilisateur));
      transaction.commit();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
    }
  }
}
