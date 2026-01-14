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
 *     └── MessagerieService
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
 * List<Message> messages = service.getMessagesUtilisateur(loueur);
 * List<Message> conversation = service.getConversation(loueur, agent);
 * }</pre>
 *
 * @see fr.univ.m1.projetagile.messagerie.entity.Message
 * @see fr.univ.m1.projetagile.messagerie.persistence.MessageRepository
 * @see fr.univ.m1.projetagile.messagerie.service.MessagerieService
 *
 * @since 1.0
 * @version 1.0
 * @author Projet Agile M1
 */
package fr.univ.m1.projetagile.messagerie;
