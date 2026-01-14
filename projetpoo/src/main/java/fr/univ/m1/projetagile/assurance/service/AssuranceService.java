package fr.univ.m1.projetagile.assurance.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import fr.univ.m1.projetagile.assurance.entity.Assurance;
import fr.univ.m1.projetagile.assurance.entity.GrilleTarif;
import fr.univ.m1.projetagile.assurance.entity.SouscriptionAssurance;
import fr.univ.m1.projetagile.assurance.entity.TarifOptionAssurance;
import fr.univ.m1.projetagile.assurance.entity.TarifVehicule;
import fr.univ.m1.projetagile.assurance.persistence.AssuranceRepository;
import fr.univ.m1.projetagile.assurance.persistence.GrilleTarifRepository;
import fr.univ.m1.projetagile.assurance.persistence.SouscriptionAssuranceRepository;
import fr.univ.m1.projetagile.assurance.persistence.TarifOptionAssuranceRepository;
import fr.univ.m1.projetagile.assurance.persistence.TarifVehiculeRepository;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.enums.TypeV;

public class AssuranceService {

  private final SouscriptionAssuranceRepository souscriptionAssuranceRepository;
  private final GrilleTarifRepository grilleTarifRepository;
  private final TarifVehiculeRepository tarifVehiculeRepository;
  private final TarifOptionAssuranceRepository tarifOptionAssuranceRepository;
  private final AssuranceRepository assuranceRepository;

  /**
   * Constructeur avec injection de tous les repositories.
   */
  public AssuranceService(SouscriptionAssuranceRepository souscriptionAssuranceRepository,
      GrilleTarifRepository grilleTarifRepository, TarifVehiculeRepository tarifVehiculeRepository,
      TarifOptionAssuranceRepository tarifOptionAssuranceRepository,
      AssuranceRepository assuranceRepository) {
    this.souscriptionAssuranceRepository = souscriptionAssuranceRepository;
    this.grilleTarifRepository = grilleTarifRepository;
    this.tarifVehiculeRepository = tarifVehiculeRepository;
    this.tarifOptionAssuranceRepository = tarifOptionAssuranceRepository;
    this.assuranceRepository = assuranceRepository;
  }

  /**
   * Constructeur par défaut qui crée des instances de tous les repositories.
   */
  public AssuranceService() {
    this.souscriptionAssuranceRepository = new SouscriptionAssuranceRepository();
    this.grilleTarifRepository = new GrilleTarifRepository();
    this.tarifVehiculeRepository = new TarifVehiculeRepository();
    this.tarifOptionAssuranceRepository = new TarifOptionAssuranceRepository();
    this.assuranceRepository = new AssuranceRepository();
  }

  /**
   * Crée une GrilleTarif vide et la persiste en base de données.
   *
   * @return la grille tarifaire sauvegardée
   */
  public GrilleTarif creerGrille() {
    GrilleTarif grille = new GrilleTarif();
    return grilleTarifRepository.save(grille);
  }

  /**
   * Ajoute un tarif véhicule dans la grille et persiste les modifications. IMPORTANT: TarifVehicule
   * a un @ManyToOne grilleTarif nullable=false donc on crée le TarifVehicule avec la grille.
   *
   * @param grille la grille tarifaire à laquelle ajouter le tarif
   * @param type le type de véhicule
   * @param modele le modèle de véhicule
   * @param prixParJour le prix par jour
   * @return le tarif véhicule sauvegardé
   */
  public TarifVehicule ajouterTarifVehicule(GrilleTarif grille, TypeV type, String modele,
      double prixParJour) {
    if (grille == null)
      throw new IllegalArgumentException("grille null");
    if (type == null)
      throw new IllegalArgumentException("type null");
    if (modele == null || modele.isBlank())
      throw new IllegalArgumentException("modele vide");
    if (prixParJour < 0)
      throw new IllegalArgumentException("prixParJour < 0");

    TarifVehicule tv = new TarifVehicule(type, modele, prixParJour, grille);
    grille.ajouterTarifVehicule(tv);

    // Persister le tarif véhicule et mettre à jour la grille
    TarifVehicule tarifSauvegarde = tarifVehiculeRepository.save(tv);
    grilleTarifRepository.save(grille);

    return tarifSauvegarde;
  }

  /**
   * Ajoute un tarif option dans la grille et persiste les modifications.
   *
   * @param grille la grille tarifaire à laquelle ajouter l'option
   * @param nomOption le nom de l'option
   * @param description la description de l'option
   * @param prixParJour le prix par jour
   * @return le tarif d'option sauvegardé
   */
  public TarifOptionAssurance ajouterTarifOption(GrilleTarif grille, String nomOption,
      String description, double prixParJour) {
    if (grille == null)
      throw new IllegalArgumentException("grille null");
    if (nomOption == null || nomOption.isBlank())
      throw new IllegalArgumentException("nomOption vide");
    if (prixParJour < 0)
      throw new IllegalArgumentException("prixParJour < 0");

    TarifOptionAssurance to = new TarifOptionAssurance(nomOption, description, prixParJour, grille);
    grille.ajouterTarifOption(to);

    // Persister le tarif option et mettre à jour la grille
    TarifOptionAssurance tarifSauvegarde = tarifOptionAssuranceRepository.save(to);
    grilleTarifRepository.save(grille);

    return tarifSauvegarde;
  }

  /**
   * Crée une assurance liée à une grille et la persiste en base de données.
   *
   * @param nom le nom de l'assurance
   * @param grille la grille tarifaire associée
   * @return l'assurance sauvegardée
   */
  public Assurance creerAssurance(String nom, GrilleTarif grille) {
    if (nom == null || nom.isBlank())
      throw new IllegalArgumentException("nom vide");
    if (grille == null)
      throw new IllegalArgumentException("grille null");

    Assurance assurance = new Assurance(nom, grille);

    // Ajouter l'assurance à la grille pour maintenir la cohérence bidirectionnelle
    grille.ajouterAssurance(assurance);

    // Persister l'assurance et mettre à jour la grille
    Assurance assuranceSauvegardee = assuranceRepository.save(assurance);
    grilleTarifRepository.save(grille);

    return assuranceSauvegardee;
  }

  /**
   * Souscrire une assurance pour une location, avec une liste d'options (noms). Sauvegarde la
   * souscription en base de données et retourne l'objet SouscriptionAssurance.
   *
   * @param location la location pour laquelle souscrire l'assurance
   * @param assurance l'assurance à souscrire
   * @param options liste des noms d'options (peut être null ou vide)
   * @return la souscription d'assurance sauvegardée
   */
  public SouscriptionAssurance souscrire(Location location, Assurance assurance,
      List<String> options) {
    if (location == null)
      throw new IllegalArgumentException("location null");
    if (assurance == null)
      throw new IllegalArgumentException("assurance null");

    // Créer la souscription
    SouscriptionAssurance souscription =
        SouscriptionAssurance.souscrire(location, assurance, options);

    // Persister en base de données
    return souscriptionAssuranceRepository.save(souscription);
  }

  /**
   * Calculer le prix assurance TOTAL pour une location donnée, avec une assurance + options. (tarif
   * véhicule/jour + options/jour) * nbJours
   *
   * NOTE : nbJours = max(1, DAYS entre dateDebut et dateFin)
   */
  public double calculerPrixAssurance(Location location, Assurance assurance,
      List<String> options) {
    if (location == null)
      throw new IllegalArgumentException("location null");
    if (assurance == null || assurance.getGrille() == null) {
      throw new IllegalArgumentException("assurance/grille manquante");
    }
    if (location.getVehicule() == null) {
      throw new IllegalArgumentException("vehicule manquant dans la location");
    }

    int nbJours = calculerNbJours(location.getDateDebut(), location.getDateFin());

    TypeV type = location.getVehicule().getType();
    String modele = location.getVehicule().getModele();

    TarifVehicule tv = assurance.getGrille().trouverTarifVehicule(type, modele);
    if (tv == null) {
      throw new IllegalArgumentException(
          "Aucun tarif véhicule trouvé pour type=" + type + ", modele=" + modele);
    }

    double prixParJour = tv.getPrix();

    if (options != null) {
      for (String opt : options) {
        if (opt == null || opt.isBlank())
          continue;

        TarifOptionAssurance to = assurance.getGrille().trouverTarifOption(opt);
        if (to == null) {
          throw new IllegalArgumentException("Option inconnue dans la grille: " + opt);
        }
        prixParJour += to.getPrix();
      }
    }

    return prixParJour * nbJours;
  }

  /**
   * Récupère la souscription d'assurance pour une location donnée.
   *
   * @param locationId l'identifiant de la location
   * @return la souscription d'assurance ou null si aucune souscription n'existe pour cette location
   */
  public SouscriptionAssurance getSouscriptionParLocation(Long locationId) {
    if (locationId == null) {
      throw new IllegalArgumentException("locationId null");
    }

    List<SouscriptionAssurance> souscriptions =
        souscriptionAssuranceRepository.findByLocationId(locationId);

    if (souscriptions.isEmpty()) {
      return null;
    }

    // Normalement, il ne devrait y avoir qu'une seule souscription par location
    return souscriptions.get(0);
  }

  /**
   * Calcul du nb de jours à partir des LocalDateTime. - si la différence est 0 (même jour), on
   * considère 1 jour (pour éviter prix=0)
   */
  public int calculerNbJours(LocalDateTime debut, LocalDateTime fin) {
    if (debut == null || fin == null)
      throw new IllegalArgumentException("dates null");
    if (!fin.isAfter(debut))
      throw new IllegalArgumentException("dateFin doit être après dateDebut");

    long days = ChronoUnit.DAYS.between(debut, fin);
    if (days <= 0)
      return 1;
    return (int) days;
  }
}
