


package fr.univ.m1.projetagile.core.persistence;

import java.time.LocalDate;
import java.util.List;
import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.RappelControleTechnique;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;

public class RappelControleTechniqueRepository {

    private final EntityManager em;

    public RappelControleTechniqueRepository(EntityManager em) {
        this.em = em;
    }

    // =========================
    // CRUD de base
    // =========================

    public void save(RappelControleTechnique rappel) {
        em.getTransaction().begin();
        em.persist(rappel);
        em.getTransaction().commit();
    }

    public void update(RappelControleTechnique rappel) {
        em.getTransaction().begin();
        em.merge(rappel);
        em.getTransaction().commit();
    }

    public RappelControleTechnique findById(Long id) {
        return em.find(RappelControleTechnique.class, id);
    }

    // =========================
    // Méthodes métier
    // =========================

    /**
     * Vérifie si un rappel existe déjà pour un véhicule et un agent
     */
    public boolean existeRappel(Vehicule vehicule, Agent agent) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM RappelControleTechnique r " +
                "WHERE r.vehicule = :vehicule AND r.agent = :agent",
                Long.class
        )
        .setParameter("vehicule", vehicule)
        .setParameter("agent", agent)
        .getSingleResult();

        return count > 0;
    }

    /**
     * Retourne les rappels non envoyés dont la date est atteinte
     */
    public List<RappelControleTechnique> findRappelsNonEnvoyes(LocalDate dateCourante) {
        return em.createQuery(
                "SELECT r FROM RappelControleTechnique r " +
                "WHERE r.envoye = false AND r.dateRappel <= :date",
                RappelControleTechnique.class
        )
        .setParameter("date", dateCourante)
        .getResultList();
    }

    /**
     * Supprimer un rappel (optionnel, mais propre)
     */
    public void delete(RappelControleTechnique rappel) {
        em.getTransaction().begin();
        em.remove(em.contains(rappel) ? rappel : em.merge(rappel));
        em.getTransaction().commit();
    }
}
