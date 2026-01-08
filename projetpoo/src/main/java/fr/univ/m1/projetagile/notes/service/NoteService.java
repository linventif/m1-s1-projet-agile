package fr.univ.m1.projetagile.notes.service;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Location;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import fr.univ.m1.projetagile.enums.StatutLocation;
import fr.univ.m1.projetagile.notes.NoteA;
import fr.univ.m1.projetagile.notes.NoteL;
import fr.univ.m1.projetagile.notes.NoteV;
import jakarta.persistence.EntityManager;

public class NoteService {

  private EntityManager em;

  // 1️⃣ Constructeur : EntityManager injecté
  public NoteService(EntityManager em) {
    this.em = em;
  }

  // 2️⃣ Loueur note un Agent (générique)
  public NoteA noterAgent(Loueur loueur, Agent agent, double n1, double n2, double n3) {

    NoteA note = NoteA.create();
    note.setLoueur(loueur);
    note.setAgent(agent);
    note.setNote1(n1);
    note.setNote2(n2);
    note.setNote3(n3);

    try {
      em.getTransaction().begin();
      em.persist(note);
      em.getTransaction().commit();
      return note;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    }
  }

  // 3️⃣ Agent note un Loueur (générique)
  public NoteL noterLoueur(Agent agent, Loueur loueur, double n1, double n2, double n3) {

    NoteL note = NoteL.create();
    note.setAgent(agent);
    note.setLoueur(loueur);
    note.setNote1(n1);
    note.setNote2(n2);
    note.setNote3(n3);

    try {
      em.getTransaction().begin();
      em.persist(note);
      em.getTransaction().commit();
      return note;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    }
  }

  // 4️⃣ Loueur note un Véhicule (générique)
  public NoteV noterVehicule(Loueur loueur, Vehicule vehicule, double n1, double n2, double n3) {

    NoteV note = NoteV.create();
    note.setLoueur(loueur);
    note.setVehicule(vehicule);
    note.setNote1(n1);
    note.setNote2(n2);
    note.setNote3(n3);

    try {
      em.getTransaction().begin();
      em.persist(note);
      em.getTransaction().commit();
      return note;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    }
  }

  // 5️⃣ ✅ NOTATION LIÉE À UNE LOCATION TERMINÉE (fonctionnalité demandée)
  public void noterLocationTerminee(Location location, double noteAgent1, double noteAgent2,
      double noteAgent3, double noteVehicule1, double noteVehicule2, double noteVehicule3) {

    if (location == null) {
      throw new IllegalArgumentException("La location est obligatoire.");
    }

    if (location.getStatut() != StatutLocation.TERMINE) {
      throw new IllegalStateException("La notation n'est possible que pour une location terminée.");
    }

    Loueur loueur = location.getLoueur();
    Vehicule vehicule = location.getVehicule();
    Agent agent = vehicule.getProprietaire();

    if (loueur == null || vehicule == null || agent == null) {
      throw new IllegalStateException("Données de location incomplètes.");
    }

    NoteA noteAgent = NoteA.create();
    noteAgent.setLoueur(loueur);
    noteAgent.setAgent(agent);
    noteAgent.setNote1(noteAgent1);
    noteAgent.setNote2(noteAgent2);
    noteAgent.setNote3(noteAgent3);

    NoteV noteVehicule = NoteV.create();
    noteVehicule.setLoueur(loueur);
    noteVehicule.setVehicule(vehicule);
    noteVehicule.setNote1(noteVehicule1);
    noteVehicule.setNote2(noteVehicule2);
    noteVehicule.setNote3(noteVehicule3);

    try {
      em.getTransaction().begin();
      em.persist(noteAgent);
      em.persist(noteVehicule);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    }
  }
}
