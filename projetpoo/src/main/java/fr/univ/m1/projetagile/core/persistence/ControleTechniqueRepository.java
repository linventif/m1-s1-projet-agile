package fr.univ.m1.projetagile.core.persistence;

import java.util.List;
import fr.univ.m1.projetagile.core.entity.ControleTechnique;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;

public class ControleTechniqueRepository {

  private EntityManager em;

  public ControleTechniqueRepository(EntityManager em) {
    this.em = em;
  }

  public void save(ControleTechnique ct) {
    em.getTransaction().begin();
    em.persist(ct);
    em.getTransaction().commit();
  }

  public List<ControleTechnique> findByVehicule(Vehicule vehicule) {
    return em.createQuery("SELECT c FROM ControleTechnique c WHERE c.vehicule = :vehicule",
        ControleTechnique.class).setParameter("vehicule", vehicule).getResultList();
  }
}
