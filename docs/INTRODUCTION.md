# Mini‑documentation — Architecture du projet

## 🎯 Objectif du document

Ce document a pour but d’expliquer **l’architecture du projet**, le rôle de chaque dossier, et **le fonctionnement de JPA (Hibernate)** avec des exemples simples.

Il est destiné à toute l’équipe afin que chacun sache :

-   où placer son code
-   comment fonctionne la persistance des données
-   comment ajouter de nouvelles entités proprement

---

## 🧱 Vue d’ensemble de l’architecture

### 🧠 Principe fondamental

Le **core est la base du logiciel**.

Il contient **tout ce qui est indispensable au fonctionnement du système**, indépendamment des fonctionnalités annexes :

-   le modèle métier (véhicules, agents, locations, etc.)
-   les règles de base du domaine
-   la persistance des données

👉 Le core **ne dépend de rien d’autre**.
👉 Les autres fonctionnalités **dépendent du core**, jamais l’inverse.

---

### 🧩 Architecture modulaire (objectif du projet)

L’objectif du projet est de pouvoir **ajouter de nouvelles fonctionnalités sous forme de packages (ou modules) indépendants**, qui viennent **se brancher sur le core sans le modifier**.

Chaque fonctionnalité avancée est :

-   isolée dans son propre package
-   indépendante des autres fonctionnalités
-   remplaçable ou supprimable sans casser le core

Exemples de fonctionnalités modulaires :

-   messagerie
-   notation
-   génération de PDF
-   assurances
-   entreprises d’entretien

---

### 📦 Exemple : fonctionnalité de messagerie

La **messagerie n’appartient pas au core**.

Elle utilise le core (utilisateurs, agents, loueurs), mais **le core n’a aucune connaissance de la messagerie**.

Structure logique visée :

```
fr.univ.m1.projetagile
├── core
│   └── domain
│       ├── Vehicule
│       ├── Agent
│       └── Loueur
├── messaging
│   ├── Message
│   ├── Conversation
│   └── MessagingService
```

👉 Si on supprime entièrement le package `messaging` :

-   le core continue de fonctionner
-   la plateforme reste cohérente

---

Le projet est organisé autour d’un **core métier**. Tout ce qui définit le fonctionnement de la plateforme (véhicules, agents, locations, etc.) vit dans ce core.

```
fr.univ.m1.projetagile
└── core
    ├── Main.java
    └── domain
        └── Vehicule.java
```

> 💡 Le mot _core_ signifie ici **cœur métier**, pas un simple dossier technique.

---

## 📦 Rôle des dossiers

### `core/`

Contient le **cœur du projet** :

-   le point d’entrée temporaire (`Main.java`)
-   les entités métier
-   plus tard : services, persistance, règles métier

### `core/domain/`

Contient les **entités métier**, c’est‑à‑dire les objets principaux du système.

Exemples d’entités métier :

-   `Vehicule`
-   `Agent`
-   `Loueur`
-   `Location`
-   `Assurance`

👉 Une entité métier représente un concept réel du sujet.

---

## 🚗 Exemple : l’entité `Vehicule`

Fichier :

```
core/domain/Vehicule.java
```

```java
@Entity
@Table(name = "vehicules")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;     // voiture, moto, camion
    private String marque;   // Peugeot, Mercedes
    private String modele;
    private String couleur;

    protected Vehicule() {}

    public Vehicule(String type, String marque, String modele, String couleur) {
        this.type = type;
        this.marque = marque;
        this.modele = modele;
        this.couleur = couleur;
    }
}
```

---

## 🗄️ Qu’est‑ce que JPA ?

**JPA (Jakarta Persistence API)** est une norme Java permettant de :

-   transformer des objets Java en tables SQL
-   éviter d’écrire du SQL à la main
-   travailler avec des objets plutôt qu’avec des lignes

Dans notre projet :

-   JPA est utilisé via **Hibernate**
-   PostgreSQL est la base de données

---

## 🔄 Comment fonctionne JPA (schéma simple)

```
Objet Java (Vehicule)
        ↓
     JPA / Hibernate
        ↓
Table SQL (vehicules)
```

Quand on fait :

```java
em.persist(vehicule);
```

➡️ JPA génère automatiquement un `INSERT INTO vehicules ...`

---

## ⚙️ Configuration JPA

La configuration se fait dans :

```
src/main/resources/META-INF/persistence.xml
```

Ce fichier indique :

-   quelle base de données utiliser
-   quel driver
-   quelles entités sont gérées

Extrait important :

```xml
<property name="hibernate.hbm2ddl.auto" value="update"/>
```

👉 Cela signifie :

-   les tables sont créées automatiquement
-   le schéma évolue avec les entités

---

## ▶️ Exemple simple d’utilisation dans `Main`

```java
EntityManagerFactory emf =
    Persistence.createEntityManagerFactory("default");
EntityManager em = emf.createEntityManager();

em.getTransaction().begin();

Vehicule v = new Vehicule("voiture", "Peugeot", "208", "bleu");
em.persist(v);

em.getTransaction().commit();
```

➡️ Résultat : un véhicule est ajouté en base de données.

---

## ➕ Ajouter une nouvelle entité (méthode à suivre)

Pour ajouter une nouvelle entité (ex : `Agent`) :

1. Créer la classe dans `core/domain`
2. Annoter avec `@Entity`
3. Ajouter les attributs métier
4. (Optionnel) lister la classe dans `persistence.xml`

Structure type :

```java
@Entity
public class Agent {
    @Id
    @GeneratedValue
    private Long id;
}
```

---

## 🧭 Règles importantes à respecter

-   ❌ Pas de SQL dans les entités
-   ❌ Pas de logique complexe dans `Main`
-   ✅ Une classe = un concept métier
-   ✅ Tout ce qui est métier vit dans le core

---

## 🚀 Évolutions prévues

À terme, le projet évoluera vers :

```
core
├── domain        (entités métier)
├── service       (logique applicative)
├── persistence   (accès base de données)
└── api / cli     (points d’entrée)
```

---

## 📌 Conclusion

-   Le **core** contient le cœur métier du projet
-   Les **entités JPA** modélisent le domaine
-   JPA permet de persister les objets sans SQL
-   L’architecture est pensée pour être **lisible, évolutive et notée correctement**

👉 Ce document sert de référence commune pour toute l’équipe.
