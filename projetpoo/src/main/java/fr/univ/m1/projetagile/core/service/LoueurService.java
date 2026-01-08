package fr.univ.m1.projetagile.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.LocationDTO;
import fr.univ.m1.projetagile.core.dto.LoueurDTO;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.LoueurRepository;
import fr.univ.m1.projetagile.enums.StatutLocation;

/**
 * Service métier pour la gestion des loueurs
 * Fournit des méthodes pour créer, récupérer et gérer les loueurs
 */
public class LoueurService extends UtilisateurService<Loueur, LoueurRepository> {

  private VehiculeService vehiculeService;

  public LoueurService(LoueurRepository loueurRepository) {
    super(loueurRepository);
  }

  public LoueurService(LoueurRepository loueurRepository, VehiculeService vehiculeService) {
    super(loueurRepository);
    this.vehiculeService = vehiculeService;
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
   * Récupère le profil complet d'un loueur sous forme de DTO
   * Sépare les locations courantes (non terminées) et l'historique (terminées)
   * Exclut les locations annulées et trie par date décroissante
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
    dto.setNoteMoyenne(loueur.calculerNote());

    // Récupérer les locations depuis le repository avec eager loading
    // (exclut déjà les locations annulées et trie par date décroissante)
    List<Location> locations = repository.findLocationsByLoueurId(loueur.getIdU());

    // Séparer les locations courantes et terminées
    List<LocationDTO> currentLocations = new ArrayList<>();
    List<LocationDTO> oldLocations = new ArrayList<>();

    for (Location location : locations) {
      LocationDTO locationDTO = convertLocationToDTO(location);
      
      if (location.getStatut() == StatutLocation.TERMINE) {
        oldLocations.add(locationDTO);
      } else {
        // Tous les autres statuts sont considérés comme "courants"
        // (EN_ATTENTE_D_ACCEPTATION_PAR_L_AGENT, ACCEPTE, EN_COURS, etc.)
        currentLocations.add(locationDTO);
      }
    }

    dto.setCurrentLocations(currentLocations);
    dto.setOldLocations(oldLocations);

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
    dto.setPrixTotal(location.getPrixLocation());

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
      vehiculeDTO.setNoteMoyenne(vehicule.calculerNote());

      dto.setVehicule(vehiculeDTO);
    }

    return dto;
  }
}
