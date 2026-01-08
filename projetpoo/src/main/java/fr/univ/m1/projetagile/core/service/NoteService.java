package fr.univ.m1.projetagile.core.Service;

import fr.univ.m1.projetagile.core.entity.Agent;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.NoteA;
import fr.univ.m1.projetagile.core.entity.NoteL;
import fr.univ.m1.projetagile.core.entity.NoteV;
import fr.univ.m1.projetagile.core.entity.Vehicule;
import jakarta.persistence.EntityManager;

public class NoteService {

  private EntityManager em;

  // 1️⃣ 构造函数：把 EntityManager 传进来
  public NoteService(EntityManager em) {
    this.em = em;
  }

  // 2️⃣ Loueur note pour Agent
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
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw e;
    }
    return note;
  }

  // 3️⃣ Agent note pour Loueur
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
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw e;
    }
    return note;
  }

  // 4️⃣ Loueur note pour Vehicule
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
    } catch (Exception e) {
      em.getTransaction().rollback();
      throw e;
    }
    return note;
  }

}
