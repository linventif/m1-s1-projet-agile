package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Entretien;
import fr.univ.m1.projetagile.core.entity.EntretienVehicule;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;

public class EntretienVehiculeRepository {

  private final EntityManager em;

  public EntretienVehiculeRepository(EntityManager em) {
    this.em = em;
  }

  /**
   * Sauvegarde un entretien de véhicule (création ou mise à jour).
   */
  public EntretienVehicule save(EntretienVehicule ev) {
    if (ev.getId() == null) {
      em.persist(ev);
      return ev;
    } else {
      return em.merge(ev);
    }
  }

  public EntretienVehicule findById(Long id) {
    return em.find(EntretienVehicule.class, id);
  }

  public Vehicule findVehiculeById(Long vehiculeId) {
    return em.find(Vehicule.class, vehiculeId);
  }

  public Entretien findEntretienById(Long entretienId) {
    return em.find(Entretien.class, entretienId);
  }

  /**
   * Tous les entretiens d'un véhicule (non triés).
   */
  public List<EntretienVehicule> findByVehicule(Long vehiculeId) {
    return em.createQuery("SELECT ev FROM EntretienVehicule ev " + "WHERE ev.vehicule.id = :id",
        EntretienVehicule.class).setParameter("id", vehiculeId).getResultList();
  }

  /**
   * Dernier entretien (par date_entretien) pour un véhicule, ou null si aucun.
   */
  public EntretienVehicule findDernierEntretienByVehicule(Long vehiculeId) {
    return em
        .createQuery("SELECT ev FROM EntretienVehicule ev " + "WHERE ev.vehicule.id = :id "
            + "ORDER BY ev.dateEntretien DESC", EntretienVehicule.class)
        .setParameter("id", vehiculeId).setMaxResults(1).getResultStream().findFirst().orElse(null);
  }

  /**
   * Entretiens d'un véhicule entre deux dates (optionnel, pour filtres).
   */
  public List<EntretienVehicule> findByVehiculeAndDateBetween(Long vehiculeId, LocalDate debut,
      LocalDate fin) {
    return em
        .createQuery("SELECT ev FROM EntretienVehicule ev " + "WHERE ev.vehicule.id = :id "
            + "AND ev.dateEntretien >= :debut " + "AND ev.dateEntretien <= :fin "
            + "ORDER BY ev.dateEntretien DESC", EntretienVehicule.class)
        .setParameter("id", vehiculeId).setParameter("debut", debut).setParameter("fin", fin)
        .getResultList();
  }

  public void delete(EntretienVehicule ev) {
    EntretienVehicule managed = ev;
    if (!em.contains(ev)) {
      managed = em.merge(ev);
    }
    em.remove(managed);
  }
}
