/**
 * Package de messagerie pour la plateforme de location de véhicules.
 *
 * <p>
 * Ce package fournit un système de messagerie complet permettant aux utilisateurs (Agents et
 * Loueurs) de communiquer entre eux. Il est conçu comme un module indépendant qui s'appuie sur le
 * core métier sans le modifier.
 * </p>
 *
 * <h2>Architecture</h2>
 *
 * <pre>
 * messagerie/
 * ├── entity/           (Entités JPA)
 * │   └── Message
 * ├── persistence/      (Accès aux données)
 * │   └── MessageRepository
 * ├── service/          (Logique métier)
 * │   └── MessagerieService
 * └── Main              (Point d'entrée de démonstration)
 * </pre>
 *
 * <h2>Fonctionnalités principales</h2>
 * <ul>
 * <li>Envoi et réception de messages entre utilisateurs</li>
 * <li>Sauvegarde automatique des messages</li>
 * <li>Récupération de l'historique des messages</li>
 * <li>Gestion des conversations entre deux utilisateurs</li>
 * <li>Validation du contenu des messages</li>
 * </ul>
 *
 * <h2>Utilisation</h2>
 *
 * <pre>{@code
 * // Via le service (recommandé)
 * MessagerieService service = new MessagerieService();
 * Message msg = service.envoyerMessage(loueur, agent, "Bonjour!");
 *
 * // Via les méthodes de Utilisateur
 * Message msg2 = utilisateur.envoyerMessage(destinataire, "Bonjour!");
 * List<Message> messages = utilisateur.getMessages();
 * List<Message> conversation = utilisateur.getConversationAvec(autreUtilisateur);
 * }</pre>
 *
 * @see fr.univ.m1.projetagile.messagerie.entity.Message
 * @see fr.univ.m1.projetagile.messagerie.persistence.MessageRepository
 * @see fr.univ.m1.projetagile.messagerie.service.MessagerieService
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur#envoyerMessage(fr.univ.m1.projetagile.core.entity.Utilisateur,
 *      String)
 * @see fr.univ.m1.projetagile.core.entity.Utilisateur#getMessages()
 *
 * @since 1.0
 * @version 1.0
 * @author Projet Agile M1
 */
package fr.univ.m1.projetagile.messagerie;
