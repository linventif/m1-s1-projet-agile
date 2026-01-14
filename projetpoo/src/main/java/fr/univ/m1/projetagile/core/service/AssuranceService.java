package fr.univ.m1.projetagile.core.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Assurance;
import fr.univ.m1.projetagile.core.entity.GrilleTarif;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.SouscriptionAssurance;
import fr.univ.m1.projetagile.core.entity.TarifOptionAssurance;
import fr.univ.m1.projetagile.core.entity.TarifVehicule;
import fr.univ.m1.projetagile.core.persistence.SouscriptionAssuranceRepository;
import fr.univ.m1.projetagile.enums.TypeV;

public class AssuranceService {

  private final SouscriptionAssuranceRepository souscriptionAssuranceRepository;

  /**
   * Constructeur avec injection du repository.
   */
  public AssuranceService(SouscriptionAssuranceRepository souscriptionAssuranceRepository) {
    this.souscriptionAssuranceRepository = souscriptionAssuranceRepository;
  }

  /**
   * Constructeur par défaut qui crée une instance du repository.
   */
  public AssuranceService() {
    this.souscriptionAssuranceRepository = new SouscriptionAssuranceRepository();
  }

  /**
   * Crée une GrilleTarif vide (tu ajoutes ensuite les tarifs).
   */
  public GrilleTarif creerGrille() {
    return new GrilleTarif();
  }

  /**
   * Ajoute un tarif véhicule dans la grille. IMPORTANT: TarifVehicule a un @ManyToOne grilleTarif
   * nullable=false donc on crée le TarifVehicule avec la grille.
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
    return tv;
  }

  /**
   * Ajoute un tarif option dans la grille.
   */
  public TarifOptionAssurance ajouterTarifOption(GrilleTarif grille, String nomOption, String description,
      double prixParJour) {
    if (grille == null)
      throw new IllegalArgumentException("grille null");
    if (nomOption == null || nomOption.isBlank())
      throw new IllegalArgumentException("nomOption vide");
    if (prixParJour < 0)
      throw new IllegalArgumentException("prixParJour < 0");

    TarifOptionAssurance to = new TarifOptionAssurance(nomOption, description, prixParJour, grille);
    grille.ajouterTarifOption(to);
    return to;
  }

  /**
   * Crée une assurance liée à une grille.
   */
  public Assurance creerAssurance(String nom, GrilleTarif grille) {
    if (nom == null || nom.isBlank())
      throw new IllegalArgumentException("nom vide");
    if (grille == null)
      throw new IllegalArgumentException("grille null");

    Assurance assurance = new Assurance(nom, grille);

    // optionnel mais cohérent si tu as GrilleTarif.ajouterAssurance()
    // grille.ajouterAssurance(assurance);

    return assurance;
  }

  /**
   * Souscrire une assurance pour une location, avec une liste d'options (noms). 
   * Sauvegarde la souscription en base de données et retourne l'objet SouscriptionAssurance.
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
    SouscriptionAssurance souscription = SouscriptionAssurance.souscrire(location, assurance, options);

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
