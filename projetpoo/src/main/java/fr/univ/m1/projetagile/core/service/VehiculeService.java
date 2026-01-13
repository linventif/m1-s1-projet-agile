package fr.univ.m1.projetagile.core.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import fr.univ.m1.projetagile.core.dto.VehiculeDTO;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.core.persistence.VehiculeRepository;
import fr.univ.m1.projetagile.enums.TypeV;

/**
 * Service métier responsable de la gestion des véhicules.
 * <p>
 * Cette classe constitue la couche de service autour du {@link VehiculeRepository} et des entités
 * {@link Vehicule}. Elle fournit des opérations pour :
 * <ul>
 * <li>Créer de nouveaux véhicules ;</li>
 * <li>Consulter la liste des véhicules avec leurs infos enrichies (US.L.1) ;</li>
 * <li>Consulter le détail d'un véhicule (US.L.1) ;</li>
 * <li>Rechercher des véhicules selon ville/type (US.L.1) ;</li>
 * <li>Rechercher des véhicules disponibles sur une période (US.L.1) ;</li>
 * <li>Filtrer les disponibilités selon les réservations existantes.</li>
 * </ul>
 */
public class VehiculeService {

  private VehiculeRepository vehiculeRepository;

  public VehiculeService(VehiculeRepository vehiculeRepository) {
    this.vehiculeRepository = vehiculeRepository;
  }

  /**
   * US.L.1 - Récupère tous les véhicules avec leurs informations enrichies.
   *
   * @return Liste de VehiculeDTO
   */
  public List<VehiculeDTO> getVehicules() {
    List<Vehicule> vehicules = vehiculeRepository.findAll();
    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  /**
   * US.L.1 - Consulter le détail d'un véhicule à partir de son id.
   *
   * @param id identifiant du véhicule
   * @return VehiculeDTO correspondant
   */
  public VehiculeDTO getVehiculeById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    Vehicule v = vehiculeRepository.findById(id);
    if (v == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'id " + id);
    }
    return convertToDTO(v);
  }

  /**
   * US.L.1 - Rechercher des véhicules selon ville et/ou type. (filtrage simple)
   *
   * @param ville ville (optionnel, null/blank = pas de filtre)
   * @param type type véhicule (optionnel)
   * @return liste de VehiculeDTO
   */
  public List<VehiculeDTO> rechercherVehicules(String ville, TypeV type) {
    List<Vehicule> vehicules = vehiculeRepository.findAll();

    return vehicules.stream()
        .filter(v -> ville == null || ville.isBlank() || v.getVille().equalsIgnoreCase(ville))
        .filter(v -> type == null || v.getType() == type).map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * US.L.1 - Rechercher des véhicules disponibles sur une période. On conserve seulement les
   * véhicules qui ont AU MOINS une disponibilité filtrée couvrant totalement [dateDebut, dateFin].
   *
   * @param ville ville (optionnel)
   * @param type type véhicule (optionnel)
   * @param dateDebut début demandé (obligatoire)
   * @param dateFin fin demandée (obligatoire)
   * @return liste de VehiculeDTO disponibles
   */
  public List<VehiculeDTO> rechercherVehiculesDisponibles(String ville, TypeV type,
      LocalDate dateDebut, LocalDate dateFin) {

    if (dateDebut == null || dateFin == null) {
      throw new IllegalArgumentException("dateDebut et dateFin sont obligatoires.");
    }
    if (dateFin.isBefore(dateDebut)) {
      throw new IllegalArgumentException("dateFin ne peut pas être avant dateDebut.");
    }

    List<VehiculeDTO> tous = rechercherVehicules(ville, type);

    return tous.stream()
        .filter(dto -> dto.getDatesDispo() != null && dto.getDatesDispo().stream()
            .anyMatch(d -> d != null && d.length >= 2
                && (d[0].isBefore(dateDebut) || d[0].isEqual(dateDebut))
                && (d[1].isAfter(dateFin) || d[1].isEqual(dateFin))))
        .collect(Collectors.toList());
  }

  /**
   * Création d'un véhicule (utile côté agent).
   */
  public Vehicule createVehicule(TypeV type, String marque, String modele, String couleur,
      String ville, Double prixJ, Agent proprietaire) {

    if (type == null) {
      throw new IllegalArgumentException("Le type de véhicule ne peut pas être nul.");
    }
    if (marque == null || marque.trim().isEmpty()) {
      throw new IllegalArgumentException("La marque du véhicule ne peut pas être vide.");
    }
    if (modele == null || modele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle du véhicule ne peut pas être vide.");
    }
    if (couleur == null || couleur.trim().isEmpty()) {
      throw new IllegalArgumentException("La couleur du véhicule ne peut pas être vide.");
    }
    if (ville == null || ville.trim().isEmpty()) {
      throw new IllegalArgumentException("La ville du véhicule ne peut pas être vide.");
    }
    if (prixJ == null || prixJ.doubleValue() <= 0.0) {
      throw new IllegalArgumentException("Le prix journalier doit être strictement positif.");
    }
    if (proprietaire == null) {
      throw new IllegalArgumentException("Le propriétaire du véhicule ne peut pas être nul.");
    }

    Vehicule vehicule = new Vehicule(type, marque, modele, couleur, ville, prixJ, proprietaire);
    return vehiculeRepository.save(vehicule);
  }

  /**
   * Recherche des véhicules avec plusieurs filtres.
   *
   * @param dateDebut date de début pour la disponibilité (optionnel)
   * @param dateFin date de fin pour la disponibilité (optionnel)
   * @param ville ville (optionnel)
   * @param marque marque (optionnel)
   * @param modele modèle (optionnel)
   * @param couleur couleur (optionnel)
   * @param prixMin prix minimum (optionnel)
   * @param prixMax prix maximum (optionnel)
   * @param type type de véhicule (optionnel)
   * @return liste de VehiculeDTO correspondant aux critères
   */
  public List<VehiculeDTO> searchVehiculesWithFilters(LocalDate dateDebut, LocalDate dateFin,
      String ville, String marque, String modele, String couleur, Double prixMin, Double prixMax,
      TypeV type) {
    List<Vehicule> vehicules = vehiculeRepository.findWithFilters(dateDebut, dateFin, ville, marque,
        modele, couleur, prixMin, prixMax, type);
    return vehicules.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  /**
   * Récupère un véhicule par son ID (retourne l'entité Vehicule, pas un DTO).
   *
   * @param id identifiant du véhicule
   * @return Vehicule correspondant
   */
  public Vehicule findVehiculeById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    Vehicule v = vehiculeRepository.findById(id);
    if (v == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'id " + id);
    }
    return v;
  }

  /**
   * Met à jour la marque d'un véhicule. Vérifie que l'agent est bien le propriétaire.
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouvelleMarque la nouvelle marque
   */
  public void updateVehiculeMarque(Agent agent, Long vehiculeId, String nouvelleMarque) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null.");
    }
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    if (nouvelleMarque == null || nouvelleMarque.trim().isEmpty()) {
      throw new IllegalArgumentException("La marque ne peut pas être vide.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'id " + vehiculeId);
    }

    if (!agent.equals(vehicule.getProprietaire())) {
      throw new IllegalArgumentException("L'agent n'est pas le propriétaire de ce véhicule.");
    }

    vehicule.setMarque(nouvelleMarque);
    vehiculeRepository.save(vehicule);
  }

  /**
   * Met à jour le modèle d'un véhicule. Vérifie que l'agent est bien le propriétaire.
   *
   * @param agent l'agent propriétaire
   * @param vehiculeId l'identifiant du véhicule
   * @param nouveauModele le nouveau modèle
   */
  public void updateVehiculeModele(Agent agent, Long vehiculeId, String nouveauModele) {
    if (agent == null) {
      throw new IllegalArgumentException("L'agent ne peut pas être null.");
    }
    if (vehiculeId == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    if (nouveauModele == null || nouveauModele.trim().isEmpty()) {
      throw new IllegalArgumentException("Le modèle ne peut pas être vide.");
    }

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
    if (vehicule == null) {
      throw new IllegalArgumentException("Aucun véhicule trouvé avec l'id " + vehiculeId);
    }

    if (!agent.equals(vehicule.getProprietaire())) {
      throw new IllegalArgumentException("L'agent n'est pas le propriétaire de ce véhicule.");
    }

    vehicule.setModele(nouveauModele);
    vehiculeRepository.save(vehicule);
  }

  /**
   * Supprime un véhicule.
   *
   * @param id l'identifiant du véhicule à supprimer
   */
  public void deleteVehicule(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("L'id du véhicule ne peut pas être nul.");
    }
    vehiculeRepository.delete(id);
  }

  /**
   * Convertit un Vehicule en VehiculeDTO.
   */
  private VehiculeDTO convertToDTO(Vehicule v) {
    VehiculeDTO dto = new VehiculeDTO();
    dto.setId(v.getId());
    dto.setType(v.getType());
    dto.setMarque(v.getMarque());
    dto.setModele(v.getModele());
    dto.setCouleur(v.getCouleur());
    dto.setVille(v.getVille());
    dto.setPrixJ(v.getPrixJ());
    dto.setDisponible(v.isDisponible());
    dto.setDatesDispo(v.getDatesDispo());
    return dto;
  }
}
