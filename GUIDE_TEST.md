# Guide de test - Enregistrement des utilisateurs

## 📋 Prérequis

1. **Base de données Oracle** : Assurez-vous que la base de données Oracle est accessible
2. **Configuration** : Vérifiez que le fichier `persistence.xml` est correctement configuré
3. **Dependencies Maven** : Toutes les dépendances doivent être installées

## 🚀 Méthodes pour tester

### **Méthode 1 : Exécuter Main.java (Recommandée)**

#### Via IDE (IntelliJ IDEA, Eclipse, VS Code)
1. Ouvrez le fichier `projetpoo/src/main/java/fr/univ/m1/projetagile/core/Main.java`
2. Clic droit sur la classe `Main`
3. Sélectionnez **"Run Main.main()"** ou **"Run As > Java Application"**

#### Via ligne de commande (Maven)
```bash
# Naviguer vers le dossier du projet
cd projetpoo

# Compiler le projet
mvn compile

# Exécuter la classe Main
mvn exec:java -Dexec.mainClass="fr.univ.m1.projetagile.core.Main"
```

#### Via ligne de commande (Java direct)
```bash
cd projetpoo

# Compiler
javac -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  src/main/java/fr/univ/m1/projetagile/core/Main.java

# Exécuter
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  fr.univ.m1.projetagile.core.Main
```

### **Méthode 2 : Créer un test personnalisé**

Créez une nouvelle classe de test dans votre projet :

```java
package fr.univ.m1.projetagile.core;

import fr.univ.m1.projetagile.core.service.UtilisateurService;
import jakarta.persistence.EntityManager;

public class TestUtilisateurs {
  public static void main(String[] args) {
    EntityManager em = null;

    try {
      DatabaseConnection.init();
      em = DatabaseConnection.getEntityManager();

      UtilisateurService service = new UtilisateurService(em);

      // Test création d'un Agent Particulier
      var agent = service.creerAgentParticulier(
          "Test", "Jean", "test@example.com", "mdp123", "0123456789");
      System.out.println("Agent créé avec ID: " + agent.getIdU());

      // Test recherche
      var user = service.trouverParEmail("test@example.com");
      System.out.println("Utilisateur trouvé: " + user.getEmail());

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (em != null) em.close();
      DatabaseConnection.close();
    }
  }
}
```

## ✅ Résultat attendu

Si tout fonctionne correctement, vous devriez voir :

```
✓ DB connectée

=== Enregistrement des utilisateurs ===

--- Méthode 1 : Utilisation directe ---
✓ Agent Particulier créé : Dupont Jean
✓ Agent Professionnel créé : AutoLoc Pro (SIRET: 12345678901234)
✓ Loueur créé : Martin Sophie
✓ Tous les utilisateurs ont été enregistrés avec succès !

IDs générés :
  - Agent Particulier ID: 1
  - Agent Professionnel ID: 2
  - Loueur ID: 3

--- Méthode 2 : Utilisation du service ---
✓ Agent Particulier créé (service) : Dupont Marie (ID: 4)
✓ Loueur créé (service) : Bernard Pierre (ID: 5)

=== Vérification des utilisateurs enregistrés ===

📋 Agents Particuliers (2) :
  - ID: 1 | Dupont Jean | Email: jean.dupont@example.com
  - ID: 4 | Dupont Marie | Email: marie.dupont@example.com

📋 Agents Professionnels (1) :
  - ID: 2 | AutoLoc Pro | SIRET: 12345678901234 | Email: contact@autoloc.com

📋 Loueurs (2) :
  - ID: 3 | Martin Sophie | Email: sophie.martin@example.com
  - ID: 5 | Bernard Pierre | Email: pierre.bernard@example.com

🔍 Test de recherche par email :
  ✓ Utilisateur trouvé : jean.dupont@example.com (ID: 1)

=== Liste des tables ===
  - AGENTS
  - AGENTS_PARTICULIERS
  - AGENTS_PROFESSIONNELS
  - LOUEURS
  - ...
```

## 🔍 Vérification dans la base de données

Vous pouvez également vérifier directement dans Oracle :

```sql
-- Voir tous les agents particuliers
SELECT * FROM AGENTS_PARTICULIERS;

-- Voir tous les agents professionnels
SELECT * FROM AGENTS_PROFESSIONNELS;

-- Voir tous les loueurs
SELECT * FROM LOUEURS;

-- Voir la table parente AGENTS
SELECT * FROM AGENTS;
```

## ⚠️ Erreurs possibles

### Erreur de connexion à la base de données
```
✗ Erreur: Unable to acquire JDBC Connection
```
**Solution** : Vérifiez les paramètres dans `persistence.xml` (URL, user, password)

### Erreur de table inexistante
```
✗ Erreur: table or view does not exist
```
**Solution** : Vérifiez que `hibernate.hbm2ddl.auto=update` est activé dans `persistence.xml`

### Erreur de contrainte unique (email)
```
✗ Erreur: unique constraint violated
```
**Solution** : L'email existe déjà. Changez l'email ou supprimez l'utilisateur existant.

## 📝 Notes

- Les tables sont créées automatiquement par Hibernate au premier lancement
- Les IDs sont générés automatiquement par la base de données
- Les emails doivent être uniques (contrainte dans Utilisateur)
- Les transactions sont gérées automatiquement par le service
