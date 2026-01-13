package fr.univ.m1.projetagile.core.entity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.commentaire.service.CommentaireService;
import fr.univ.m1.projetagile.core.dto.ProfilInfo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_utilisateur", discriminatorType = DiscriminatorType.STRING)
public abstract class Utilisateur {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idU")
  protected Long idU;

  @Column(nullable = false, unique = true)
  protected String email;

  @Column(nullable = false, name = "motdePasse")
  protected String motDePasse;

  @Column(name = "nom", length = 100)
  protected String nom;

  @Column(name = "prenom", length = 100)
  protected String prenom;

  @Column(name = "bio", length = 500)
  protected String bio;

  @Column(name = "telephone", length = 20)
  protected String telephone;

  @Column(name = "adresse", length = 200)
  protected String adresse;

  // Constructeur sans argument pour JPA
  protected Utilisateur() {}

  protected Utilisateur(String email, String motDePasse) {
    this.email = email;
    this.motDePasse = motDePasse;
  }

  protected Utilisateur(Long idU, String email, String motDePasse) {
    this.idU = idU;
    this.email = email;
    this.motDePasse = motDePasse;
  }

  // Getters
  public Long getIdU() {
    return idU;
  }

  public String getEmail() {
    return email;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  // Setters
  public void setIdU(Long idU) {
    this.idU = idU;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public boolean verifierMotDePasse(String mdp) {
    return Objects.equals(this.motDePasse, mdp);
  }

  // Méthodes selon UML
  public void contacterAgent(Agent agent) {
    // À implémenter avec le système de messagerie
    // TODO: implémenter la logique de contact avec un agent
  }

  public void contacterLoueur(Loueur loueur) {
    // À implémenter avec le système de messagerie
    // TODO: implémenter la logique de contact avec un loueur
  }

  /**
   * Envoie un message à un autre utilisateur avec validation et sauvegarde automatiques.
   *
   * @param destinataire l'utilisateur destinataire
   * @param contenu le contenu du message
   * @return le message envoyé et sauvegardé
   */
  public fr.univ.m1.projetagile.messagerie.entity.Message envoyerMessage(Utilisateur destinataire,
      String contenu) {
    fr.univ.m1.projetagile.messagerie.service.MessagerieService service =
        new fr.univ.m1.projetagile.messagerie.service.MessagerieService();
    return service.envoyerMessage(this, destinataire, contenu);
  }

  /**
   * Récupère la conversation avec un autre utilisateur.
   *
   * @param autreUtilisateur l'autre utilisateur
   * @return la liste des messages échangés dans l'ordre chronologique
   */
  public List<fr.univ.m1.projetagile.messagerie.entity.Message> getConversationAvec(
      Utilisateur autreUtilisateur) {
    fr.univ.m1.projetagile.messagerie.service.MessagerieService service =
        new fr.univ.m1.projetagile.messagerie.service.MessagerieService();
    return service.getConversation(this, autreUtilisateur);
  }

  /**
   * Récupère tous les messages de cet utilisateur (envoyés et reçus)
   *
   * @return la liste des messages
   */
  public List<fr.univ.m1.projetagile.messagerie.entity.Message> getMessages() {
    fr.univ.m1.projetagile.messagerie.persistence.MessageRepository messageRepository =
        new fr.univ.m1.projetagile.messagerie.persistence.MessageRepository();
    return messageRepository.findAllMessagesByUser(this);
  }

  /**
   * Récupère les messages envoyés par cet utilisateur
   *
   * @return la liste des messages envoyés
   */
  public List<fr.univ.m1.projetagile.messagerie.entity.Message> getMessagesSent() {
    fr.univ.m1.projetagile.messagerie.persistence.MessageRepository messageRepository =
        new fr.univ.m1.projetagile.messagerie.persistence.MessageRepository();
    return messageRepository.findMessagesSentBy(this);
  }

  /**
   * Récupère les messages reçus par cet utilisateur
   *
   * @return la liste des messages reçus
   */
  public List<fr.univ.m1.projetagile.messagerie.entity.Message> getMessagesReceived() {
    fr.univ.m1.projetagile.messagerie.persistence.MessageRepository messageRepository =
        new fr.univ.m1.projetagile.messagerie.persistence.MessageRepository();
    return messageRepository.findMessagesReceivedBy(this);
  }

  public boolean seConnecter(String email, String motDePasse) {
    // Vérifie les identifiants et connecte l'utilisateur
    return this.email.equals(email) && verifierMotDePasse(motDePasse);
  }

  public void changerMDP(String ancienMDP, String nouveauMDP) {
    // Change le mot de passe si l'ancien est correct
    if (verifierMotDePasse(ancienMDP)) {
      this.motDePasse = nouveauMDP;
    } else {
      throw new IllegalArgumentException("Ancien mot de passe incorrect");
    }
  }

  public void changerEmail(String nouveauEmail) {
    // Change l'email de l'utilisateur
    if (nouveauEmail != null && !nouveauEmail.trim().isEmpty()) {
      this.email = nouveauEmail;
    }
  }

  /**
   * Récupérer les informations du profil
   */
  public ProfilInfo getProfil(EntityManager em) {
    ProfilInfo profil = new ProfilInfo();
    profil.setIdUtilisateur(this.idU);
    profil.setNom(this.nom);
    profil.setPrenom(this.prenom);
    profil.setEmail(this.email);
    profil.setTelephone(this.telephone);
    profil.setAdresse(this.adresse);
    profil.setBio(this.bio);

    // Récupérer le nom commercial si c'est un Agent
    if (this instanceof Agent) {
      Agent agent = (Agent) this;
      profil.setNomCommercial(agent.getNomCommercial());

      // Récupérer les véhicules disponibles
      List<Vehicule> vehiculesDisponibles =
          agent.getVehicules().stream().filter(Vehicule::isDisponible).collect(Collectors.toList());
      profil.setVehiculesDisponibles(vehiculesDisponibles);
    }

    // Récupérer les commentaires
    if (em != null) {
      CommentaireService commentaireService = new CommentaireService(em);
      profil.setCommentaires(commentaireService.getCommentairesProfil(this.idU));
      profil.setMoyenneNotes(commentaireService.getMoyenneNotes(this.idU));
      profil.setNombreCommentaires(commentaireService.countCommentaires(this.idU));
    }

    return profil;
  }

  /**
   * Modifier les informations du profil
   */
  public void modifierProfil(String nom, String prenom, String telephone, String adresse,
      String bio) {
    if (nom != null && !nom.trim().isEmpty()) {
      this.nom = nom;
    }
    if (prenom != null && !prenom.trim().isEmpty()) {
      this.prenom = prenom;
    }
    if (telephone != null) {
      this.telephone = telephone;
    }
    if (adresse != null) {
      this.adresse = adresse;
    }
    if (bio != null) {
      this.bio = bio;
    }
  }

  // Getters et setters pour les nouveaux champs
  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [id=" + idU + ", email=" + email + "]";
  }
}
