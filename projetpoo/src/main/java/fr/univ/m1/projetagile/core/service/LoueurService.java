package fr.univ.m1.projetagile.core.service;

import java.util.ArrayList;
import java.util.List;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.LoueurDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.notes.service.NoteService;

/**
 * Service métier pour la gestion des loueurs Fournit des méthodes pour créer, récupérer et gérer
 * les loueurs
 */
public class LoueurService extends UtilisateurService<Loueur, LoueurRepository> {

  private final NoteService noteService;

  public LoueurService(LoueurRepository loueurRepository) {
    super(loueurRepository);
    this.noteService = new NoteService();
  }

  public LoueurService(LoueurRepository loueurRepository, NoteService noteService) {
    super(loueurRepository);
    this.noteService = noteService;
  }

  /**
   * Crée un nouveau loueur
   *
   * @param nom le nom du loueur
   * @param prenom le prénom du loueur
   * @param email l'email du loueur
   * @param motDePasse le mot de passe du loueur
   * @return le loueur créé et enregistré
   */
  public Loueur createLoueur(String nom, String prenom, String email, String motDePasse) {

    validateCommonFields(email, motDePasse);

    if (nom == null || nom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom ne peut pas être vide.");
    }
    if (prenom == null || prenom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
    }

    // Vérifier que l'email n'est pas déjà utilisé
    if (repository.findByEmail(email) != null) {
      throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
    }

    Loueur loueur = new Loueur(nom, prenom, email, motDePasse);
    return repository.save(loueur);
  }

  /**
   * Récupère les locations courantes d'un loueur (statut différent de TERMINE) Récupère les
   * locations depuis le repository avec eager loading Exclut les locations annulées et trie par
   * date décroissante
   *
   * @param loueur le loueur dont on veut récupérer les locations courantes
   * @return la liste des locations courantes sous forme de DTOs
   */
  public List<LocationDTO> getCurrentLocationsForLoueur(Loueur loueur) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être nul.");
    }

    // Récupérer les locations depuis le repository avec eager loading
    // (exclut déjà les locations annulées et trie par date décroissante)
    List<Location> locations = repository.findLocationsByLoueurId(loueur.getIdU());

    List<LocationDTO> currentLocations = new ArrayList<>();

    for (Location location : locations) {
      if (location.getStatut() != StatutLocation.TERMINE) {
        // Tous les statuts sauf TERMINE sont considérés comme "courants"
        // (EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT, ACCEPTE, EN_COURS, etc.)
        LocationDTO locationDTO = convertLocationToDTO(location);
        currentLocations.add(locationDTO);
      }
    }

    return currentLocations;
  }

  /**
   * Récupère l'historique des locations d'un loueur (statut TERMINE uniquement) Récupère les
   * locations depuis le repository avec eager loading Exclut les locations annulées et trie par
   * date décroissante
   *
   * @param loueur le loueur dont on veut récupérer l'historique des locations
   * @return la liste des locations terminées sous forme de DTOs
   */
  public List<LocationDTO> getOldLocationsForLoueur(Loueur loueur) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être nul.");
    }

    // Récupérer les locations depuis le repository avec eager loading
    // (exclut déjà les locations annulées et trie par date décroissante)
    List<Location> locations = repository.findLocationsByLoueurId(loueur.getIdU());

    List<LocationDTO> oldLocations = new ArrayList<>();

    for (Location location : locations) {
      if (location.getStatut() == StatutLocation.TERMINE) {
        LocationDTO locationDTO = convertLocationToDTO(location);
        oldLocations.add(locationDTO);
      }
    }

    return oldLocations;
  }

  /**
   * Récupère le profil complet d'un loueur sous forme de DTO Sépare les locations courantes (non
   * terminées) et l'historique (terminées) Exclut les locations annulées et trie par date
   * décroissante
   *
   * @param loueur le loueur dont on veut récupérer le profil
   * @return LoueurDTO contenant toutes les informations du profil et les locations séparées
   */
  public LoueurDTO getLoueurProfile(Loueur loueur) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être nul.");
    }

    LoueurDTO dto = new LoueurDTO();

    // Informations de base
    dto.setIdU(loueur.getIdU());
    dto.setEmail(loueur.getEmail());
    dto.setNom(loueur.getNom());
    dto.setPrenom(loueur.getPrenom());
    dto.setNoteMoyenne(noteService.getMoyenneLoueur(loueur));

    // Récupérer les locations courantes et anciennes en utilisant les méthodes dédiées
    dto.setCurrentLocations(getCurrentLocationsForLoueur(loueur));
    dto.setOldLocations(getOldLocationsForLoueur(loueur));

    return dto;
  }

  /**
   * Convertit une entité Location en LocationDTO
   *
   * @param location l'entité Location à convertir
   * @return le DTO correspondant
   */
  private LocationDTO convertLocationToDTO(Location location) {
    LocationDTO dto = new LocationDTO();

    dto.setId(location.getId());
    dto.setDateDebut(location.getDateDebut());
    dto.setDateFin(location.getDateFin());
    dto.setLieuDepot(location.getLieuDepot());
    dto.setStatut(location.getStatut());

    // Convertir le véhicule en VehiculeDTO
    if (location.getVehicule() != null) {
      Vehicule vehicule = location.getVehicule();
      VehiculeDTO vehiculeDTO = new VehiculeDTO();
      vehiculeDTO.setId(vehicule.getId());
      vehiculeDTO.setType(vehicule.getType());
      vehiculeDTO.setMarque(vehicule.getMarque());
      vehiculeDTO.setModele(vehicule.getModele());
      vehiculeDTO.setCouleur(vehicule.getCouleur());
      vehiculeDTO.setVille(vehicule.getVille());
      vehiculeDTO.setPrixJ(vehicule.getPrixJ());
      vehiculeDTO.setDisponible(vehicule.isDisponible());
      vehiculeDTO.setNoteMoyenne(noteService.getMoyenneVehicule(vehicule));

      dto.setVehicule(vehiculeDTO);
    }

    return dto;
  }

  /**
   * Met à jour le nom d'un loueur
   *
   * @param loueur le loueur à modifier
   * @param nouveauNom le nouveau nom
   * @return le loueur mis à jour
   * @throws IllegalArgumentException si le loueur est nul ou si le nouveau nom est vide
   */
  public Loueur updateLoueurNom(Loueur loueur, String nouveauNom) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être nul.");
    }
    if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nouveau nom ne peut pas être vide.");
    }

    loueur.setNom(nouveauNom);
    return repository.save(loueur);
  }

  /**
   * Met à jour le prénom d'un loueur
   *
   * @param loueur le loueur à modifier
   * @param nouveauPrenom le nouveau prénom
   * @return le loueur mis à jour
   * @throws IllegalArgumentException si le loueur est nul ou si le nouveau prénom est vide
   */
  public Loueur updateLoueurPrenom(Loueur loueur, String nouveauPrenom) {
    if (loueur == null) {
      throw new IllegalArgumentException("Le loueur ne peut pas être nul.");
    }
    if (nouveauPrenom == null || nouveauPrenom.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nouveau prénom ne peut pas être vide.");
    }

    loueur.setPrenom(nouveauPrenom);
    return repository.save(loueur);
  }
}
