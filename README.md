# Lm3alem

## Description
Lm3alem est une application Android moderne qui met en relation les clients avec les ouvriers et artisans locaux. L'application facilite la recherche de services professionnels et permet une gestion simplifiée des demandes de travaux.

## Technologies & Librairies
- **Langage**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase (Authentication, Firestore)
- **Injection de dépendances**: Hilt
- **Stockage Local**: DataStore
- **Navigation**: Navigation Compose
- **Multilingue**: Support pour le Français (par défaut), l'Anglais et l'Arabe (RTL supporté)

## Fonctionnalités

### 👤 Client
- **Authentification**: Création de compte et connexion (Email/Mot de passe et Google Sign-In).
- **Recherche**: Trouver un artisan par métier ou description.
- **Filtrage**: Affiner les résultats par ville.
- **Profil Artisan**: Consulter les détails, l'expérience, le prix et les avis.
- **Demandes**: Envoyer des demandes de service détaillées.
- **Avis**: Noter et commenter les prestations des artisans.

### 🛠️ Artisan
- **Profil Professionnel**: Créer et modifier sa bio, son métier, ses années d'expérience et ses tarifs.
- **Gestion des demandes**: Recevoir, accepter ou refuser les demandes de service des clients.
- **Tableau de bord**: Suivre le statut des travaux (En attente, Accepté, Terminé).
- **Réputation**: Consulter sa note moyenne et les commentaires laissés par les clients.

## Système de Design (Material 3)
L'application utilise un système de design personnalisé avec des composants réutilisables :
- `MainButton`: Boutons stylisés avec états de chargement.
- `AppTextField`: Champs de saisie standardisés avec validation.
- `ArtisanCard` & `RequestCard`: Affichage élégant des données.
- `AppTopBar`: Navigation et titres cohérents.

## Installation
1. Cloner le projet : `git clone https://github.com/OuzzikiLhoussaine/Projet-Android-Lm3alem.git`
2. Ouvrir avec **Android Studio (Ladybug ou version ultérieure)**.
3. Ajouter votre fichier `google-services.json` dans le répertoire `app/`.
4. Synchroniser le projet avec Gradle.
5. Lancer l'application sur un émulateur ou un appareil physique.
